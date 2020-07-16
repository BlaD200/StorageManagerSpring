package org.vsynytsyn.storagemanager.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.vsynytsyn.storagemanager.util.EntityIdResolver;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(of = "authority_id")
@Table(name = "authority_entity")
@JsonIdentityInfo(
        property = "authority_id",
        generator = ObjectIdGenerators.PropertyGenerator.class,
        scope = AuthorityEntity.class,
        resolver = EntityIdResolver.class
)
public class AuthorityEntity implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authority_id;

    @Column(unique = true, nullable = false)
    private String name;

//    @ManyToMany(mappedBy = "userAuthorities")
//    private List<UserEntity> users;


    @Override
    public String getAuthority() {
        return name;
    }
}
