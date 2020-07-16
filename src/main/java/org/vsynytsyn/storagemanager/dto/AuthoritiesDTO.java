package org.vsynytsyn.storagemanager.dto;

import lombok.*;
import org.vsynytsyn.storagemanager.domain.AuthorityEntity;

import java.util.List;

@RequiredArgsConstructor(staticName = "of")
@NoArgsConstructor
@Getter @Setter
public class AuthoritiesDTO {
    @NonNull
    private List<AuthorityEntity> authorities;
    // TODO store users token and mark then invalid if user authorities was changed
}
