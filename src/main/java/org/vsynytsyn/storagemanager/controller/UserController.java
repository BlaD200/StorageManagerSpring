package org.vsynytsyn.storagemanager.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.UserAuthoritiesDTO;
import org.vsynytsyn.storagemanager.dto.UserDTO;
import org.vsynytsyn.storagemanager.dto.Views;
import org.vsynytsyn.storagemanager.exceptions.UserAuthoritiesEditingException;
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
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_USER')")
    public ResponseEntity<UserEntity> getOne(
            @PathVariable("id") UserEntity userEntity,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        System.err.println(currentUser);
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


    @PostMapping("/{id}")
    @JsonView(Views.ID.class)
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<Object> update(
            @PathVariable(name = "id") UserEntity userEntity,
            @RequestBody UserDTO userDTO,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        if (userEntity == null)
            return ResponseEntity.notFound().build();
        try {
            UserEntity updated = userService.update(userEntity, userDTO, currentUser);
            if (updated == null)
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            return ResponseEntity.ok(updated);
        } catch (UserAuthoritiesEditingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }


    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public ResponseEntity<UserEntity> delete(
            @PathVariable("id") UserEntity userEntity,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        return super.delete(userEntity, currentUser);
    }


    @GetMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('GET_AUTHORITIES')")
    public ResponseEntity<UserAuthoritiesDTO> getAuthorities(
            @PathVariable(name = "id") UserEntity userEntity
    ) {
        if (userEntity == null)
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(UserAuthoritiesDTO.of(userEntity.getUserAuthorities()));
    }


    @PostMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('SET_USER_AUTHORITIES')")
    public ResponseEntity<Object> updateAuthorities(
            @PathVariable(name = "id") UserEntity userEntity,
            @RequestBody UserAuthoritiesDTO authoritiesDTO,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        if (userEntity == null)
            return ResponseEntity.notFound().build();
        else {
            try {
                userService.updateAuthorities(userEntity, authoritiesDTO, currentUser);
            } catch (UserAuthoritiesEditingException e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }
        }
        return ResponseEntity.ok(UserAuthoritiesDTO.of(userEntity.getUserAuthorities()));
    }

    // TODO send responses as JSON
}
