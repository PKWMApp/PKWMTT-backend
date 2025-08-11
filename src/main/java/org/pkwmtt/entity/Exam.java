package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "`exams`")
@Data
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "exam_id")
    private Integer examId;

    private String title;

    private String description;

    private Date date;
    //private Instant date; Proposition to change

    @Column(name = "`groups`")
    private String examGroup;

    @ManyToOne
    @JoinColumn(name = "exam_type_id")
    private ExamType examType;
}
