package com.renting.rentingwebsite.Service.StripeService;

import com.renting.rentingwebsite.DTO.StripePaymentInfoRequestDTO;
import com.renting.rentingwebsite.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StripeService {
    private PaymentRepository paymentRepository;

    @Autowired
    public StripeService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        Stripe.apiKey = "sk_test_ggZzlAIjmzcrw53GTbwMylep";
    }

    public PaymentIntent createPaymentIntent(StripePaymentInfoRequestDTO paymentInfoRequest) throws StripeException {
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentInfoRequest.amount());
        params.put("currency", "dkk");
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }
}
