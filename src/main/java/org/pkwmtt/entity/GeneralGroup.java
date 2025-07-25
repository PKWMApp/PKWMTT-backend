package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class GeneralGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long generalGroupId;

    private String name;

    @OneToMany(mappedBy = "generalGroup")
    private Set<Group> groups;

    public GeneralGroup() {

    }
}
