package main.repositories;

import jakarta.transaction.Transactional;
import main.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    //GET ID BY EMAIL
    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    @Transactional
    Optional<Integer> getIdByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE User user SET user.uid = :uuid WHERE user.email = :email")
    void updateUserUuidByEmail(@Param("email") String email, @Param("uuid") UUID uuid);
}