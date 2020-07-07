package org.vsynytsyn.storagemanager.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.vsynytsyn.storagemanager.domain.UserEntity;
import org.vsynytsyn.storagemanager.dto.Views;
import org.vsynytsyn.storagemanager.service.AbstractService;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRestController<E, T, D> {

    protected final AbstractService<E, T, D> abstractService;


    protected AbstractRestController(AbstractService<E, T, D> abstractService) {
        this.abstractService = abstractService;
    }


    @GetMapping
    @JsonView(Views.IDName.class)
    public ResponseEntity<List<E>> getAll() {
        return ResponseEntity.ok(abstractService.getAll());
    }


    @GetMapping("/{id}")
    @JsonView(Views.FullProfile.class)
    public ResponseEntity<E> getOne(
            @PathVariable("id") E obj,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        System.err.println(currentUser);
        return ResponseEntity.of(Optional.ofNullable(obj));
    }


    @PutMapping
    @PreAuthorize("hasAuthority('ADMIN')")
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
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }


    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<E> delete(
            @PathVariable("id") E obj,
            @AuthenticationPrincipal UserEntity currentUser
    ) {
        if (abstractService.delete(obj, currentUser))
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}
