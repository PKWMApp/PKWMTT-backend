package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

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
