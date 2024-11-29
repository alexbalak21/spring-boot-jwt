package main.services;

import main.entities.Connection;
import main.repositories.ConnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;
import java.util.UUID;

@Service
public class ConnectionService {
    private final ConnectionRepository connectionRepository;

    public ConnectionService(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    public UUID generateConnection (Integer userId) {
        UUID uuid = UUID.randomUUID();
        connectionRepository.save(new Connection(userId, uuid));
        return uuid;
    }

    public boolean checkConnection (Integer userId, UUID uuid) {
        return connectionRepository.findUserIdByConnectionId(uuid).isPresent();
    }

    public Optional<UUID> renewConnection (Integer userId, UUID uuid) {
        if (!checkConnection(userId, uuid)) return Optional.empty();
        connectionRepository.deleteByuserId(userId);
        return Optional.ofNullable(generateConnection(userId));
    }
}
