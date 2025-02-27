package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.StripePaymentInfoRequestDTO;
import com.renting.rentingwebsite.Service.StripeService.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stripe.model.PaymentIntent;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    private StripeService paymentService;

    @Autowired
    public StripeController(StripeService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody StripePaymentInfoRequestDTO paymentInfoRequest) throws StripeException {

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoRequest);
        String paymentStr = paymentIntent.toJson();

        return new ResponseEntity<>(paymentStr, HttpStatus.OK);
    }
}
