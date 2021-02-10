package io.kentec.SaccoMobile.repository;

import io.kentec.SaccoMobile.entity.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {

    @Query("SELECT t FROM MessageTemplate t WHERE t.messageCode = :messageCode")
    MessageTemplate findByMessageCode(@Param("messageCode") String messageCode);
}
