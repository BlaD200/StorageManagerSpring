package org.vsynytsyn.storagemanager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.vsynytsyn.storagemanager.domain.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    Page<UserEntity> findAllByUsernameContains(Pageable pageable, String username);
}
