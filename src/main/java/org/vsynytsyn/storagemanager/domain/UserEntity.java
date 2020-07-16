package org.vsynytsyn.storagemanager.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.vsynytsyn.storagemanager.dto.Views;
import org.vsynytsyn.storagemanager.util.EntityIdResolver;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(of = {"id"})
@JsonIdentityInfo(
        property = "id",
        generator = ObjectIdGenerators.PropertyGenerator.class,
        scope = UserEntity.class,
        resolver = EntityIdResolver.class
)
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.ID.class)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    @JsonView(Views.IDName.class)
    private String username;

    @NotNull
    @Column(nullable = false)
    @JsonIgnore
    private String password;

//    @ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
//    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
//    @Enumerated(value = EnumType.STRING)
    @JsonView(Views.FullProfile.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private List<AuthorityEntity> userAuthorities;


    public void setAuthorities(List<AuthorityEntity> authorities){
        setUserAuthorities(authorities);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getUserAuthorities();
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
