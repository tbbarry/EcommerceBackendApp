package com.backend.ecommerce.service.email;

import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.OrderEmailNotification;
import com.backend.ecommerce.entity.OrderItem;
import com.backend.ecommerce.entity.OrderMessageTemplate;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.repository.OrderEmailNotificationRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.OrderMessageTemplateRepository;
import com.backend.ecommerce.repository.StoreSettingsRepository;
import com.backend.ecommerce.service.StoreSettingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderNotificationTest {
    private StoreSettingsService store(String name) {
        return new StoreSettingsService(mock(StoreSettingsRepository.class), name);
    }
    @Mock OrderEmailNotificationRepository repository;
    @Mock OrderEmailSender sender;
    @Mock JavaMailSender mailSender;
    @Mock EmailService emailService;
    @Mock OrderRepository orders;
    @Mock OrderMessageTemplateRepository templateRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    OrderNotificationDelivery delivery;

    @BeforeEach
    void setUp() {
        delivery = new OrderNotificationDelivery(repository, sender);
    }

    @Test
    void queueCapturesOnlyActualRelevantTransitions() {
        User user = User.builder().firstname("Léa").email("lea@example.test").build();
        Order order = Order.builder().user(user).orderNumber("CMD-1").total(new BigDecimal("25.00"))
                .status(OrderStatus.PENDING).build();
        order.setId(8);
        OrderNotificationService service = new OrderNotificationService(repository, objectMapper, new OrderMessageTemplateService(templateRepository), "admin@example.test");
        service.queueStatusChange(order, OrderStatus.PENDING);
        service.queueStatusChange(order, OrderStatus.CANCELLED);
        verifyNoInteractions(repository);

        order.setStatus(OrderStatus.SHIPPED);
        service.queueStatusChange(order, OrderStatus.PROCESSING);
        ArgumentCaptor<OrderEmailNotification> captured = ArgumentCaptor.forClass(OrderEmailNotification.class);
        verify(repository).save(captured.capture());
        OrderEmailNotification notification = captured.getValue();
        assertEquals(8, notification.getOrderId());
        assertEquals("CMD-1", notification.getOrderNumber());
        assertEquals("lea@example.test", notification.getRecipient());
        assertEquals(OrderStatus.SHIPPED, notification.getOrderStatus());
        assertEquals(new BigDecimal("25.00"), notification.getTotal());
        assertNotNull(notification.getNextAttemptAt());
        assertNull(notification.getSentAt());
    }

    @Test
    void paidOrderQueuesSeparateCustomerAndAdminSnapshotsOnlyOnce() throws Exception {
        User user = User.builder().firstname("Léa").email("lea@example.test").build();
        OrderItem item = OrderItem.builder().productNameSnapshot("Robe").colorSnapshot("Bleu")
                .sizeSnapshot("M").quantity(2).build();
        Order order = Order.builder().user(user).orderNumber("CMD-1").total(new BigDecimal("25.00"))
                .status(OrderStatus.PAID).orderItems(List.of(item)).build();
        order.setId(8);
        OrderNotificationService service = new OrderNotificationService(repository, objectMapper, new OrderMessageTemplateService(templateRepository), "admin@example.test");

        service.queuePaymentConfirmed(order, OrderStatus.PENDING);
        service.queuePaymentConfirmed(order, OrderStatus.PAID);
        ArgumentCaptor<OrderEmailNotification> captured = ArgumentCaptor.forClass(OrderEmailNotification.class);
        verify(repository, times(2)).save(captured.capture());
        var customer = captured.getAllValues().get(0);
        var admin = captured.getAllValues().get(1);
        assertEquals("lea@example.test", customer.getRecipient());
        assertEquals("admin@example.test", admin.getRecipient());
        assertFalse(customer.getAdminNotification());
        assertTrue(admin.getAdminNotification());
        assertEquals(customer.getItemsSnapshot(), admin.getItemsSnapshot());
        item.setProductNameSnapshot("Modifié");
        assertEquals("Robe", objectMapper.readValue(customer.getItemsSnapshot(), OrderEmailItem[].class)[0].name());
    }

    @Test
    void configuredMessageIsCapturedForRetriesAndVariablesAreResolved() {
        when(templateRepository.findById("SHIPPED")).thenReturn(Optional.of(
                new OrderMessageTemplate("SHIPPED", "Order {{order_number}}",
                        "Hi {{customer_name}}, your order has shipped.")));
        User user = User.builder().firstname("Alex").email("alex@example.test").build();
        Order order = Order.builder().user(user).orderNumber("ORD-8").total(BigDecimal.TEN)
                .status(OrderStatus.SHIPPED).build();
        order.setId(8);
        new OrderNotificationService(repository, objectMapper, new OrderMessageTemplateService(templateRepository),
                "admin@example.test").queueStatusChange(order, OrderStatus.PROCESSING);
        ArgumentCaptor<OrderEmailNotification> captured = ArgumentCaptor.forClass(OrderEmailNotification.class);
        verify(repository).save(captured.capture());
        assertEquals("Order ORD-8", captured.getValue().getSubjectSnapshot());
        assertEquals("Hi Alex, your order has shipped.", captured.getValue().getMessageSnapshot());
        new OrderEmailSender(emailService, store("Store"), "https://shop.example.test", "USD", orders, objectMapper)
                .send(captured.getValue());
        verify(emailService).sendOrder(eq("alex@example.test"), contains("Order ORD-8"),
                argThat(values -> values.get("message").contains("Hi Alex")), anyString(), anyList());
    }

    @Test
    void templateRejectsUnknownVariablesAndSubjectLineBreaks() {
        OrderMessageTemplateService service = new OrderMessageTemplateService(templateRepository);
        assertThrows(IllegalArgumentException.class, () -> service.update("SHIPPED",
                new OrderMessageTemplateService.Update("Subject", "Hello {{unknown}}")));
        assertThrows(IllegalArgumentException.class, () -> service.update("SHIPPED",
                new OrderMessageTemplateService.Update("Subject\nInjected", "Hello")));
        assertThrows(IllegalArgumentException.class, () -> service.update("UNKNOWN",
                new OrderMessageTemplateService.Update("Subject", "Hello")));
        verifyNoInteractions(templateRepository);
    }

    @Test
    void manualStatusChangeDoesNotSendThePaidOrderAlertToAdmin() {
        User user = User.builder().firstname("Léa").email("lea@example.test").build();
        Order order = Order.builder().user(user).orderNumber("CMD-1").total(new BigDecimal("25.00"))
                .status(OrderStatus.PAID).build();
        order.setId(8);
        new OrderNotificationService(repository, objectMapper, new OrderMessageTemplateService(templateRepository), "admin@example.test")
                .queueStatusChange(order, OrderStatus.PENDING);
        ArgumentCaptor<OrderEmailNotification> captured = ArgumentCaptor.forClass(OrderEmailNotification.class);
        verify(repository).save(captured.capture());
        assertEquals("lea@example.test", captured.getValue().getRecipient());
        assertFalse(captured.getValue().getAdminNotification());
    }

    @Test
    void requiresAValidAdminRecipient() {
        assertThrows(IllegalStateException.class, () -> new OrderNotificationService(repository, objectMapper, new OrderMessageTemplateService(templateRepository), ""));
        assertThrows(IllegalStateException.class, () -> new OrderNotificationService(repository, objectMapper, new OrderMessageTemplateService(templateRepository), "invalid"));
    }

    @Test
    void smtpFailureLeavesNotificationPendingWithBackoff() {
        OrderEmailNotification notification = OrderEmailNotification.builder().attempts(1).build();
        notification.setId(5);
        when(repository.lockNextReady(any())).thenReturn(Optional.of(notification));
        doThrow(new EmailDeliveryException("SMTP", new MailSendException("offline"))).when(sender).send(notification);
        LocalDateTime before = LocalDateTime.now();
        assertTrue(delivery.sendNext());
        assertNull(notification.getSentAt());
        assertEquals(2, notification.getAttempts());
        assertFalse(notification.getNextAttemptAt().isBefore(before.plusMinutes(2)));
        verify(repository).save(notification);
    }

    @Test
    void successfulDeliveryIsMarkedSentAndEmptyQueueStops() {
        OrderEmailNotification notification = OrderEmailNotification.builder().build();
        when(repository.lockNextReady(any())).thenReturn(Optional.of(notification), Optional.empty());
        assertTrue(delivery.sendNext());
        assertNotNull(notification.getSentAt());
        assertFalse(delivery.sendNext());
        verify(sender).send(notification);
        verify(repository).save(notification);
    }

    @Test
    void senderUsesStatusSnapshotAndConfiguredFrontend() {
        OrderEmailNotification notification = OrderEmailNotification.builder()
                .recipient("lea@example.test").customerName("Léa").orderNumber("CMD-1")
                .orderStatus(OrderStatus.SHIPPED).total(new BigDecimal("25.00"))
                .itemsSnapshot("[{\"name\":\"Robe\",\"color\":\"Bleu\",\"size\":\"M\",\"quantity\":2}]").build();
        new OrderEmailSender(emailService, store("Boutique"), "https://shop.example.test/", "usd", orders, objectMapper).send(notification);
        verify(emailService).sendOrder(eq("lea@example.test"), contains("shipped"),
                argThat(values -> values.get("status").equals("Shipped")
                        && values.get("link").equals("https://shop.example.test/my-orders")
                        && values.get("order_number").equals("CMD-1")),
                contains("Robe (Bleu · M) × 2"), anyList());
    }

    @Test
    void senderProvidesASeparateMessageForEachNotifiedStatus() {
        OrderEmailSender composer = new OrderEmailSender(emailService, store("Boutique"), "https://shop.example.test", "usd", orders, objectMapper);
        OrderEmailNotification notification = OrderEmailNotification.builder()
                .recipient("lea@example.test").customerName("Léa").orderNumber("CMD-1")
                .total(new BigDecimal("25.00")).itemsSnapshot("[]").build();
        for (OrderStatus status : new OrderStatus[]{OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED,
                OrderStatus.DELIVERED, OrderStatus.CANCELLED}) {
            notification.setOrderStatus(status);
            composer.send(notification);
        }
        verify(emailService).sendOrder(anyString(), contains("confirmed"), anyMap(), anyString(), anyList());
        verify(emailService).sendOrder(anyString(), contains("prepared"), anyMap(), anyString(), anyList());
        verify(emailService).sendOrder(anyString(), contains("shipped"), anyMap(), anyString(), anyList());
        verify(emailService).sendOrder(anyString(), contains("delivered"), anyMap(), anyString(), anyList());
        verify(emailService).sendOrder(anyString(), contains("canceled"), anyMap(), anyString(), anyList());
    }

    @Test
    void adminEmailLinksToOrderManagementAndLegacyNotificationsLoadItems() {
        Order order = Order.builder().orderItems(List.of(OrderItem.builder().productNameSnapshot("Chemise")
                .colorSnapshot("").quantity(1).build())).build();
        when(orders.findById(8)).thenReturn(Optional.of(order));
        OrderEmailNotification notification = OrderEmailNotification.builder().orderId(8)
                .recipient("admin@example.test").customerName("Léa").orderNumber("CMD-1")
                .adminNotification(true).orderStatus(OrderStatus.PAID).total(new BigDecimal("25.00")).build();
        new OrderEmailSender(emailService, store("Boutique"), "https://shop.example.test", "usd", orders, objectMapper)
                .send(notification);
        verify(emailService).sendOrder(eq("admin@example.test"), contains("paid order"),
                argThat(values -> values.get("link").endsWith("/admin/orders?orderId=8")
                        && values.get("message").contains("Léa")),
                contains("Chemise × 1"), anyList());
    }

    @Test
    void authenticationTemplateIsUtf8EscapedAndKeepsOriginalLink() throws Exception {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);
        new EmailService(mailSender).sendMail("client@example.test", "<script>é</script> {{link}}",
                "https://shop.example.test/verify?a=1&b=2", "Boutique", EmailTemplate.ACTIVATION_ACCOUNT);
        verify(mailSender).send(message);
        message.saveChanges();
        assertTrue(message.getSubject().contains("Activate"));
        String html = findHtml(message.getContent());
        assertNotNull(html);
        assertTrue(html.contains("&lt;script&gt;"));
        assertFalse(html.contains("<script>"));
        assertTrue(html.contains("{{link}}"));
        assertTrue(html.contains("a=1&amp;b=2"));
    }

    @Test
    void orderEmailRendersUtf8HtmlAndPlainText() throws Exception {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);
        OrderEmailNotification notification = OrderEmailNotification.builder()
                .recipient("lea@example.test").customerName("<Léa>").orderNumber("CMD-1")
                .orderStatus(OrderStatus.DELIVERED).total(new BigDecimal("25.00"))
                .itemsSnapshot("[{\"name\":\"<script>Robe</script>\",\"color\":\"Bleu & blanc\",\"size\":\"M\",\"quantity\":2}]").build();
        new OrderEmailSender(new EmailService(mailSender), store("Boutique"), "https://shop.example.test", "usd", orders, objectMapper)
                .send(notification);
        verify(mailSender).send(message);
        message.saveChanges();
        assertTrue(message.getSubject().contains("delivered"));
        String html = findHtml(message.getContent());
        assertNotNull(html);
        assertTrue(html.contains("&lt;Léa&gt;"));
        assertTrue(html.contains("https://shop.example.test/my-orders"));
        assertTrue(html.contains("CMD-1"));
        assertTrue(html.contains("&lt;script&gt;Robe&lt;/script&gt;"));
        assertTrue(html.contains("Bleu &amp; blanc"));
        assertFalse(html.contains("<script>"));
        assertFalse(html.contains("<img"));
        assertFalse(html.contains("{{"));
        assertTrue(findPlainText(message.getContent()).contains("<script>Robe</script> (Bleu & blanc · M) × 2"));
    }

    @Test
    void smtpErrorPreservesItsCause() {
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));
        doThrow(new MailSendException("offline")).when(mailSender).send(any(MimeMessage.class));
        EmailDeliveryException error = assertThrows(EmailDeliveryException.class,
                () -> new EmailService(mailSender).sendMail("client@example.test", "Léa",
                        "https://shop.example.test", "Boutique", EmailTemplate.RESET_PASSWORD));
        assertInstanceOf(MailSendException.class, error.getCause());
    }

    @Test
    void schedulerIsRegistered() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(OrderNotificationDelivery.class, () -> delivery);
            context.register(OrderNotificationScheduler.class);
            context.refresh();
            assertEquals(1, context.getBean(ScheduledAnnotationBeanPostProcessor.class).getScheduledTasks().size());
        }
    }

    @Test
    void sendsThroughAnIsolatedLocalSmtpStub() throws Exception {
        try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            server.setSoTimeout(5000);
            CompletableFuture<String> received = CompletableFuture.supplyAsync(() -> {
                try (var socket = server.accept();
                     var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                     var out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                    socket.setSoTimeout(5000);
                    out.print("220 localhost test\r\n");
                    out.flush();
                    StringBuilder data = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("EHLO") || line.startsWith("HELO")) {
                            out.print("250 localhost\r\n");
                        } else if (line.equals("DATA")) {
                            out.print("354 Continue\r\n");
                            out.flush();
                            while ((line = in.readLine()) != null && !line.equals(".")) data.append(line).append('\n');
                            out.print("250 Accepted\r\n");
                        } else if (line.equals("QUIT")) {
                            out.print("221 Bye\r\n");
                            out.flush();
                            break;
                        } else {
                            out.print("250 OK\r\n");
                        }
                        out.flush();
                    }
                    return data.toString();
                } catch (Exception e) {
                    throw new java.util.concurrent.CompletionException(e);
                }
            });
            JavaMailSenderImpl localMail = new JavaMailSenderImpl();
            localMail.setHost(server.getInetAddress().getHostAddress());
            localMail.setPort(server.getLocalPort());
            localMail.getJavaMailProperties().put("mail.smtp.connectiontimeout", "5000");
            localMail.getJavaMailProperties().put("mail.smtp.timeout", "5000");
            OrderEmailNotification notification = OrderEmailNotification.builder()
                    .recipient("client@example.test").customerName("Léa").orderNumber("CMD-1")
                    .orderStatus(OrderStatus.PAID).total(new BigDecimal("25.00")).itemsSnapshot("[]").build();
            new OrderEmailSender(new EmailService(localMail), store("Boutique"), "https://shop.example.test", "usd", orders, objectMapper)
                    .send(notification);
            String message = received.get(10, TimeUnit.SECONDS);
            assertTrue(message.contains("Subject:"));
            assertTrue(message.contains("multipart"));
        }
    }

    private String findHtml(Object content) throws Exception {
        return findPart(content, "text/html");
    }

    private String findPlainText(Object content) throws Exception {
        return findPart(content, "text/plain");
    }

    private String findPart(Object content, String type) throws Exception {
        if (!(content instanceof Multipart multipart)) return null;
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (part.isMimeType(type)) return (String) part.getContent();
            String nested = findPart(part.getContent(), type);
            if (nested != null) return nested;
        }
        return null;
    }
}
