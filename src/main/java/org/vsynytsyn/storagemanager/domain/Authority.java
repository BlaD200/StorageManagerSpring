package org.vsynytsyn.storagemanager.domain;

import org.springframework.security.core.GrantedAuthority;


public enum Authority implements GrantedAuthority {
    GET_USER, CREATE_USER, UPDATE_USER, DELETE_USER, GET_AUTHORITIES, SET_USER_AUTHORITIES;


    @Override
    public String getAuthority() {
        return name();
    }
}
