package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "`exam_type`")
public class ExamType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_type_id", nullable = false)
    private Integer examTypeId;

    @Column(nullable = false)
    private String name;
}
