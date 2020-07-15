package org.vsynytsyn.storagemanager.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(of = "authority_id")
public class Authority implements GrantedAuthority {
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


    @JsonValue
    public String toJson(){
        return getName();
    }
}
