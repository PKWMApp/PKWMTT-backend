package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "`exam_type`")
public class ExamType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "exam_type_id")
    private Integer examTypeId;

    private String name;
}