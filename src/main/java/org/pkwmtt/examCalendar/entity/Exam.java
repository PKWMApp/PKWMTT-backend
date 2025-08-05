package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.InvalidGroupIdentifierException;

import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Getter
@Builder(builderClassName = "Builder", buildMethodName = "build")
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

    public static class Builder {
        public Exam build() {
            //    max length of group identifier is 6
            Arrays.stream(examGroups.split(", ")).forEach(group -> {
                if(group.length() > 6)
                    throw new InvalidGroupIdentifierException(group);
            });
            return new Exam(examId, title, description, date, examGroups, examType);
        }
    }
}
