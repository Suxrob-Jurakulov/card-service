package org.company.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.company.config.CustomUserDetails;
import org.company.domain.CardStatus;
import org.company.domain.Currency;
import org.company.dto.CardDto;
import org.company.dto.TransactionDto;
import org.company.dto.UserAccountDto;
import org.company.exp.BadRequestException;
import org.company.form.CreditForm;
import org.company.form.DebitForm;
import org.company.form.PageForm;
import org.company.form.card.CardForm;
import org.company.json.CurrencyJson;
import org.company.proxy.CBUProxy;
import org.company.service.AuthService;
import org.company.service.CardService;
import org.company.service.TransactionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService service;
    private final AuthService authService;
    private final TransactionService transactionService;
    private final CBUProxy proxy;

    private void checkUser(String username, Long userId) {
        UserAccountDto user = authService.getUser(username);
        if (user == null || !user.getId().equals(userId)) {
            throw new BadRequestException("User not found");
        }
    }

    private UserAccountDto getUser(String username) {
        UserAccountDto user = authService.getUser(username);
        if (user == null) {
            throw new BadRequestException("User not found");
        }
        return user;
    }

    private CardDto checkIdempotencyKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new BadRequestException("Idempotency-Key cannot be null or empty");
        } else {
            return service.getByKey(key);
        }
    }

    private void checkCardAmount(CardForm form) {
        Integer cardAmount = service.getAllActiveCardAmount(form);
        if (cardAmount >= 3) {
            throw new BadRequestException("You cannot create more than 3 cards");
        }
    }

    private void validateForm(CardForm form) {
        // Validate card status
        if (form.getStatus() == null) {
            form.setStatus(CardStatus.ACTIVE.getId());
        } else {
            if (CardStatus.find(form.getStatus()) == null) {
                throw new BadRequestException("Invalid card status");
            }
        }

        // Validate currency
        if (form.getCurrency() == null) {
            form.setCurrency(Currency.UZS.getId());
        } else {
            if (Currency.find(form.getCurrency()) == null) {
                throw new BadRequestException("Invalid currency");
            }
        }
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestParam("Idempotency-Key") String idempotencyKey,
                                 @RequestBody @Valid CardForm form, Authentication authentication) throws NoSuchAlgorithmException {
        // Validate form
        validateForm(form);

        // Check idempotency key
        CardDto card = checkIdempotencyKey(idempotencyKey);
        if (card != null) {
            return ResponseEntity.status(HttpStatus.OK).body(card);
        }

        // Check user
        checkUser(((CustomUserDetails) authentication.getPrincipal()).getUsername(), form.getUserId());

        form.setKey(idempotencyKey);

        // Check card amount
        checkCardAmount(form);

        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(form));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDto> get(@PathVariable("cardId") String cardId, Authentication authentication) {

        // Get current user
        UserAccountDto user = getUser(((CustomUserDetails) authentication.getPrincipal()).getUsername());

        CardDto card = service.getUserCard(user.getId(), cardId);
        if (card == null) {
            throw new BadRequestException("Card with such id not exists in processing.");
        }
        return ResponseEntity.ok().eTag(card.getETag()).body(card);
    }

    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> block(@PathVariable("cardId") String cardId,
                                      @RequestHeader("If-Match") String ifMatch,
                                      Authentication authentication) {
        // Get current user
        UserAccountDto user = getUser(((CustomUserDetails) authentication.getPrincipal()).getUsername());

        // Get card
        CardDto card = service.getUserCardByStatus(user.getId(), cardId, CardStatus.ACTIVE.getId());
        if (card == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!card.getETag().equals(ifMatch)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
        // Change status
        service.changeStatus(card, CardStatus.BLOCKED.getId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{cardId}/unblock")
    public ResponseEntity<Void> unblock(@PathVariable("cardId") String cardId,
                                        @RequestHeader("If-Match") String ifMatch,
                                        Authentication authentication) {
        // Get current user
        UserAccountDto user = getUser(((CustomUserDetails) authentication.getPrincipal()).getUsername());

        // Get card
        CardDto card = service.getUserCardByStatus(user.getId(), cardId, CardStatus.BLOCKED.getId());
        if (card == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (!card.getETag().equals(ifMatch)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
        // Change status
        service.changeStatus(card, CardStatus.ACTIVE.getId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping("/{cardId}/debit")
    public ResponseEntity<TransactionDto> debit(@PathVariable("cardId") String cardId,
                                                @RequestHeader("Idempotency-Key") String idempotencyKey,
                                                @RequestBody @Valid DebitForm form,
                                                Authentication authentication) {
        // Get current user
        UserAccountDto user = getUser(((CustomUserDetails) authentication.getPrincipal()).getUsername());

        TransactionDto dto = transactionService.getByIdemKey(idempotencyKey);
        if (dto != null) {
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }

        // Get card
        CardDto card = service.getUserCardByStatus(user.getId(), cardId, CardStatus.ACTIVE.getId());
        if (card == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!card.getCurrency().equals(form.getCurrency())) {
            List<CurrencyJson> currencies = proxy.getCurrency(Currency.USD.getId(), LocalDate.now().toString());
            form.setExchangeRate((long) (currencies.get(0).getRate() * 100));

            if (card.getCurrency().equals(Currency.USD.getId())) {
                form.setTotalAmount(form.getAmount() / form.getExchangeRate());
            } else {
                form.setTotalAmount(form.getAmount() * form.getExchangeRate());
            }
        }
        if (card.getBalance() < form.getTotalAmount()) {
            throw new BadRequestException("insufficient_funds");
        }
        form.setCardId(card.getCardId());
        form.setUserId(user.getId());
        form.setIdempotencyKey(idempotencyKey);

        return ResponseEntity.ok().body(service.debit(form));
    }

    @PostMapping("/{cardId}/credit")
    public ResponseEntity<TransactionDto> credit(@PathVariable("cardId") String cardId,
                                                 @RequestHeader("Idempotency-Key") String idempotencyKey,
                                                 @RequestBody @Valid CreditForm form,
                                                 Authentication authentication) {
        // Get current user
        UserAccountDto user = getUser(((CustomUserDetails) authentication.getPrincipal()).getUsername());

        TransactionDto dto = transactionService.getByIdemKey(idempotencyKey);
        if (dto != null) {
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }

        // Get card
        CardDto card = service.getUserCardByStatus(user.getId(), cardId, CardStatus.ACTIVE.getId());
        if (card == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!card.getCurrency().equals(form.getCurrency())) {
            List<CurrencyJson> currencies = proxy.getCurrency(Currency.USD.getId(), LocalDate.now().toString());
            form.setExchangeRate((long) (currencies.get(0).getRate() * 100));

            if (card.getCurrency().equals(Currency.USD.getId())) {
                form.setTotalAmount(form.getAmount() / form.getExchangeRate());
            } else {
                form.setTotalAmount(form.getAmount() * form.getExchangeRate());
            }
        }

        form.setCardId(card.getCardId());
        form.setUserId(user.getId());
        form.setIdempotencyKey(idempotencyKey);

        return ResponseEntity.ok().body(service.credit(form));
    }


    @GetMapping("/{cardId}/transactions")
    public ResponseEntity<PageImpl<TransactionDto>> getTransactions(@PathVariable String cardId,
                                                                    @RequestParam String type,
                                                                    @RequestParam("transaction_id") String transactionId,
                                                                    @RequestParam("external_id") String externalId,
                                                                    @RequestParam(defaultValue = "0") Integer page,
                                                                    @RequestParam(defaultValue = "10") Integer size,
                                                                    Authentication authentication) {

        UserAccountDto user = getUser(((CustomUserDetails) authentication.getPrincipal()).getUsername());

        CardDto card = service.getUserCard(user.getId(), cardId);
        if (card == null) {
            throw new BadRequestException("Card with such id not exists in processing.");
        }

        PageForm form = new PageForm(cardId, type, transactionId, externalId, page, size);

        return ResponseEntity.ok().body(transactionService.getHistoryByCard(form));

    }

}
