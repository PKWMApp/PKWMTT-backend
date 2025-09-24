package org.pkwmtt.moderator;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "moderators")
public class Moderator {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String password;
    private String role;
}
