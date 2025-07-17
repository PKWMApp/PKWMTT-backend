package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long groupId;

    private String name;

    private int groupCount;

    @ManyToOne
    @JoinColumn(name = "general_group_id")
    private GeneralGroup generalGroup;
}
