package org.vsynytsyn.storagemanager.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    @PreAuthorize("hasAuthority('GET_USER')")
    @JsonView(Views.IDName.class)
    public ResponseEntity<Page<UserEntity>> getAll(
            Pageable pageable,
            @RequestParam(required = false) String username
    ) {
        return ResponseEntity.ok(userService.getAll(pageable, username));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_USER')")
    @JsonView(Views.FullProfile.class)
    public ResponseEntity<UserEntity> getOne(
            @PathVariable("id") UserEntity userEntity,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        return ResponseEntity.of(Optional.ofNullable(userEntity));
    }


    @PutMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @JsonView(Views.ID.class)
    public ResponseEntity<Object> create(
            @RequestBody UserEntity userEntity,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        try {
            UserEntity e = userService.create(userEntity, currentUser);
            if (e == null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(e, HttpStatus.CREATED);
        } catch (RuntimeException exception) {
            return new ErrorResponse(exception.getMessage(), HttpStatus.CONFLICT).getResponseEntity();
        }
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @JsonView(Views.ID.class)
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


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public ResponseEntity<Object> delete(
            @PathVariable("id") UserEntity userEntity,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        try {
            if (userService.delete(userEntity, currentUser))
                return ResponseEntity.noContent().build();
            return ResponseEntity.notFound().build();
        } catch (UserDeletionException e) {
            return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT).getResponseEntity();
        }
    }


    @GetMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('GET_AUTHORITIES')")
    @JsonView(Views.IDName.class)
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
    @JsonView(Views.IDName.class)
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
