package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name = "`general_group`")
public class GeneralGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "general_group_id")
    private Integer generalGroupId;

    private String name;

    @OneToMany(mappedBy = "generalGroup")
    private Set<Group> groups;
}
