package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "`general_group`")
public class GeneralGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "general_group_id")
    private Integer generalGroupId;

    @Column(nullable = false)
    private String name;
}
