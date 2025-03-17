package com.renting.rentingwebsite.Service.StripeService;

import com.renting.rentingwebsite.DTO.StripePaymentInfoRequestDTO;
import com.renting.rentingwebsite.entities.RentableItem;
import com.renting.rentingwebsite.entities.Reservation;
import com.renting.rentingwebsite.entities.User;
import com.renting.rentingwebsite.repository.PaymentIntentLogRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import jakarta.annotation.PostConstruct;
import jdk.jshell.spi.ExecutionControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class StripeService {
    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    @Value("${stripe.currency}")
    private String currency;

    @Autowired
    public StripeService(PaymentIntentLogRepository paymentRepository) {
    }

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentIntent createPaymentIntent(
            long cost,
            StripePaymentInfoRequestDTO stripePaymentInfoRequestDTO,
            User user
    ) throws StripeException {

        Map<String, String> metadata = new HashMap<>();
        metadata.put("start_date", stripePaymentInfoRequestDTO.startDate().toString());
        metadata.put("end_date", stripePaymentInfoRequestDTO.endDate().toString());
        metadata.put("rentable_id", String.valueOf(stripePaymentInfoRequestDTO.rentable_id()));
        metadata.put("user_id", user.getId().toString()); // Add User ID for tracking

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(cost)
                .setCurrency(currency)
                .addPaymentMethodType("card")
                .setCustomer(user.getStripeCustomerId())
                .putAllMetadata(metadata)
                .build();

        return PaymentIntent.create(params);
    }

    public Customer createCustomer(String userName, String userEmail) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(userName)
                .setEmail(userEmail)
                .setDescription("")
                .build();

        return Customer.create(params);
    }

    public Refund createRefund(PaymentIntent paymentIntent, User user, String reason) throws StripeException {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntent.getId())
                .setCustomer(user.getStripeCustomerId())
                .setCurrency("DKK")
                .setAmount(paymentIntent.getAmount())
                .setReason(RefundCreateParams.Reason.valueOf(reason))
                .build();
        return Refund.create(params);
    }

    public InvoiceItem createInvoiceItem(Invoice invoice, RentableItem rentableItem, long reservationDays, long price) throws StripeException {
        InvoiceItemCreateParams invoiceItemParams =
                InvoiceItemCreateParams.builder()
                        .setCustomer(invoice.getCustomer())
                        .setInvoice(invoice.getId())
                        .setCurrency(currency)
                        .setDiscountable(true)
                        .setQuantity(reservationDays)
                        .setUnitAmount(rentableItem.getPrice())
                        .setDescription(rentableItem.getName())
                        .build();

        return InvoiceItem.create(invoiceItemParams);
    }

    public Invoice createInvoice(User user, LocalDate startDate) throws StripeException {
        long dueDate = startDate.atTime(23, 59, 59).toEpochSecond(ZoneOffset.ofHours(0));

        var issuer = InvoiceCreateParams.Issuer.builder()
                .setType(InvoiceCreateParams.Issuer.Type.SELF)
                .build();


        InvoiceCreateParams params = InvoiceCreateParams.builder()
                .setCustomer(user.getStripeCustomerId())
                .setCurrency(currency)
                .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
                .setIssuer(issuer)
                .setDescription("If you plan on paying cash please leave this unpaid, it will be marked paid on pickup.")
                .setDueDate(dueDate)
                .build();

        return Invoice.create(params);
    }
}
