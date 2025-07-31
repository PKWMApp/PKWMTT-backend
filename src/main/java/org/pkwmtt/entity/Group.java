package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "`groups`")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer groupId;

    private String name;

    private int groupCount;

    @ManyToOne
    @JoinColumn(name = "general_group_id")
    private GeneralGroup generalGroup;

    public Group() {

    }
}
