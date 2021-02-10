package io.kentec.SaccoMobile.repository;

import io.kentec.SaccoMobile.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customers, Long> {
    @Query(value = "SELECT * FROM CUSTOMERS c WHERE ID_NO = :idNo", nativeQuery = true)
    Customers findByIdNo(@Param("idNo") int idNo);

    @Query(value = "SELECT * FROM CUSTOMERS c WHERE c.MSISDN = :msisdn", nativeQuery = true)
    Customers findByMsisdn(@Param("msisdn") String msisdn);
}
