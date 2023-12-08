package com.challenge.spring.jpa.h2.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConverterDto implements Serializable {

    static final String BASIC_EXCHANGE_RATE_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?sort=-record_date&fields=country_currency_desc,exchange_rate,record_date&filter=country_currency_desc:in:(%s,%s),record_date:lte:%s";
    static final String TAG_DATA = "data";

    @Id private long id;
    private BigDecimal exchange_rate;
    private BigDecimal converted_amount;
    private String country_currency_desc;

    private String record_date;
    private String transaction_date;
    private String description;
    private BigDecimal amount;
}
