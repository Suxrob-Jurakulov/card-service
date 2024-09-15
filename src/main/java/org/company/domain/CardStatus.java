package org.company.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.company.dto.TextDto;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum CardStatus {
    ACTIVE("ACTIVE", new TextDto("ACTIVE", "АКТИВНЫЙ", "FAOL")),
    BLOCKED("BLOCKED", new TextDto("BLOCKED", "ЗАБЛОКИРОВАНО", "BLOKLANGAN")),
    CLOSED("CLOSED", new TextDto("CLOSED", "ЗАКРЫТО", "YOPIQ"));

    private final String id;
    private final TextDto name;

    public static CardStatus find(String id) {
        return Stream.of(CardStatus.values())
                .filter(a -> !id.isBlank() && id.equals(a.getId()))
                .findFirst().orElse(null);
    }

}
