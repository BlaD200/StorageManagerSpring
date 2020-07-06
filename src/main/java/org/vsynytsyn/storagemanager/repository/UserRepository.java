package org.vsynytsyn.storagemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vsynytsyn.storagemanager.domain.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
