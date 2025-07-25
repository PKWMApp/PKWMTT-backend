package org.pkwmtt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class ExamType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long examTypeId;

    private String name;

    public ExamType() {

    }
}