package org.vsynytsyn.storagemanager.dto;

import lombok.*;
import org.vsynytsyn.storagemanager.domain.Authority;

import java.util.Set;

@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter @Setter
public class UserAuthoritiesDTO {
    @NonNull
    private Set<Authority> authorities;
}
