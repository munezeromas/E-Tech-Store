package gencoders.e_tech_store_app.service;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import gencoders.e_tech_store_app.dto.PaymentRequest;
import gencoders.e_tech_store_app.dto.PaymentResponse;
import gencoders.e_tech_store_app.model.Payment;
import gencoders.e_tech_store_app.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {

    private final PayPalHttpClient payPalHttpClient;

    public PaymentResponse createOrder(PaymentRequest request, Payment payment) {
        try {
            OrderRequest orderRequest = buildOrderRequest(request, payment);
            OrdersCreateRequest createRequest = new OrdersCreateRequest().requestBody(orderRequest);

            HttpResponse<Order> response = payPalHttpClient.execute(createRequest);
            Order order = response.result();

            String receiptUrl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .map(LinkDescription::href)
                    .findFirst()
                    .orElse(null);

            PaymentResponse res = new PaymentResponse();
            res.setPaymentId(payment.getId());
            res.setTransactionId(order.id());
            res.setAmount(payment.getAmount());
            res.setCurrencyCode(request.getCurrencyCode());
            res.setStatus(PaymentStatus.PENDING);
            res.setPaymentDate(LocalDateTime.now());
            res.setMessage("PayPal order created. Awaiting approval.");
            res.setPaymentMethod("PAYPAL");
            res.setGatewayResponse("PayPal ORDER_CREATED");
            res.setRequiresAction(true);
            res.setActionUrl(receiptUrl);
            res.setReceiptUrl(receiptUrl);
            return res;

        } catch (IOException e) {
            log.error("PayPal order creation failed: {}", e.getMessage());
            throw new RuntimeException("PayPal order creation failed", e);
        }
    }

    public PaymentResponse captureOrder(String orderId) {
        try {
            OrdersCaptureRequest captureRequest = new OrdersCaptureRequest(orderId);
            HttpResponse<Order> response = payPalHttpClient.execute(captureRequest);
            Order order = response.result();

            PaymentResponse res = new PaymentResponse();
            res.setTransactionId(order.id());
            res.setStatus(PaymentStatus.COMPLETED);
            res.setMessage("Payment captured successfully");
            res.setPaymentMethod("PAYPAL");
            res.setPaymentDate(LocalDateTime.now());
            res.setGatewayResponse("PayPal CAPTURED");
            return res;

        } catch (IOException e) {
            log.error("PayPal capture failed: {}", e.getMessage());
            throw new RuntimeException("PayPal capture failed", e);
        }
    }

    private OrderRequest buildOrderRequest(PaymentRequest request, Payment payment) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext context = new ApplicationContext()
                .brandName("E-Tech Rwanda Store")
                .landingPage("BILLING")
                .cancelUrl("https://yourdomain.com/cancel")
                .returnUrl("https://yourdomain.com/success")
                .userAction("PAY_NOW");

        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode(request.getCurrencyCode())
                .value(payment.getAmount().toPlainString());

        PurchaseUnitRequest unit = new PurchaseUnitRequest()
                .description("E-Tech Rwanda Order")
                .amountWithBreakdown(amount);

        orderRequest.applicationContext(context);
        orderRequest.purchaseUnits(List.of(unit));

        return orderRequest;
    }
}
