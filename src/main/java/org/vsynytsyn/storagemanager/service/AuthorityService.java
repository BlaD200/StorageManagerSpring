package org.vsynytsyn.storagemanager.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.vsynytsyn.storagemanager.domain.AuthorityEntity;
import org.vsynytsyn.storagemanager.repository.AuthorityRepository;

import java.util.List;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;


    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }


    public List<AuthorityEntity> getAll(Sort sort, String authorityName) {
        return authorityRepository.findAllByNameContainingIgnoreCase(sort, authorityName);
    }
}
