package com.backend.ecommerce.service.email;

import com.backend.ecommerce.entity.OrderMessageTemplate;
import com.backend.ecommerce.repository.OrderMessageTemplateRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderMessageTemplateServiceTest {
    private final OrderMessageTemplateRepository repository = mock(OrderMessageTemplateRepository.class);
    private final OrderMessageTemplateService service = new OrderMessageTemplateService(repository);

    @Test
    void accountDefaultsUseOnlySafeVariablesAndRetainTheExpectedExpiryTemplates() {
        assertEquals("Activate your account - {{site_name}}",
                service.get(OrderMessageTemplateService.ACCOUNT_ACTIVATION).subject());
        assertEquals("Reset your password - {{site_name}}",
                service.get(OrderMessageTemplateService.PASSWORD_RESET).subject());
        assertEquals("Thanks for joining {{site_name}}.",
                service.get(OrderMessageTemplateService.ACCOUNT_ACTIVATION).message());
        assertEquals("Welcome, Alex from Store.",
                OrderMessageTemplateService.resolveAccount("Welcome, {{user_name}} from {{site_name}}.", "Alex", "Store"));
    }

    @Test
    void accountTemplatesCanBeUpdatedButCannotControlSecureLinksOrAcceptOtherVariables() {
        var update = new OrderMessageTemplateService.Update(
                "Hello {{user_name}} from {{site_name}}", "Hi {{user_name}}, welcome to {{site_name}}.");

        assertEquals(update.subject(),
                service.update(OrderMessageTemplateService.ACCOUNT_ACTIVATION, update).subject());
        ArgumentCaptor<OrderMessageTemplate> saved = ArgumentCaptor.forClass(OrderMessageTemplate.class);
        verify(repository).save(saved.capture());
        assertEquals(OrderMessageTemplateService.ACCOUNT_ACTIVATION, saved.getValue().getTemplateKey());
        assertEquals(update.subject(), saved.getValue().getSubject());
        assertEquals(update.message(), saved.getValue().getMessage());

        assertThrows(IllegalArgumentException.class, () -> service.update(
                OrderMessageTemplateService.ACCOUNT_ACTIVATION,
                new OrderMessageTemplateService.Update("Reset {{link}}", "Text")));
        assertThrows(IllegalArgumentException.class, () -> service.update(
                OrderMessageTemplateService.PASSWORD_RESET,
                new OrderMessageTemplateService.Update("Subject", "{{token}}")));
    }

    @Test
    void existingOrderVariablesAndStoredTemplatesRemainSupported() {
        when(repository.findById("PAID")).thenReturn(java.util.Optional.of(
                new OrderMessageTemplate("PAID", "Order {{order_number}}", "Thanks {{customer_name}}")));

        assertEquals("Order {{order_number}}", service.get("PAID").subject());
        assertEquals("Thanks, Alex / ORD-1001",
                OrderMessageTemplateService.resolve("Thanks, {{customer_name}} / {{order_number}}", "Alex", "ORD-1001"));
    }
}
