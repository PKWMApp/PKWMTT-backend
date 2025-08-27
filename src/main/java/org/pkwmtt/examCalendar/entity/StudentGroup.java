package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "student_groups")
public class StudentGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer groupId;

    @Column(nullable = false, unique = true)
    private String name;

//    FIXME: remove?
//    @ManyToMany(mappedBy = "groups")
//    private Set<Exam> exams = new HashSet<>();
}
