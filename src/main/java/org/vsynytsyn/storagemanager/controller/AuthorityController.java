package org.vsynytsyn.storagemanager.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vsynytsyn.storagemanager.dto.AuthoritiesDTO;
import org.vsynytsyn.storagemanager.dto.Views;
import org.vsynytsyn.storagemanager.repository.AuthorityRepository;

@RestController
@RequestMapping("/api/authorities")
public class AuthorityController {

    private final AuthorityRepository authorityRepository;


    public AuthorityController(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }


    @GetMapping
    @PreAuthorize("hasAuthority('GET_AUTHORITIES')")
    @JsonView(Views.IDName.class)
    public ResponseEntity<AuthoritiesDTO> getAll() {
        return ResponseEntity.ok(AuthoritiesDTO.of(authorityRepository.findAll()));
    }
}
