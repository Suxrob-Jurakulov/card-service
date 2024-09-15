package org.company.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private String id;
    private String externalId;
    private String cardId;
    private Long amount;
    private Long afterBalance;
    private String currency;
    private String purpose;
    private Long exchangeRate;
    private LocalDateTime createdAt;

    @JsonIgnore
    private String idemKey;
}
