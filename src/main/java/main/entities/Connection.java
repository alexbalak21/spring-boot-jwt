package main.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Table(name = "connections")
@Entity
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Integer user1Id;

    @Column(nullable = false)
    private UUID connectionId;
}
