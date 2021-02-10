package io.kentec.SaccoMobile.repository;

import io.kentec.SaccoMobile.entity.SystemMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SystemMessagesRepository extends JpaRepository<SystemMessages, Long> {
    @Query("SELECT m FROM SystemMessages m WHERE m.status = :status")
    List<SystemMessages> findMessagesByStatus(@Param("status") String status);
}
