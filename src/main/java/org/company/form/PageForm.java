package org.company.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageForm {

    private String cardId;
    private String type;
    private String transactionId;
    private String externalId;
    private Integer page;
    private Integer size;
}
