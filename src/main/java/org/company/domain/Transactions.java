package org.company.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String idemKey;

    @Column(nullable = false)
    private String externalId;

    @Column(nullable = false)
    private String cardId;

    @Column
    private String type;

    @Column(nullable = false)
    private Long amount;

    @Column
    private Long afterBalance;

    @Column
    private String currency;

    @Column
    private String purpose;

    @Column
    private Long exchangeRate;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
