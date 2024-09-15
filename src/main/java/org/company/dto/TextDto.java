package org.company.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextDto {

    private String en;
    private String ru;
    private String uz;

    public static String get(String lang) {
        String result = "en";
        if (lang.equals("ru")) {
            result = "ru";
        } else if (lang.equals("uz")) {
            result = "uz";
        }
        return result;
    }
}
