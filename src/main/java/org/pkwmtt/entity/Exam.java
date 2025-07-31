package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Builder
@RequiredArgsConstructor
@Table(name = "`exams`")
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer exam_id;

    private String title;

    private String description;

    private Date date;

    @Column(name = "`groups`")
    private String exam_group;

    @ManyToOne
    @JoinColumn(name = "exam_type_id")
    private ExamType exam_type;
}
