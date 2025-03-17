package com.renting.rentingwebsite.controller;

import com.renting.rentingwebsite.DTO.PaymentStatus;
import com.renting.rentingwebsite.DTO.ReservationDTO;
import com.renting.rentingwebsite.DTO.StripeIntentDTO;
import com.renting.rentingwebsite.DTO.StripePaymentInfoRequestDTO;
import com.renting.rentingwebsite.Service.StripeService.StripeService;
import com.renting.rentingwebsite.Service.StripeService.WebhookService;
import com.renting.rentingwebsite.entities.PaymentIntentLog;
import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.Reservation;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.enums.PaymentIntentStatus;
import com.renting.rentingwebsite.repository.PaymentIntentLogRepository;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import com.renting.rentingwebsite.repository.ReservationRepository;
import com.renting.rentingwebsite.security.CurrentUser;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.annotation.security.PermitAll;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.PaymentIntent;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    private final StripeService stripeService;
    private final ReservationRepository reservationRepository;
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private static final Logger logger = LoggerFactory.getLogger(StripeController.class);

    private final WebhookService webhookService;

    private StripeService paymentService;

    private RentableItemRepository rentableItemRepository;

    private PaymentIntentLogRepository paymentIntentLogRepository;

    @Autowired
    public StripeController(WebhookService webhookService, StripeService paymentService, RentableItemRepository rentableItemRepository, PaymentIntentLogRepository paymentIntentLogRepository, StripeService stripeService, ReservationRepository reservationRepository) {
        this.webhookService = webhookService;
        this.paymentService = paymentService;
        this.rentableItemRepository = rentableItemRepository;
        this.paymentIntentLogRepository = paymentIntentLogRepository;
        this.stripeService = stripeService;
        this.reservationRepository = reservationRepository;
    }

    @PostMapping("/webhook")
    @PermitAll
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) throws BadRequestException, StripeException {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        webhookService.HandleWebhook(event);

        return ResponseEntity.ok("Webhook received");
    }

    @PostMapping("/payment-intent")
    @PreAuthorize("isAuthenticated()")
    public StripeIntentDTO createPaymentIntent(@RequestBody StripePaymentInfoRequestDTO paymentInfoRequest, @CurrentUser User user) throws StripeException, BadRequestException {
        var startDate = paymentInfoRequest.startDate();
        var endDate = paymentInfoRequest.endDate();

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        RentableItem rentableItem = rentableItemRepository.findById(paymentInfoRequest.rentable_id())
                .orElseThrow(() -> new BadRequestException("Rentable not found with ID: " + paymentInfoRequest.rentable_id()));

        long price = daysBetween * rentableItem.getPrice();

        logger.info("Created patment intent for {} days with the calculated price being {}", daysBetween, price);

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(price, paymentInfoRequest, user);

        PaymentIntentLog paymentIntentLog = new PaymentIntentLog(
                paymentIntent.getId(),
                user,
                price
        );

        paymentIntentLogRepository.save(paymentIntentLog);

        return new StripeIntentDTO(paymentIntent.getId(), paymentIntent.getClientSecret(), paymentIntent.getPaymentMethodTypes(), daysBetween, price);
    }

    @PostMapping("/invoice")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Reservation> createInvocie(@RequestBody StripePaymentInfoRequestDTO paymentInfoRequest, @CurrentUser User user) throws StripeException, BadRequestException {
        var startDate = paymentInfoRequest.startDate();
        var endDate = paymentInfoRequest.endDate();

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        RentableItem rentableItem = rentableItemRepository.findById(paymentInfoRequest.rentable_id())
                .orElseThrow(() -> new BadRequestException("Rentable not found with ID: " + paymentInfoRequest.rentable_id()));

        long price = daysBetween * rentableItem.getPrice();

        logger.info("Reservation will be created for {} days with the calculated price being {}", daysBetween, price);

        var invoice = stripeService.createInvoice(user, startDate);

        var reservation = new Reservation(startDate, endDate, false, false, user, rentableItem, invoice.getId());

        reservationRepository.save(reservation);

        stripeService.createInvoiceItem(invoice, rentableItem, daysBetween, price);

        invoice.finalizeInvoice();

        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/payment-status/{paymentIntent}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentStatus> paymentStatus(@PathVariable String paymentIntent, @CurrentUser User user ) throws BadRequestException {

        var paymentLog = paymentIntentLogRepository.findByPaymentIntentId(paymentIntent).orElseThrow(
                () -> new BadRequestException("Payment intent not found with ID: " + paymentIntent)
        );

        if (!Objects.equals(paymentLog.getUser().getId(), user.getId())) {
            throw new BadRequestException("You are not allowed to see if this payment status");
        }

        if (paymentLog.getStatus() == PaymentIntentStatus.SUCCEEDED) {
            return ResponseEntity.ok(new PaymentStatus(PaymentIntentStatus.SUCCEEDED, false));
        }

        return ResponseEntity.ok(new PaymentStatus(paymentLog.getStatus(), true));

    }
}
