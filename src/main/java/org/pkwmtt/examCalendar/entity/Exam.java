package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@RequiredArgsConstructor
@Table(name = "exams")
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer examId;

    private String title;

    private String description;

    private LocalDateTime date;

    @Column(name = "`groups`")
    private String examGroups;

    @ManyToOne
    @JoinColumn(name = "exam_type_id")
    private ExamType examType;

//    TODO: add exam builder
}
