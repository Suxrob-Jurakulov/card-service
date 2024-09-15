package org.company.repository;

import org.company.domain.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, String>, JpaSpecificationExecutor<Transactions> {

    Optional<Transactions> findByIdemKey(String idemKey);

}
