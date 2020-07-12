package org.vsynytsyn.storagemanager.service;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.vsynytsyn.storagemanager.domain.Authority;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.AuthoritiesDTO;
import org.vsynytsyn.storagemanager.dto.UserDTO;
import org.vsynytsyn.storagemanager.exceptions.UserAuthoritiesEditingException;
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
            userEntity.setAuthorities(Arrays.stream(Authority.values()).collect(Collectors.toSet()));
            userRepository.save(userEntity);
        } else {
            UserEntity admin = userRepository.findByUsername("admin");
            admin.getAuthorities().clear();
            admin.setAuthorities(Arrays.stream(Authority.values()).collect(Collectors.toSet()));
            userRepository.save(admin);
        }
    }


    @Override
    public UserEntity create(UserEntity userEntity, UserEntity currentUser) throws RuntimeException {
        try {
            userEntity.setPassword(encoder.encode(userEntity.getPassword()));
            userEntity.setAuthorities(Collections.singleton(Authority.GET_USER));
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

        return userRepository.save(userEntity);
    }

    public void updateAuthorities(
            UserEntity userEntity,
            AuthoritiesDTO authorities,
            UserEntity currentUser
    ) throws UserAuthoritiesEditingException {
        if (authorities != null && authorities.getAuthorities() != null) {
            if (!currentUser.getAuthorities().contains(Authority.SET_USER_AUTHORITIES))
                throw new UserAuthoritiesEditingException(
                        "User '" + currentUser.getUsername() + "' has no rights " +
                                "to modifying authorities for other users.");
            if (currentUser.getAuthorities().contains(Authority.SET_USER_AUTHORITIES) &&
                    !authorities.getAuthorities().contains(Authority.SET_USER_AUTHORITIES) &&
                    userEntity.getId().equals(currentUser.getId()))
                throw new UserAuthoritiesEditingException(
                        "User '" + currentUser.getUsername() + "' with the authority SET_USER_AUTHORITIES" +
                                " cannot remove SET_USER_AUTHORITIES right from himself. "
                );
            userEntity.getAuthorities().clear();

            for (Authority authority : authorities.getAuthorities()) {
                userEntity.getUserAuthorities().add(authority);
            }

            userRepository.save(userEntity);
        } else throw new UserAuthoritiesEditingException("Authorities cannot be null");
    }
}
