package io.swagger.firebird.repository;

import io.swagger.firebird.model.Color;
import io.swagger.firebird.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    @Query(nativeQuery = true, value = "SELECT FIRST 1 DISTINCT c.* FROM DOCUMENT_OUT AS do " +
            "INNER JOIN CONTACT AS c ON c.CONTACT_ID = do.CLIENT_CONTACT_ID " +
            "WHERE do.CLIENT_ID = :clientId")
    Contact findContactByClientId(@Param("clientId") Integer clientId);

}
