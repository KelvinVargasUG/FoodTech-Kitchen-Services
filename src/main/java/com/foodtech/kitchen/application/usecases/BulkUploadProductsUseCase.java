package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.ChunkAssemblyException;
import com.foodtech.kitchen.application.exepcions.CsvValidationException;
import com.foodtech.kitchen.application.exepcions.FileSizeLimitExceededException;
import com.foodtech.kitchen.application.exepcions.UploadSessionNotFoundException;
import com.foodtech.kitchen.application.ports.in.BulkUploadProductsPort;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.application.ports.out.UploadSessionRepository;
import com.foodtech.kitchen.domain.services.validation.NameValidator;
import com.foodtech.kitchen.domain.services.validation.PriceValidator;
import com.foodtech.kitchen.domain.services.validation.RowLengthValidator;
import com.foodtech.kitchen.domain.services.validation.RowValidator;
import com.foodtech.kitchen.domain.services.validation.StationValidator;
import com.foodtech.kitchen.domain.services.validation.StatusValidator;
import com.foodtech.kitchen.application.usecases.dto.BulkUploadResult;
import com.foodtech.kitchen.domain.model.Product;
import com.foodtech.kitchen.domain.model.ProductStatus;
import com.foodtech.kitchen.domain.model.ProductType;
import com.foodtech.kitchen.domain.model.upload.ErrorRecord;
import com.foodtech.kitchen.domain.model.upload.ProductStaging;
import com.foodtech.kitchen.domain.model.upload.UploadChunk;
import com.foodtech.kitchen.domain.model.upload.UploadSession;
import com.foodtech.kitchen.domain.model.upload.UploadedFile;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BulkUploadProductsUseCase implements BulkUploadProductsPort {

    private static final String[] REQUIRED_HEADERS =
            {"nombre", "precio", "categoria", "estacion", "descripcion", "estado"};
    private static final int BATCH_SIZE = 50;
    private static final long DEFAULT_MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024 * 1024; 

    private final UploadSessionRepository uploadSessionRepository;
    private final ProductRepository productRepository;
    private final String storageBasePath;
    private final long maxFileSizeBytes;
    private final RowValidator rowValidationChain;

    public BulkUploadProductsUseCase(UploadSessionRepository uploadSessionRepository,
                                     ProductRepository productRepository,
                                     String storageBasePath) {
        this(uploadSessionRepository, productRepository, storageBasePath, DEFAULT_MAX_FILE_SIZE_BYTES);
    }

    public BulkUploadProductsUseCase(UploadSessionRepository uploadSessionRepository,
                                     ProductRepository productRepository,
                                     String storageBasePath,
                                     long maxFileSizeBytes) {
        this.uploadSessionRepository = uploadSessionRepository;
        this.productRepository = productRepository;
        this.storageBasePath = storageBasePath;
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.rowValidationChain = buildValidationChain();
    }

    private RowValidator buildValidationChain() {
        RowValidator length  = new RowLengthValidator();
        RowValidator name    = new NameValidator();
        RowValidator price   = new PriceValidator();
        RowValidator station = new StationValidator();
        RowValidator status  = new StatusValidator();
        length.andThen(name).andThen(price).andThen(station).andThen(status);
        return length;
    }

    @Override
    public UploadSession initSession(String fileName, String createdBy) {
        UploadSession session = UploadSession.initiate(fileName, createdBy);
        return uploadSessionRepository.saveSession(session);
    }

    @Override
    public void receiveChunk(UUID uploadId, int chunkIndex, byte[] data, String checksum, String createdBy) {
        uploadSessionRepository.findSessionById(uploadId)
                .orElseThrow(() -> new UploadSessionNotFoundException(uploadId.toString()));

        String dirPath = storageBasePath + "/" + uploadId;
        String filePath = dirPath + "/chunk_" + chunkIndex;

        try {
            Files.createDirectories(Paths.get(dirPath));
            Files.write(Paths.get(filePath), data);
        } catch (IOException e) {
            throw new ChunkAssemblyException("Failed to save chunk " + chunkIndex, e);
        }

        UploadChunk chunk = UploadChunk.receive(uploadId, chunkIndex, data.length, checksum, filePath, createdBy);
        uploadSessionRepository.saveChunk(chunk);

        long cumulativeSize = uploadSessionRepository.findChunksByUploadId(uploadId)
                .stream()
                .mapToLong(UploadChunk::getSize)
                .sum();
        if (cumulativeSize > maxFileSizeBytes) {
            throw new FileSizeLimitExceededException(cumulativeSize, maxFileSizeBytes);
        }
    }

    @Override
    public BulkUploadResult completeAndProcess(UUID uploadId, String updatedBy) {
        UploadSession session = uploadSessionRepository.findSessionById(uploadId)
                .orElseThrow(() -> new UploadSessionNotFoundException(uploadId.toString()));

        List<UploadChunk> chunks = uploadSessionRepository.findChunksByUploadId(uploadId);
        chunks.sort(Comparator.comparingInt(UploadChunk::getChunkIndex));

        String assembledPath = storageBasePath + "/" + uploadId + "/assembled.csv";
        try (OutputStream out = Files.newOutputStream(Paths.get(assembledPath),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (UploadChunk chunk : chunks) {
                byte[] chunkData = Files.readAllBytes(Paths.get(chunk.getFilePath()));
                out.write(chunkData);
            }
        } catch (IOException e) {
            throw new ChunkAssemblyException("Failed to assemble chunks for upload " + uploadId, e);
        }

        long fileSize = new File(assembledPath).length();

        if (fileSize > maxFileSizeBytes) {
            throw new FileSizeLimitExceededException(fileSize, maxFileSizeBytes);
        }

        UploadedFile assembledFile = UploadedFile.assemble(uploadId, assembledPath, fileSize, "assembled", updatedBy);
        uploadSessionRepository.saveFile(assembledFile);

        UploadSession uploaded = session.withUploadStatus(UploadSession.UploadStatus.UPLOADED);
        uploadSessionRepository.saveSession(uploaded);

        return processCSV(uploadId, assembledPath, updatedBy);
    }

    private BulkUploadResult processCSV(UUID uploadId, String csvPath, String updatedBy) {
        int created = 0;
        int updated = 0;
        List<ErrorRecord> errors = new ArrayList<>();
        int rowNumber = 1; 

        try (CSVReader reader = new CSVReader(new InputStreamReader(
                new FileInputStream(csvPath), StandardCharsets.UTF_8))) {

            List<String[]> allRows = reader.readAll();
            if (allRows.isEmpty()) {
                throw new CsvValidationException("CSV file is empty");
            }

            String[] headers = allRows.get(0);
            validateHeaders(headers);

            List<ProductStaging> stagingBatch = new ArrayList<>();

            for (int i = 1; i < allRows.size(); i++) {
                rowNumber = i;
                String[] row = allRows.get(i);
                String rawData = String.join(",", row);

                String errorMessage = rowValidationChain.validate(row, rowNumber);
                if (errorMessage != null) {
                    errors.add(ErrorRecord.of(uploadId, rowNumber, rawData,
                        "VALIDATION_ERROR", errorMessage, updatedBy));
                    continue;
                }

                String nombre = row[0].trim();
                String precioStr = row[1].trim();
                String categoria = row[2].trim();
                String estacion = row[3].trim();
                String descripcion = row.length > 4 ? row[4].trim() : "";
                String estado = row.length > 5 ? row[5].trim() : "Activo";

                ProductStaging staging = ProductStaging.fromRawRow(uploadId, nombre, precioStr,
                        categoria, estacion, descripcion, estado, updatedBy);
                stagingBatch.add(staging);

                if (stagingBatch.size() >= BATCH_SIZE) {
                    uploadSessionRepository.saveAllStaging(new ArrayList<>(stagingBatch));
                    stagingBatch.clear();
                }
            }
            if (!stagingBatch.isEmpty()) {
                uploadSessionRepository.saveAllStaging(stagingBatch);
            }

            List<ProductStaging> stagingRows = uploadSessionRepository.findStagingByUploadId(uploadId);
            for (ProductStaging staging : stagingRows) {
                String[] upsertResult = upsertProduct(staging, updatedBy);
                if ("created".equals(upsertResult[0])) created++;
                else if ("updated".equals(upsertResult[0])) updated++;
            }

            for (ErrorRecord error : errors) {
                uploadSessionRepository.saveError(error);
            }

        } catch (CsvValidationException e) {
            throw e;
        } catch (CsvException | IOException e) {
            throw new CsvValidationException("Failed to parse CSV: " + e.getMessage());
        }

        UploadSession session = uploadSessionRepository.findSessionById(uploadId)
                .orElseThrow(() -> new UploadSessionNotFoundException(uploadId.toString()));
        UploadSession finalSession = session.withProcessingResult(
                UploadSession.ProcessingStatus.COMPLETED,
                created + updated + errors.size(), created + updated, errors.size());
        uploadSessionRepository.saveSession(finalSession);

        return new BulkUploadResult(created, updated, errors.size(), errors);
    }

    private void validateHeaders(String[] headers) {
        if (headers.length < REQUIRED_HEADERS.length) {
            throw new CsvValidationException(
                    "Invalid CSV structure. Expected headers: nombre,precio,categoria,estacion,descripcion,estado");
        }
        for (int i = 0; i < REQUIRED_HEADERS.length; i++) {
            if (!REQUIRED_HEADERS[i].equalsIgnoreCase(headers[i].trim())) {
                throw new CsvValidationException(
                        "Invalid header at position " + i + ". Expected '" + REQUIRED_HEADERS[i]
                                + "' but found '" + headers[i] + "'");
            }
        }
    }

    private String[] upsertProduct(ProductStaging staging, String updatedBy) {
        int priceInCents = (int) (Double.parseDouble(staging.getPrice()) * 100);
        ProductType type = stationToProductType(staging.getStation());
        ProductStatus status = "Inactivo".equalsIgnoreCase(staging.getStatus())
                ? ProductStatus.INACTIVE : ProductStatus.ACTIVE;

        Optional<Product> existing = productRepository.findByName(staging.getName());
        if (existing.isPresent()) {
            Product updatedProduct = existing.get().update(
                    staging.getName(),
                    staging.getDescription() != null && !staging.getDescription().isBlank()
                            ? staging.getDescription() : null,
                    type, staging.getCategory(), priceInCents, status);
            productRepository.save(updatedProduct);
            return new String[]{"updated"};
        }

        Product newProduct = Product.create(staging.getName(), type, staging.getCategory(), priceInCents,
                staging.getDescription() != null && !staging.getDescription().isBlank()
                        ? staging.getDescription() : null);
        productRepository.save(newProduct);
        return new String[]{"created"};
    }

    private ProductType stationToProductType(String station) {
        return switch (station) {
            case "BAR" -> ProductType.DRINK;
            case "HOT_KITCHEN" -> ProductType.HOT_DISH;
            case "COLD_KITCHEN" -> ProductType.COLD_DISH;
            default -> throw new CsvValidationException("Invalid station: " + station);
        };
    }

    @Override
    public UploadSession getStatus(UUID uploadId) {
        return uploadSessionRepository.findSessionById(uploadId)
                .orElseThrow(() -> new UploadSessionNotFoundException(uploadId.toString()));
    }

    @Override
    public byte[] getErrorsCsv(UUID uploadId) {
        List<ErrorRecord> errors = uploadSessionRepository.findErrorsByUploadId(uploadId);

        StringBuilder sb = new StringBuilder();
        sb.append("fila,datos_originales,codigo_error,motivo_del_error\n");
        for (ErrorRecord e : errors) {
            sb.append(e.getRowNumber()).append(",")
              .append("\"").append(e.getRawData() != null ? e.getRawData().replace("\"", "\"\"") : "").append("\",")
              .append(e.getErrorCode()).append(",")
              .append("\"").append(e.getErrorMessage() != null
                  ? e.getErrorMessage().replace("\"", "\"\"") : "").append("\"")
              .append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
