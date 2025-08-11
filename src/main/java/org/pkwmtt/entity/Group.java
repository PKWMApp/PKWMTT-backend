package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "`groups`")
//Recommended change name of this class. Group is key word in sql and may lead to misunderstandings
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id")
    private Integer groupId;

    private String name;

    @Column(name = "group_count")
    private int groupCount;

    @ManyToOne
    @JoinColumn(name = "general_group_id")
    private GeneralGroup generalGroup;
}
