package org.vsynytsyn.storagemanager.dto;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vsynytsyn.storagemanager.domain.AuthorityEntity;
import org.vsynytsyn.storagemanager.repository.AuthorityRepository;

import javax.annotation.PostConstruct;


public enum AuthorityEnum {
    GET_USER, CREATE_USER, UPDATE_USER, DELETE_USER,
    GET_AUTHORITIES, SET_USER_AUTHORITIES;

    @Getter
    private AuthorityEntity authorityEntity;


    @Component
    private static class AuthorityInjector {

        private final AuthorityRepository repository;


        @Autowired
        private AuthorityInjector(AuthorityRepository repository) {
            this.repository = repository;
        }


        @PostConstruct
        void injectRepository() {
            for (AuthorityEnum authorityEnum : AuthorityEnum.values()) {
                authorityEnum.authorityEntity =
                        repository.findByName(authorityEnum.name()).orElseThrow(IllegalArgumentException::new);
            }
        }
    }
}
