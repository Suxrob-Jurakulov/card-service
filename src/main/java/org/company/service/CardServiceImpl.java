package org.company.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.company.domain.CardStatus;
import org.company.domain.Cards;
import org.company.dto.CardDto;
import org.company.dto.TransactionDto;
import org.company.form.CreditForm;
import org.company.form.DebitForm;
import org.company.form.card.CardForm;
import org.company.repository.CardRepository;
import org.company.utils.ETagGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository repository;
    private final TransactionService transactionService;

    @Override
    public Integer getAllActiveCardAmount(CardForm form) {
        return repository.countAllByUserIdAndStatusIdAndDeletedIsFalse(form.getUserId(), CardStatus.ACTIVE.getId());
    }

    @Override
    public CardDto getByKey(String key) {
        Optional<Cards> keyOpl = repository.findByIdempotencyKey(key);
        return keyOpl.map(this::map).orElse(null);
    }

    @Override
    public CardDto getUserCard(Long id, String cardId) {
        Optional<Cards> cardOpl = repository.findByCardIdAndUserIdAndDeletedIsFalse(cardId, id);
        return cardOpl.map(this::map).orElse(null);
    }

    @Override
    public void changeStatus(CardDto card, String status) {
        Optional<Cards> cardOpl = repository.findByCardIdAndUserIdAndDeletedIsFalse(card.getCardId(), card.getUserId());
        if (cardOpl.isPresent()) {
            Cards cards = cardOpl.get();
            cards.setStatusId(CardStatus.find(status).getId());
            cards.setUpdatedAt(LocalDateTime.now());
            card.setETag(ETagGenerator.generateETag(cards));

            repository.save(cards);
        }
    }

    @Override
    public CardDto getUserCardByStatus(Long id, String cardId, String status) {
        Optional<Cards> cardOpl = repository.findByCardIdAndUserIdAndStatusIdAndDeletedIsFalse(cardId, id, status);
        return cardOpl.map(this::map).orElse(null);
    }

    @Override
    public TransactionDto debit(DebitForm form) {
        Optional<Cards> cardOpl = repository.findByCardIdAndUserIdAndDeletedIsFalse(form.getCardId(), form.getUserId());
        if (cardOpl.isPresent()) {
            Cards card = cardOpl.get();

            card.setBalance(card.getBalance() - form.getTotalAmount());
            card.setUpdatedAt(LocalDateTime.now());
            card.setETag(ETagGenerator.generateETag(card));

            TransactionDto debit = transactionService.debit(card, form);

            repository.save(card);

            return debit;
        }
        return null;
    }

    @Override
    public TransactionDto credit(CreditForm form) {
        Optional<Cards> cardOpl = repository.findByCardIdAndUserIdAndDeletedIsFalse(form.getCardId(), form.getUserId());
        if (cardOpl.isPresent()) {
            Cards card = cardOpl.get();

            card.setBalance(card.getBalance() + form.getTotalAmount());
            card.setUpdatedAt(LocalDateTime.now());
            card.setETag(ETagGenerator.generateETag(card));

            TransactionDto credit = transactionService.credit(card, form);

            repository.save(card);

            return credit;
        }
        return null;
    }

    @SneakyThrows
    @Override
    public CardDto add(CardForm form) {
        Cards card = new Cards();

        card.setUserId(form.getUserId());
        card.setIdempotencyKey(form.getKey());
        card.setStatusId(form.getStatus());
        card.setBalance(form.getInitialAmount());
        card.setCurrencyId(form.getCurrency());
        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        card.setETag(ETagGenerator.generateETag(card));
        card.setDeleted(false);

        repository.save(card);

        return map(card);
    }

    // MAPPER
    private CardDto map(Cards card) {
        CardDto dto = new CardDto();

        dto.setCardId(card.getCardId());
        dto.setUserId(card.getUserId());
        dto.setStatus(card.getStatusId());
        dto.setBalance(card.getBalance());
        dto.setCurrency(card.getCurrencyId());
        dto.setETag(card.getETag());

        return dto;
    }
}
