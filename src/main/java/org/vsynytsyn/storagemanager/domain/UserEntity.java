package org.vsynytsyn.storagemanager.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.vsynytsyn.storagemanager.dto.Views;
import org.vsynytsyn.storagemanager.util.EntityIdResolver;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(of = {"id"})
@JsonIdentityInfo(
        property = "id",
        generator = ObjectIdGenerators.PropertyGenerator.class,
        scope = UserEntity.class,
        resolver = EntityIdResolver.class
)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.ID.class)
    private Long id;

    @NotNull
    @JsonView(Views.IDName.class)
    private String username;
    @NotNull
    @JsonView(Views.FullProfile.class)
    private String password;
}
