package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`general_group`")
public class GeneralGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer general_group_id;

    private String name;

    @OneToMany(mappedBy = "general_group")
    private Set<Group> groups;
}
