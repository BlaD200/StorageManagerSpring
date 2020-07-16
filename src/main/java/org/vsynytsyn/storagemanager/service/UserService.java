package org.vsynytsyn.storagemanager.service;

import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.vsynytsyn.storagemanager.domain.AuthorityEntity;
import org.vsynytsyn.storagemanager.dto.AuthorityEnum;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.AuthoritiesDTO;
import org.vsynytsyn.storagemanager.dto.UserDTO;
import org.vsynytsyn.storagemanager.exceptions.UserAuthoritiesEditingException;
import org.vsynytsyn.storagemanager.exceptions.UserDeletionException;
import org.vsynytsyn.storagemanager.repository.AuthorityRepository;
import org.vsynytsyn.storagemanager.repository.UserRepository;

@Service
public class UserService extends AbstractService<UserEntity, Long, UserDTO> {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder encoder;


    protected UserService(UserRepository repository, ModelMapper mapper, AuthorityRepository authorityRepository, BCryptPasswordEncoder encoder) {
        super(repository, mapper);
        this.userRepository = repository;
        this.authorityRepository = authorityRepository;
        this.encoder = encoder;
    }


    @Override
    public UserEntity create(UserEntity userEntity, UserEntity currentUser) throws RuntimeException {
        try {
            userEntity.setPassword(encoder.encode(userEntity.getPassword()));
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


    @Override
    public boolean delete(UserEntity userEntity, UserEntity currentUser) {
        if (userEntity.getId().equals(currentUser.getId()))
            throw new UserDeletionException(currentUser.getUsername() + " cannot delete him/herself.");
        return super.delete(userEntity, currentUser);
    }


    public void updateAuthorities(
            UserEntity userEntity,
            AuthoritiesDTO authorities,
            UserEntity currentUser
    ) throws UserAuthoritiesEditingException {
        if (authorities != null && authorities.getAuthorities() != null) {
            if (!currentUser
                    .getAuthorities()
                    .contains(AuthorityEnum.GET_USER.getAuthorityEntity())
            )
                throw new UserAuthoritiesEditingException(
                        "User '" + currentUser.getUsername() + "' has no rights " +
                                "to modifying authorities for other users.");
            if (currentUser
                    .getAuthorities()
                    .contains(AuthorityEnum.SET_USER_AUTHORITIES.getAuthorityEntity())
                && !authorities
                    .getAuthorities()
                    .contains(AuthorityEnum.SET_USER_AUTHORITIES.getAuthorityEntity())
                && userEntity
                    .getId()
                    .equals(currentUser.getId())
            )
                throw new UserAuthoritiesEditingException(
                        "User '" + currentUser.getUsername() + "' with the authority SET_USER_AUTHORITIES" +
                                " cannot remove SET_USER_AUTHORITIES right from himself. "
                );
            userEntity.getAuthorities().clear();

            for (AuthorityEntity authorityEntity : authorities.getAuthorities()) {
                userEntity.getUserAuthorities().add(authorityEntity);
            }

            userRepository.save(userEntity);
        } else throw new UserAuthoritiesEditingException("Authorities cannot be null");
    }
}
