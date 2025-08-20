package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.InvalidGroupIdentifierException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder(builderClassName = "Builder", buildMethodName = "build")
@RequiredArgsConstructor
@Table(name = "exams")
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Integer examId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "`exam_date`", nullable = false)
    private LocalDateTime examDate;

    @ManyToOne
    @JoinColumn(name = "exam_type_id", nullable = false)
    private ExamType examType;

    @ManyToMany
    @JoinTable(
            name="exams_groups",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<StudentGroup> groups = new HashSet<>();

    @SuppressWarnings("unused")
    public static class Builder {
        public Exam build() {
            //    max length of group identifier is 6
            groups.forEach(group -> {
                if(group.getName().length() > 6)
                    throw new InvalidGroupIdentifierException(group.getName());
            });
            return new Exam(examId, title, description, examDate, examType, groups);
        }
    }
}