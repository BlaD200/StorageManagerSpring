package org.vsynytsyn.storagemanager.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vsynytsyn.storagemanager.dto.AuthoritiesDTO;
import org.vsynytsyn.storagemanager.dto.Views;
import org.vsynytsyn.storagemanager.service.AuthorityService;

@RestController
@RequestMapping("/api/authorities")
public class AuthorityController {

    private final AuthorityService authorityService;


    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }


    @GetMapping
    @PreAuthorize("hasAuthority('GET_AUTHORITIES')")
    @JsonView(Views.IDName.class)
    public ResponseEntity<AuthoritiesDTO> getAll(
            @SortDefault Sort sort,
            @RequestParam(name = "name", required = false, defaultValue = "") String authorityName
    ) {
        return ResponseEntity.ok(AuthoritiesDTO.of(authorityService.getAll(sort, authorityName)));
    }
}
