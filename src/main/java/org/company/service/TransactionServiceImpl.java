package org.company.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.company.domain.Cards;
import org.company.domain.Transactions;
import org.company.dto.TransactionDto;
import org.company.form.CreditForm;
import org.company.form.DebitForm;
import org.company.form.PageForm;
import org.company.repository.TransactionRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;

    @Override
    public PageImpl<TransactionDto> getHistoryByCard(PageForm form) {
        List<Sort.Order> orders = new ArrayList<>();

        Specification<Transactions> cardId = (root, cq, cb) -> cb.equal(root.get("cardId"), form.getCardId());

        Specification<Transactions> hasType = (root, cq, cb) -> {
            if (form.getType() != null && !form.getType().isBlank()) {
                return cb.equal(root.get("type"), form.getType());
            }
            return null;
        };
        Specification<Transactions> hasTransactionId = (root, cq, cb) -> {
            if (form.getType() != null && !form.getTransactionId().isBlank()) {
                return cb.equal(root.get("id"), form.getTransactionId());
            }
            return null;
        };
        Specification<Transactions> hasExternalId = (root, cq, cb) -> {
            if (form.getType() != null && !form.getExternalId().isBlank()) {
                return cb.equal(root.get("externalId"), form.getExternalId());
            }
            return null;
        };

        orders.add(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(form.getPage(), form.getSize());

        Page<Transactions> transactions = repository.findAll(
                where(cardId).and(hasType).and(hasTransactionId).and(hasExternalId),
                PageRequest.of(form.getPage(), form.getSize(), Sort.by(orders)));

        List<Transactions> list = transactions.getContent();
        List<TransactionDto> dtoList = list.stream().map(this::map).toList();

        return new PageImpl<>(dtoList, pageable, transactions.getTotalElements());

    }

    @Override
    public TransactionDto getByIdemKey(String idempotencyKey) {
        Optional<Transactions> transaction = repository.findByIdemKey(idempotencyKey);
        return transaction.map(this::map).orElse(null);
    }

    @Override
    @Transactional
    public TransactionDto debit(Cards card, DebitForm form) {
        Transactions transaction = new Transactions();

        transaction.setIdemKey(form.getIdempotencyKey());
        transaction.setExternalId(form.getExternalId());
        transaction.setCardId(card.getCardId());
        transaction.setType("DEBIT");
        transaction.setAmount(form.getAmount());
        transaction.setAfterBalance(card.getBalance());
        transaction.setCurrency(form.getCurrency());
        transaction.setPurpose(form.getPurpose());
        transaction.setExchangeRate(form.getExchangeRate());
        transaction.setCreatedAt(LocalDateTime.now());

        repository.save(transaction);

        return map(transaction);
    }

    @Override
    @Transactional
    public TransactionDto credit(Cards card, CreditForm form) {
        Transactions transaction = new Transactions();

        transaction.setIdemKey(form.getIdempotencyKey());
        transaction.setExternalId(form.getExternalId());
        transaction.setCardId(card.getCardId());
        transaction.setType("CREDIT");
        transaction.setAmount(form.getAmount());
        transaction.setAfterBalance(card.getBalance());
        transaction.setCurrency(form.getCurrency());
        transaction.setExchangeRate(form.getExchangeRate());
        transaction.setCreatedAt(LocalDateTime.now());

        repository.save(transaction);

        return map(transaction);
    }

    // MAPPER
    private TransactionDto map(Transactions transaction) {
        TransactionDto dto = new TransactionDto();

        dto.setId(transaction.getId());
        dto.setIdemKey(transaction.getIdemKey());
        dto.setExternalId(transaction.getExternalId());
        dto.setCardId(transaction.getCardId());
        dto.setAmount(transaction.getAmount());
        dto.setAfterBalance(transaction.getAfterBalance());
        dto.setCurrency(transaction.getCurrency());
        dto.setPurpose(transaction.getPurpose());
        dto.setExchangeRate(transaction.getExchangeRate());
        dto.setCreatedAt(transaction.getCreatedAt());

        return dto;
    }
}
