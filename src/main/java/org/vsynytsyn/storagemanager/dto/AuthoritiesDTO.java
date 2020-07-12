package org.vsynytsyn.storagemanager.dto;

import lombok.*;
import org.vsynytsyn.storagemanager.domain.Authority;

import java.util.Set;

@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter @Setter
public class AuthoritiesDTO {
    @NonNull
    private Set<Authority> authorities;
    // TODO store users token and mark then invalid if user authorities was changed
}
