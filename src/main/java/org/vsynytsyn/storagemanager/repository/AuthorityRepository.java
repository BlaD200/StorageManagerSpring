package org.vsynytsyn.storagemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vsynytsyn.storagemanager.domain.AuthorityEntity;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

    Optional<AuthorityEntity> findByName(String authorityName);

}
