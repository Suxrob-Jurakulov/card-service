package org.company.service;

import org.company.domain.Cards;
import org.company.dto.TransactionDto;
import org.company.form.CreditForm;
import org.company.form.DebitForm;
import org.company.form.PageForm;
import org.springframework.data.domain.PageImpl;

public interface TransactionService {

    PageImpl<TransactionDto> getHistoryByCard(PageForm form);

    TransactionDto getByIdemKey(String idempotencyKey);

    TransactionDto debit(Cards card, DebitForm form);

    TransactionDto credit(Cards card, CreditForm form);
}
