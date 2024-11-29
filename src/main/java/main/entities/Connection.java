package main.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Table(name = "connections")
@Entity
public class Connection {
    public Connection(Integer userId, UUID connectionId) {
        this.userId = userId;
        this.connectionId = connectionId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private UUID connectionId;

    public Connection() {
    }
}
