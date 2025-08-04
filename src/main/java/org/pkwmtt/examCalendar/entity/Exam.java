package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
            if (title == null || title.isEmpty() || title.length() > 255)          //TODO: change exception types
                throw new RuntimeException("Invalid title");
            if(description.length() > 255)
                throw new RuntimeException("Invalid description");
            if(date == null || date.isBefore(LocalDateTime.now()))
                throw new RuntimeException("Invalid date");
            if(examGroups == null || examGroups.length() > 255)
                throw new RuntimeException("Invalid exam groups String");
            Arrays.stream(examGroups.split(", ")).forEach(group -> {
                if(group.length() > 8)
                    throw new RuntimeException("Invalid exam group: " + group);
            });
            if(examType == null || examType.getName() == null || examType.getName().length() > 255)
                throw new RuntimeException("Invalid exam type");
            return new Exam(examId, title, description, date, examGroups, examType);
        }
    }
}
