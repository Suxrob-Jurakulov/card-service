package org.company.form.card;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardForm {

    @NotNull(message = "User ID is required")
    private Long userId;

    private String status;
    private String currency;

    @Min(value = 0, message = "Initial amount must be greater than or equal to 0")
    @Max(value = 10000, message = "Initial amount cannot exceed 10000")
    private Long initialAmount = 0L;

    // Other elements
    private String key;
}
