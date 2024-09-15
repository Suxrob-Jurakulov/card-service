package org.company.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Currency {
    USD("USD", "United States Dollar", "$", "US", 840, 2, 100),
    RUB("RUB", "Russian Ruble", "₽", "RU", 643, 2, 100),
    UZS("UZS", "Uzbekistan Som", "сўм", "UZ", 860, 2, 100);

    private final String id;
    private final String name;
    private final String symbol;
    private final String country;
    private final int numericCode;
    private final int decimalPlaces;
    private final int minorUnit;

    public static Currency find(String id) {
        return Stream.of(Currency.values())
                .filter(a -> !id.isBlank() && id.equals(a.getId()))
                .findFirst().orElse(null);
    }
}
