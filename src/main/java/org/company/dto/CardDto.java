package org.company.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {

    private String cardId;
    private Long userId;
    private String status;
    private Long balance;
    private String currency;

    @JsonIgnore
    private String eTag;
}
