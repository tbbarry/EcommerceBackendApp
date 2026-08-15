package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CheckoutSessionData;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.service.StripePaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements StripePaymentService {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${currency.code}")
    private String currencyCode;

    @Value("${app.frontend_app}")
    private String frontendUrl;


    @Override
    public CheckoutSessionData createCheckoutSession(Order order) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(order.getUser().getEmail())
                .setSuccessUrl(frontendUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/payment-cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currencyCode)
                                                .setUnitAmount(order.getTotal()
                                                        .multiply(BigDecimal.valueOf(100))
                                                        .longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Commande " + order.getOrderNumber())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("orderId", order.getId().toString())
                .build();

        Session session = Session.create(params);
        String paymentIntentId  = session.getPaymentIntent();
        System.err.println(paymentIntentId);

        return new CheckoutSessionData(session.getUrl(), paymentIntentId, session.getId());
    }
}
