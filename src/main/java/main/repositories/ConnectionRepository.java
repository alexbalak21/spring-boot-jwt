package main.repositories;

import main.entities.Connection;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectionRepository extends CrudRepository<Connection, Integer> {
    void  deleteByuserId(Integer userId);

    //GET USER ID BY CONNECTION ID
    @Query("SELECT c.userId FROM Connection c WHERE c.connectionId = :connectionId")
    Optional<Integer> findUserIdByConnectionId(@Param("connectionId") UUID connectionId);

  //UPDATE CONNECTION ID BY USER ID
    @Modifying
    @Query("UPDATE Connection c SET c.connectionId = :connectionId WHERE c.userId = :userId")
    void updateConnectionIdByUserId(@Param("userId") Integer userId, @Param("connectionId") UUID connectionId);
}
