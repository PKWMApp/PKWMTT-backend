package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "`groups`")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer group_id;

    private String name;

    private int group_count;

    @ManyToOne
    @JoinColumn(name = "general_group_id")
    private GeneralGroup general_group;

    public Group() {

    }
}
