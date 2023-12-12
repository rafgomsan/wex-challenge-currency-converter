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

    @Id private long id;
    private BigDecimal exchange_rate;
    private BigDecimal converted_amount;
    private String country_currency_desc;
    private String record_date;
    private String transaction_date;
    private String description;
    private BigDecimal amount;
}
