package com.renting.rentingwebsite.Service.StripeService;

import com.nimbusds.jose.shaded.gson.JsonElement;
import com.renting.rentingwebsite.DTO.ReservationDTO;
import com.renting.rentingwebsite.Service.ReservationService;
import com.renting.rentingwebsite.entities.PaymentIntentLog;
import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.enums.PaymentIntentStatus;
import com.renting.rentingwebsite.repository.PaymentIntentLogRepository;
import com.renting.rentingwebsite.repository.RentableItemRepository;
import com.renting.rentingwebsite.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.service.PaymentIntentService;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WebhookService {

    private final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final ReservationService reservationService;
    private final UserRepository userRepository;
    private final RentableItemRepository rentableItemRepository;
    private final PaymentIntentLogRepository paymentIntentLogRepository;
    private final StripeService stripeService;

    public WebhookService(ReservationService reservationService, UserRepository userRepository, RentableItemRepository rentableItemRepository, PaymentIntentLogRepository paymentIntentLogRepository, StripeService stripeService) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
        this.rentableItemRepository = rentableItemRepository;
        this.paymentIntentLogRepository = paymentIntentLogRepository;
        this.stripeService = stripeService;
    }

    public void HandleWebhook(Event event) throws BadRequestException, StripeException {

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            throw new BadRequestException("Object couldnt be deserialized");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent successPaymentIntent = (PaymentIntent) stripeObject;
                handlePaymentSuccess(successPaymentIntent);
                break;
            case "payment_intent.payment_failed":
                PaymentIntent failedPaymentIntent = (PaymentIntent) stripeObject;
                handlePaymentFailed(failedPaymentIntent);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
    }

    private void handlePaymentFailed(PaymentIntent paymentIntent) throws StripeException, BadRequestException {

        logger.error("Payment intent failed recieved with status {}", paymentIntent.getStatus());

        PaymentIntentLog paymentIntentLog = paymentIntentLogRepository.findByPaymentIntentId(paymentIntent.getId()).orElseThrow(
                () -> new BadRequestException("Payment intent does not exist in database")
        );

        if (PaymentIntentStatus.SUCCEEDED == paymentIntentLog.getStatus()) {
            throw new BadRequestException("Payment has already been succeeded");
        }

        paymentIntentLog.setStatus(PaymentIntentStatus.getPaymentIntentStatus(paymentIntent.getStatus()));

        paymentIntentLogRepository.save(paymentIntentLog);
    }

    private void handlePaymentSuccess(PaymentIntent paymentIntent) throws BadRequestException, StripeException {

        System.out.println("✅ Successfully deserialized PaymentIntent: " + paymentIntent.getId());

        PaymentIntentLog paymentIntentLog = paymentIntentLogRepository.findByPaymentIntentId(paymentIntent.getId()).orElseThrow(
                () -> new BadRequestException("Payment intent does not exist in database")
        );

        paymentIntentLog.setStatus(PaymentIntentStatus.getPaymentIntentStatus(paymentIntent.getStatus()));
        paymentIntentLogRepository.save(paymentIntentLog);

        // 4️⃣ Confirm the status is succeeded
        if (!"succeeded".equals(paymentIntent.getStatus())) {
            System.out.println("❌ Payment intent is not succeeded, current status: " + paymentIntent.getStatus());
            return;
        }

        // 5️⃣ Extract metadata and create reservation
        String startDateStr = paymentIntent.getMetadata().get("start_date");
        String endDateStr = paymentIntent.getMetadata().get("end_date");
        String rentableIdStr = paymentIntent.getMetadata().get("rentable_id");
        String userIdStr = paymentIntent.getMetadata().get("user_id");

        if (startDateStr == null || endDateStr == null || rentableIdStr == null || userIdStr == null) {
            System.out.println("❌ Missing metadata in payment intent");
            return;
        }

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        Long rentableId = Long.parseLong(rentableIdStr);

        User user = userRepository.findByStripeCustomerId(paymentIntent.getCustomer()).orElseThrow(
                () -> new BadRequestException("User could not be found with this customer ID")
        );

        RentableItem rentableItem = rentableItemRepository.findById(rentableId)
                .orElseThrow(() -> new BadRequestException("Rentable item not found with ID: " + rentableId));

        try {
            reservationService.createReservation(
                    startDate,
                    endDate,
                    rentableItem,
                    true,
                    paymentIntent,
                    user
            );
        } catch (Exception e) {
            stripeService.createRefund(paymentIntent, user, "Could not create reservation");
            paymentIntentLog.setStatus(PaymentIntentStatus.REFUNDED);
            paymentIntentLogRepository.save(paymentIntentLog);
            throw e;
        }
    }

}
