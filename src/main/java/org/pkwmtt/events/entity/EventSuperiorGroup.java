package org.pkwmtt.events.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "events_superior_group")
public class EventSuperiorGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "row_id")
    private Integer rowId;

    @Column(name = "event_id", nullable = false)
    private Integer eventId;

    @Column(name = "superior_group_id", nullable = false)
    private Integer superiorGroupId;
}

