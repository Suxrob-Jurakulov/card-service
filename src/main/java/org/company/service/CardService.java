package org.company.service;

import org.company.dto.CardDto;
import org.company.dto.TransactionDto;
import org.company.form.CreditForm;
import org.company.form.DebitForm;
import org.company.form.card.CardForm;

import java.security.NoSuchAlgorithmException;

public interface CardService {
    CardDto add(CardForm form) throws NoSuchAlgorithmException;

    Integer getAllActiveCardAmount(CardForm form);

    CardDto getByKey(String key);

    CardDto getUserCard(Long id, String cardId);

    void changeStatus(CardDto card, String status);

    CardDto getUserCardByStatus(Long id, String cardId, String status);

    TransactionDto debit(DebitForm form);

    TransactionDto credit(CreditForm form);
}
