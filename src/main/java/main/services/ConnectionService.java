package main.services;

import main.entities.Connection;
import main.repositories.ConnectionRepository;
import main.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConnectionService {
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    public UUID generateConnection (Integer userId) {
        UUID uuid = UUID.randomUUID();
        connectionRepository.save(new Connection(userId, uuid));
        return uuid;
    }

    public boolean checkConnection (Integer userId, UUID uuid) {
        if (connectionRepository.findUserIdByConnectionId(uuid).isPresent()) {
            return Objects.equals(userId, connectionRepository.findUserIdByConnectionId(uuid).get());
        }
        return false;
    }

    public Optional<UUID> renewConnection (UUID uniqueConnectionId, String userEmail) {
        if (uniqueConnectionId == null) return Optional.empty();
        //FORM CONNECTION DB
        Integer userConnectionId = connectionRepository.findUserIdByConnectionId(uniqueConnectionId).orElse(null);
        if (userConnectionId == null) return Optional.empty();
        //FROM USER DB
        Integer userId = userRepository.getIdByEmail(userEmail).orElse(null);
        if (userId == null) return Optional.empty();
        //COMPARES THE CONNECTION ID TO THE USER ID
        // (If the User account is deleted or deactivated it prevents the user to use the service)
        if (!userId.equals(userConnectionId)) return Optional.empty();


        if (!checkConnection(userId, uniqueConnectionId)) return Optional.empty();
        UUID newUuid = UUID.randomUUID();
        connectionRepository.updateConnectionIdByUserId(userId, newUuid);
        return Optional.of(newUuid);
    }

    boolean validateConnection(UUID connectionId, String userEmail) {
        if (connectionId == null || userEmail == null) return false;
        if (connectionRepository.findUserIdByConnectionId(connectionId).isPresent() && userRepository.getIdByEmail(userEmail).isPresent()) {
            return Objects.equals(userRepository.getIdByEmail(userEmail).get(), connectionRepository.findUserIdByConnectionId(connectionId).get());
        }
        return false;
    }
}
