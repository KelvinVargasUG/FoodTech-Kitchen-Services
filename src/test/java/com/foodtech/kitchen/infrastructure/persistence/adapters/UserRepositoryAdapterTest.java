package com.foodtech.kitchen.infrastructure.persistence.adapters;

import com.foodtech.kitchen.domain.model.User;
import com.foodtech.kitchen.domain.model.UserStatus;
import com.foodtech.kitchen.infrastructure.persistence.jpa.UserJpaRepository;
import com.foodtech.kitchen.infrastructure.persistence.jpa.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Tag;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private UserEntity buildEntity(Long id, String username, String email) {
        UserEntity entity = new UserEntity();
        entity.setId(id);
        entity.setUsername(username);
        entity.setEmail(email);
        entity.setPasswordHash("hashed_pw");
        entity.setStatus(UserStatus.ACTIVE.name());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setLastLoginAt(null);
        return entity;
    }

    private User buildUser(Long id) {
        return new User(id, "johndoe", "john@example.com",
                "hashed_pw", UserStatus.ACTIVE,
                LocalDateTime.now(), null);
    }

    @Test
    void save_mapsToEntityCallsJpaAndReturnsDomain() {
        User user = buildUser(1L);
        UserEntity saved = buildEntity(1L, "johndoe", "john@example.com");
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(saved);

        User result = adapter.save(user);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userJpaRepository).save(captor.capture());
        UserEntity captured = captor.getValue();
        assertThat(captured.getUsername()).isEqualTo("johndoe");
        assertThat(captured.getEmail()).isEqualTo("john@example.com");
        assertThat(captured.getPasswordHash()).isEqualTo("hashed_pw");
        assertThat(captured.getStatus()).isEqualTo("ACTIVE");

        assertThat(result.getUsername()).isEqualTo("johndoe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void save_withNullId_setsNullIdOnEntity() {
        User user = new User(null, "newuser", "new@example.com",
                "hashed", UserStatus.ACTIVE, LocalDateTime.now(), null);
        UserEntity saved = buildEntity(99L, "newuser", "new@example.com");
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(saved);

        User result = adapter.save(user);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userJpaRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(result.getId()).isEqualTo(99L);
    }

    @Test
    void findByEmail_found_returnsMappedUser() {
        UserEntity entity = buildEntity(5L, "johndoe", "john@example.com");
        when(userJpaRepository.findByEmail("john@example.com")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByEmail("john@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
        assertThat(result.get().getId()).isEqualTo(5L);
    }

    @Test
    void findByEmail_notFound_returnsEmpty() {
        when(userJpaRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail("missing@example.com");

        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_found_returnsMappedUser() {
        UserEntity entity = buildEntity(7L, "alice", "alice@example.com");
        when(userJpaRepository.findByUsername("alice")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByUsername("alice");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
    }

    @Test
    void findByUsername_notFound_returnsEmpty() {
        when(userJpaRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByUsername("ghost");

        assertThat(result).isEmpty();
    }

    @Test
    void findByEmailOrUsername_foundByEmail_returnsUser() {
        UserEntity entity = buildEntity(3L, "johndoe", "john@example.com");
        when(userJpaRepository.findByEmail("john@example.com")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByEmailOrUsername("john@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
        verify(userJpaRepository, never()).findByUsername(anyString());
    }

    @Test
    void findByEmailOrUsername_emailNotFound_fallsBackToUsername() {
        UserEntity entity = buildEntity(4L, "johndoe", "john@example.com");
        when(userJpaRepository.findByEmail("johndoe")).thenReturn(Optional.empty());
        when(userJpaRepository.findByUsername("johndoe")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByEmailOrUsername("johndoe");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("johndoe");
    }

    @Test
    void findByEmailOrUsername_neitherFound_returnsEmpty() {
        when(userJpaRepository.findByEmail("unknown")).thenReturn(Optional.empty());
        when(userJpaRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmailOrUsername("unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_returnsTrue_whenExists() {
        when(userJpaRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThat(adapter.existsByEmail("john@example.com")).isTrue();
    }

    @Test
    void existsByEmail_returnsFalse_whenNotExists() {
        when(userJpaRepository.existsByEmail("nope@example.com")).thenReturn(false);

        assertThat(adapter.existsByEmail("nope@example.com")).isFalse();
    }

    @Test
    void existsByUsername_returnsTrue_whenExists() {
        when(userJpaRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThat(adapter.existsByUsername("johndoe")).isTrue();
    }

    @Test
    void existsByUsername_returnsFalse_whenNotExists() {
        when(userJpaRepository.existsByUsername("phantom")).thenReturn(false);

        assertThat(adapter.existsByUsername("phantom")).isFalse();
    }
}
