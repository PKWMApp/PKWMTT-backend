package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "`general_group`")
public class GeneralGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer general_group_id;

    private String name;

    @OneToMany(mappedBy = "general_group")
    private Set<Group> groups;

    public GeneralGroup() {

    }
}
