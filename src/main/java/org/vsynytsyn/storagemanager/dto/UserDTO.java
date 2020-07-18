package org.vsynytsyn.storagemanager.dto;

import lombok.Data;
import org.vsynytsyn.storagemanager.domain.AuthorityEntity;

import java.util.List;

@Data
public class UserDTO {
    private String username;
    private String password;

    private List<AuthorityEntity> authorities;
}
