package org.vsynytsyn.storagemanager.dto;

import lombok.Data;
import org.vsynytsyn.storagemanager.domain.AuthorityEntity;

import java.util.Set;

@Data
public class UserDTO {
    private String username;
    private String password;

    private Set<AuthorityEntity> authorities;
}
