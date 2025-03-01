package com.renting.rentingwebsite.Service.StripeService;

import com.renting.rentingwebsite.repository.PaymentIntentLogRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    public PaymentIntent createPaymentIntent(long cost) throws StripeException {

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(cost)
                .setCurrency(currency)
                .addPaymentMethodType("card")
                .build();

        return PaymentIntent.create(params);
    }
}
