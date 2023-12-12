package com.challenge.spring.jpa.h2.controller;

import com.challenge.spring.jpa.h2.dto.ConverterDto;
import com.challenge.spring.jpa.h2.exception.ExchangeRateNotFoundException;
import com.challenge.spring.jpa.h2.model.Purchase;
import com.challenge.spring.jpa.h2.repository.PurchaseRepository;
import com.challenge.spring.jpa.h2.service.ConverterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api")
@Validated
public class PurchaseController {
  @Autowired
  PurchaseRepository purchaseRepository;

  @Autowired
  ConverterService converterService;

  @GetMapping("/purchases")
  public ResponseEntity<List<Purchase>> getAllPurchases(@RequestParam(required = false) String id) {
    try {
      List<Purchase> purchases = new ArrayList<Purchase>();

      purchaseRepository.findAll().forEach(purchases::add);

      if (purchases.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(purchases, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/purchases/{id}/{fromCurrency}/{toCurrency}")
  public ResponseEntity<ConverterDto> getPurchaseByIdAndfromCurrencyAndtoCurrency(@PathVariable("id") long id,
                                                                                  @PathVariable("fromCurrency") final String fromCurrency,
                                                                                  @PathVariable("toCurrency") final String toCurrency)
                                                  throws JsonProcessingException {
    ConverterDto conversionResult = null;
    Optional<Purchase> purchase = purchaseRepository.findById(id);

    if(purchase.isPresent()) {
        conversionResult = converterService.convert(fromCurrency, toCurrency, purchase);

        if( conversionResult == null ) {
            throw new ExchangeRateNotFoundException();
        }

        return new ResponseEntity<>(conversionResult, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/purchases")
  public ResponseEntity<Purchase> createPurchase(@RequestBody @Valid Purchase purchase) {
    try {
      Purchase _purchase = purchaseRepository.save(purchase);
      return new ResponseEntity<>(_purchase, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/purchases/{id}")
  public ResponseEntity<Purchase> updatePurchase(@PathVariable("id") long id, @RequestBody Purchase purchase) {
    Optional<Purchase> purchaseData = purchaseRepository.findById(id);

    if (purchaseData.isPresent()) {
      Purchase _purchase = purchaseData.get();
      _purchase.setTransactionDate(purchase.getTransactionDate());
      _purchase.setDescription(purchase.getDescription());
      _purchase.setAmount(purchase.getAmount());
      return new ResponseEntity<>(purchaseRepository.save(_purchase), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/purchases/{id}")
  public ResponseEntity<HttpStatus> deletePurchase(@PathVariable("id") long id) {
    try {
      purchaseRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/purchases")
  public ResponseEntity<HttpStatus> deleteAllPurchases() {
    try {
      purchaseRepository.deleteAll();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }

}
