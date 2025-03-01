package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.StripeIntentDTO;
import com.renting.rentingwebsite.DTO.StripePaymentInfoRequestDTO;
import com.renting.rentingwebsite.Service.StripeService.StripeService;
import com.renting.rentingwebsite.entities.PaymentIntentLog;
import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.repository.PaymentIntentLogRepository;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import com.stripe.exception.StripeException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stripe.model.PaymentIntent;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    private static final Logger logger = LoggerFactory.getLogger(StripeController.class);

    private StripeService paymentService;

    private RentableItemRepository rentableItemRepository;

    private PaymentIntentLogRepository paymentIntentLogRepository;

    @Autowired
    public StripeController(StripeService paymentService, RentableItemRepository rentableItemRepository, PaymentIntentLogRepository paymentIntentLogRepository) {
        this.paymentService = paymentService;
        this.rentableItemRepository = rentableItemRepository;
        this.paymentIntentLogRepository = paymentIntentLogRepository;
    }

    @PostMapping("/payment-intent")
    public StripeIntentDTO createPaymentIntent(@RequestBody StripePaymentInfoRequestDTO paymentInfoRequest) throws StripeException, BadRequestException {

        long daysBetween = ChronoUnit.DAYS.between(paymentInfoRequest.startDate(), paymentInfoRequest.endDate()) + 1;

        RentableItem rentableItem = rentableItemRepository.findById(paymentInfoRequest.rentable_id())
                .orElseThrow(() -> new BadRequestException("Rentable not found with ID: " + paymentInfoRequest.rentable_id()));

        long price = daysBetween * rentableItem.getPrice();

        logger.info("Created patment intent for {} days with the calculated price being {}", daysBetween, price);

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(price);

        PaymentIntentLog paymentIntentLog = new PaymentIntentLog(
                paymentIntent.getId(),
                paymentInfoRequest.receiptEmail(),
                daysBetween,
                price
        );

        paymentIntentLogRepository.save(paymentIntentLog);

        return new StripeIntentDTO(paymentIntent.getId(), paymentIntent.getClientSecret(), paymentIntent.getPaymentMethodTypes(), daysBetween, price);
    }
}
