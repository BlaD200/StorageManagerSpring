package org.vsynytsyn.storagemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vsynytsyn.storagemanager.domain.Authority;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByName(String authorityName);

//    Optional<Authority> findByAuthority_id(Long authorityID);
}
