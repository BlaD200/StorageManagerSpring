package org.vsynytsyn.storagemanager.service;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.vsynytsyn.storagemanager.domain.Role;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.UserDTO;
import org.vsynytsyn.storagemanager.exceptions.UserRoleEditingException;
import org.vsynytsyn.storagemanager.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class UserService extends AbstractService<UserEntity, Long, UserDTO> {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;


    protected UserService(UserRepository repository, ModelMapper mapper, BCryptPasswordEncoder encoder) {
        super(repository, mapper);
        this.userRepository = repository;
        this.encoder = encoder;
    }


    @PostConstruct
    private void init() {
        if (userRepository.findByUsername("admin") == null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername("admin");
            userEntity.setPassword(encoder.encode("admin"));
            userEntity.setRoles(Arrays.stream(Role.values()).collect(Collectors.toSet()));
            userRepository.save(userEntity);
        } else {
            UserEntity admin = userRepository.findByUsername("admin");
            admin.getRoles().clear();
            admin.setRoles(Arrays.stream(Role.values()).collect(Collectors.toSet()));
            userRepository.save(admin);
        }
    }


    @Override
    public UserEntity create(UserEntity userEntity, UserEntity currentUser) throws RuntimeException {
        try {
            userEntity.setPassword(encoder.encode(userEntity.getPassword()));
            userEntity.setRoles(Collections.singleton(Role.USER));
            return userRepository.save(userEntity);
        } catch (TransactionSystemException e) {
            throw new RuntimeException(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e.getCause().getCause().getMessage().split("\n")[1].trim());
        }
    }


    @Override
    public UserEntity update(UserEntity userEntity, UserDTO userDTO, UserEntity currentUser) {
        mapper.map(userDTO, userEntity);

        if (userDTO.getPassword() != null)
            userEntity.setPassword(encoder.encode(userDTO.getPassword()));
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            if (!currentUser.getRoles().contains(Role.ADMIN))
                throw new UserRoleEditingException(
                        "User '" + currentUser.getUsername() + "' has no rights " +
                                "to modifying roles for other users.");
            if (currentUser.getRoles().contains(Role.ADMIN) &&
                    !userDTO.getRoles().contains(Role.ADMIN) && userEntity.getId().equals(currentUser.getId()))
                throw new UserRoleEditingException(
                        "User '" + currentUser.getUsername() + "' with the role ADMIN" +
                                " cannot remove ADMIN role from himself. "
                );
            userEntity.getRoles().clear();

            for (Role role : userDTO.getRoles()) {
                userEntity.getRoles().add(role);
            }
        }

        return userRepository.save(userEntity);
    }
}
