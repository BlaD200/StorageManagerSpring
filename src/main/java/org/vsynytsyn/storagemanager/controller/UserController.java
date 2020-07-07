package org.vsynytsyn.storagemanager.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.UserDTO;
import org.vsynytsyn.storagemanager.dto.Views;
import org.vsynytsyn.storagemanager.exceptions.UserRoleEditingException;
import org.vsynytsyn.storagemanager.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController extends AbstractRestController<UserEntity, Long, UserDTO> {

    private final UserService userService;


    public UserController(UserService userService) {
        super(userService);
        this.userService = userService;
    }


    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(Views.ID.class)
    public ResponseEntity<Object> update(
            @PathVariable(name = "id") UserEntity userEntity,
            @RequestBody UserDTO userDTO,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        if (userEntity == null)
            return ResponseEntity.notFound().build();
        try {
            UserEntity updated = abstractService.update(userEntity, userDTO, currentUser);
            if (updated == null)
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            return ResponseEntity.ok(updated);
        } catch (UserRoleEditingException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

}
