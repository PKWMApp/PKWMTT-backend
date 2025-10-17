package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "student_codes")
public class StudentCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_code_id")
    private Integer studentCodeId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expire;

    @ManyToOne
    @JoinColumn(name = "superior_group_id", nullable = false)
    private SuperiorGroup superiorGroup;

    @Column(name = "usage")
    private Integer usage;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    public StudentCode(String code, SuperiorGroup superiorGroup) {
        this.code = code;
        this.superiorGroup = superiorGroup;
        this.expire = LocalDateTime.now().plusDays(1);
    }
}
