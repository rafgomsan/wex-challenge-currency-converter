package com.challenge.spring.jpa.h2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Purchase implements Serializable {

  public long getId() {
    return id;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Column(name = "transaction_date")
  private String transactionDate;

  @NotBlank(message = "Invalid description: Empty name")
  @NotNull(message = "Invalid description: Name is NULL")
  @Size(min = 3, max = 50, message = "Invalid description: Exceeds 50 characters")
  @Column(name = "description")
  private String description;

  @Column(name = "amount")
  private BigDecimal amount;

}
