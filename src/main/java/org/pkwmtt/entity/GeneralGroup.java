package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class GeneralGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long generalGroupId;

    private String name;

    @OneToMany(mappedBy = "generalGroup")
    private Set<Group> groups;
}
