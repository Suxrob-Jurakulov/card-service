package org.company.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyJson {

    private Integer id;

    @JsonAlias({"Code", "code"})
    private String code;

    @JsonAlias({"Ccy", "ccy"})
    private String ccy;

    @JsonAlias({"CcyNm_RU", "CcyNm_RU"})
    private String ccyNmRU;

    @JsonAlias({"CcyNm_UZ", "ccyNm_UZ"})
    private String ccyNmUZ;

    @JsonAlias({"CcyNm_UZC", "ccyNm_UZC"})
    private String ccyNmUZC;

    @JsonAlias({"CcyNm_EN", "ccyNm_EN"})
    private String ccyNmEN;

    @JsonAlias({"Nominal", "nominal"})
    private String nominal;

    @JsonAlias({"Rate", "rate"})
    private Double rate;

    @JsonAlias({"Diff", "diff"})
    private String diff;

    @JsonAlias({"Date", "date"})
    private String date;
}
