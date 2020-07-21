package org.vsynytsyn.storagemanager.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.vsynytsyn.storagemanager.domain.AuthorityEntity;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

    Optional<AuthorityEntity> findByName(String authorityName);

    List<AuthorityEntity> findAllByNameContainingIgnoreCase(Sort sort, String authorityName);

}
