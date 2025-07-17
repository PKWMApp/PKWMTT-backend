package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long examId;

    private String title;

    private String description;

    private Date date;

    private String group;

    @ManyToOne
    @JoinColumn(name = "exam_type_id")
    private ExamType examType;
}
