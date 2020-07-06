package org.vsynytsyn.storagemanager.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.UserDTO;

@Service
public class UserService extends AbstractService<UserEntity, Long, UserDTO> {


    protected UserService(JpaRepository<UserEntity, Long> repository, ModelMapper mapper) {
        super(repository, mapper);
    }
}
