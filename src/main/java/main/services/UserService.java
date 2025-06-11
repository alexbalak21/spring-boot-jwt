package main.services;

import main.entities.User;
import main.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
    public UUID generateUuid(String email) {
        UUID uuid = UUID.randomUUID();
        userRepository.updateUserUuidByEmail(email, uuid);
        return uuid;
    }

    public void resetUuid(String email) {
        userRepository.updateUserUuidByEmail(email, null);
    }
}