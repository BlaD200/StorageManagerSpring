package org.vsynytsyn.storagemanager.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.vsynytsyn.storagemanager.controller.response.ErrorResponse;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.Views;
import org.vsynytsyn.storagemanager.service.AbstractService;

import java.util.Optional;

public abstract class AbstractRestController<E, T, D> {

    protected final AbstractService<E, T, D> abstractService;


    protected AbstractRestController(AbstractService<E, T, D> abstractService) {
        this.abstractService = abstractService;
    }


    @GetMapping
    @JsonView(Views.IDName.class)
    public ResponseEntity<Page<E>> getAll(Pageable pageable) {
        return ResponseEntity.ok(abstractService.getAll(pageable));
    }


    @GetMapping("/{id}")
    @JsonView(Views.FullProfile.class)
    public ResponseEntity<E> getOne(
            @PathVariable("id") E obj,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        return ResponseEntity.of(Optional.ofNullable(obj));
    }


    @PutMapping
    @JsonView(Views.ID.class)
    public ResponseEntity<Object> create(
            @RequestBody E obj,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        try {
            E e = abstractService.create(obj, currentUser);
            if (e == null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(e, HttpStatus.CREATED);
        } catch (RuntimeException exception) {
            return new ErrorResponse(exception.getMessage(), HttpStatus.CONFLICT).getResponseEntity();
        }
    }


    @PostMapping("/{id}")
    @JsonView(Views.ID.class)
    public ResponseEntity<Object> update(
            @PathVariable(name = "id") E obj,
            @RequestBody D objDTO,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        if (obj == null)
            return ResponseEntity.notFound().build();
        E updated = abstractService.update(obj, objDTO, currentUser);
        if (updated == null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(
            @PathVariable("id") E obj,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        if (abstractService.delete(obj, currentUser))
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}
