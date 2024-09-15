package org.company.repository;

import org.company.domain.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Cards, String>, JpaSpecificationExecutor<Cards> {

    Integer countAllByUserIdAndStatusIdAndDeletedIsFalse(Long userId, String status);

    Optional<Cards> findByIdempotencyKey(String key);

    Optional<Cards> findByCardIdAndUserIdAndDeletedIsFalse(String cardId, Long userId);

    Optional<Cards> findByCardIdAndUserIdAndStatusIdAndDeletedIsFalse(String cardId, Long userId, String statusId);
}
