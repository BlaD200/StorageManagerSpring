package org.vsynytsyn.storagemanager.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.vsynytsyn.storagemanager.controller.response.ErrorResponse;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.AuthoritiesDTO;
import org.vsynytsyn.storagemanager.dto.UserDTO;
import org.vsynytsyn.storagemanager.dto.Views;
import org.vsynytsyn.storagemanager.exceptions.UserAuthoritiesEditingException;
import org.vsynytsyn.storagemanager.exceptions.UserDeletionException;
import org.vsynytsyn.storagemanager.service.UserService;

import java.util.List;


@RestController

@RequestMapping("/api/users")
public class UserController extends AbstractRestController<UserEntity, Long, UserDTO> {

    private final UserService userService;


    public UserController(UserService userService) {
        super(userService);
        this.userService = userService;
    }


    @Override
    @PreAuthorize("hasAuthority('GET_USER')")
    public ResponseEntity<List<UserEntity>> getAll() {
        return super.getAll();
    }


    @Override
    @PreAuthorize("hasAuthority('GET_USER')")
    public ResponseEntity<UserEntity> getOne(
            @PathVariable("id") UserEntity userEntity,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        return super.getOne(userEntity, currentUser);
    }


    @Override
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<Object> create(
            @RequestBody UserEntity userEntity,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        return super.create(userEntity, currentUser);
    }


    @JsonView(Views.ID.class)
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<Object> update(
            @PathVariable(name = "id") UserEntity userEntity,
            @RequestBody UserDTO userDTO,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        if (userEntity == null)
            return ResponseEntity.notFound().build();
        UserEntity updated = userService.update(userEntity, userDTO, currentUser);
        if (updated == null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        return ResponseEntity.ok(updated);
    }


    @Override
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public ResponseEntity<Object> delete(
            @PathVariable("id") UserEntity userEntity,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        try {
            return super.delete(userEntity, currentUser);
        } catch (UserDeletionException e) {
            return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT).getResponseEntity();
        }
    }


    @GetMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('GET_AUTHORITIES')")
    public ResponseEntity<AuthoritiesDTO> getAuthorities(
            @PathVariable(name = "id") UserEntity userEntity
    ) {
        if (userEntity == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(AuthoritiesDTO.of(userEntity.getUserAuthorities()));
    }


    @PostMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('SET_USER_AUTHORITIES')")
    public ResponseEntity<Object> updateAuthorities(
            @PathVariable(name = "id") UserEntity userEntity,
            @RequestBody AuthoritiesDTO authoritiesDTO,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        if (userEntity == null)
            return ResponseEntity.notFound().build();
        else {
            try {
                userService.updateAuthorities(userEntity, authoritiesDTO, currentUser);
            } catch (UserAuthoritiesEditingException e) {
                return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT).getResponseEntity();
            }
        }
        return ResponseEntity.ok(AuthoritiesDTO.of(userEntity.getUserAuthorities()));
    }
}
