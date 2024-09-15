package org.company.proxy;

import org.company.json.CurrencyJson;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Sukhrob
 */

@FeignClient(name = "cbu-service", url = "${cbu.api.url}")
public interface CBUProxy {

    @GetMapping("/arkhiv-kursov-valyut/json/{currencyId}/{date}")
    List<CurrencyJson> getCurrency(@PathVariable String currencyId, @PathVariable String date);

}
