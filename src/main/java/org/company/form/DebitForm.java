package org.company.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebitForm {

    @NotNull
    private String externalId;

    @NotNull
    private Long amount;

    private String currency;

    @NotNull
    private String purpose;

    // Other elements
    private String cardId;

    private Long userId;

    private Long totalAmount;

    private Long exchangeRate;

    private String idempotencyKey;
}
