package org.pkwmtt.calendar.events.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int event_id;
    
    @Column(name = "title")
    String title;
    
    @Column(name = "description")
    String description;
    
    @Column(name = "start_date")
    Date startDate;
    
    @Column(name = "end_date")
    Date endDate;
}
