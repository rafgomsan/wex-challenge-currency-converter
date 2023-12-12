package com.challenge.spring.jpa.h2.service;

import com.challenge.spring.jpa.h2.dto.ConverterDto;
import com.challenge.spring.jpa.h2.exception.ExchangeRateNotFoundException;
import com.challenge.spring.jpa.h2.model.Purchase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConverterService  {

    static final String BASIC_EXCHANGE_RATE_URL = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?sort=-record_date&fields=country_currency_desc,exchange_rate,record_date&filter=country_currency_desc:in:(%s,%s),record_date:lte:%s";
    static final String TAG_DATA = "data";

    private static RestTemplate restTemplate;
    public ConverterService() {
        this.restTemplate = new RestTemplate();
    }

    public final String query(final String from, final String to, final String dt) {
        return String.format(BASIC_EXCHANGE_RATE_URL, from, to, dt);
    }
    public final ConverterDto[] request(final String fromCurrency,
                                        final String toCurrency,
                                        final String dtPurchase) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        return mapper.readValue(mapper.readTree(restTemplate.exchange(query(fromCurrency, toCurrency, dtPurchase),
                        HttpMethod.GET,
                        entity,
                        String.class).getBody()).get(TAG_DATA).toString(),
                        ConverterDto[].class);
    }
    public final ConverterDto getValidExchangeDateRange(final ConverterDto co, final Purchase pu) {
        if(isValidExchangeDateRange(pu.getTransactionDate(), co.getRecord_date())
        ) {
            return buildConversion(co, pu);
        } else {
            throw new ExchangeRateNotFoundException();
        }
    }
    public final ConverterDto buildConversion(final ConverterDto co, final Purchase pu){
        return  ConverterDto.builder()
                .id(pu.getId())
                .exchange_rate(co.getExchange_rate())
                .amount(pu.getAmount())
                .country_currency_desc(co.getCountry_currency_desc())
                .description(pu.getDescription())
                .transaction_date(pu.getTransactionDate())
                .converted_amount(pu.getAmount().multiply(co.getExchange_rate())
                  .setScale(2, BigDecimal.ROUND_HALF_EVEN))
                .build();
    }
    public final ConverterDto convert(final String fromCurrency,
                                      final String toCurrency,
                                      final Optional<Purchase> purchase) throws JsonProcessingException,
                                                                                ExchangeRateNotFoundException {
        List<ConverterDto> converted = Arrays.stream(request(fromCurrency,
                                                             toCurrency,
                                                             purchase.get().getTransactionDate()))
                                                            .limit(1)
                                                            .collect(Collectors.toList());

        if( converted.isEmpty() ) {
            throw new ExchangeRateNotFoundException();
        }
        else {
            return getValidExchangeDateRange(converted.get(0), purchase.get());
        }
    }
    private Boolean isValidExchangeDateRange(final String startDate, final String endDate){
        long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.parse(startDate).withDayOfMonth(1),
                                                       LocalDate.parse(endDate).withDayOfMonth(1));
        return monthsBetween <= 6;
    }
}
