package org.vsynytsyn.storagemanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vsynytsyn.storagemanager.service.AbstractService;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRestController<E, T, D> {

    protected final AbstractService<E, T, D> abstractService;


    protected AbstractRestController(AbstractService<E, T, D> abstractService) {
        this.abstractService = abstractService;
    }


    @GetMapping
    public ResponseEntity<List<E>> getAll(){
        return ResponseEntity.ok(abstractService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<E> getOne(
            @PathVariable("id") E obj
    ){
        return ResponseEntity.of(Optional.ofNullable(obj));
    }

    @PutMapping
    public ResponseEntity<E> create(
            @RequestBody E obj
    ){
        E e = abstractService.create(obj);
        if (e == null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        return new ResponseEntity<>(e, HttpStatus.CREATED);
    }


    @PostMapping("/{id}")
    public ResponseEntity<E> update(
            @PathVariable(name = "id") E obj,
            @RequestBody D objDTO
    ){
        if (obj == null)
            return ResponseEntity.notFound().build();
        E updated = abstractService.update(obj, objDTO);
        if (updated == null)
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<E> delete(
            @PathVariable("id") E obj
    ){
        if (abstractService.delete(obj))
            return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }
}
