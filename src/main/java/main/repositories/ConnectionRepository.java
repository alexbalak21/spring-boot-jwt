package main.repositories;

import main.entities.Connection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectionRepository extends CrudRepository<Connection, Integer> {
    void  deleteByuserId(Integer userId);

    //GETS userId by connectionId
    Optional <Integer> findUserIdByConnectionId(UUID connectionId);
}
