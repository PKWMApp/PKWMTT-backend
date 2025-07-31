package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "`exam_type`")
public class ExamType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer exam_type_id;

    private String name;

    public ExamType() {

    }
}