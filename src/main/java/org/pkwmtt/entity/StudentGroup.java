package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "`groups`")
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer groupId;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "groups")
    private Set<Exam> exams = new HashSet<>();
}
