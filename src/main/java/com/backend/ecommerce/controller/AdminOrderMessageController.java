package com.backend.ecommerce.controller;

import com.backend.ecommerce.service.email.OrderMessageTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/order-messages")
@RequiredArgsConstructor
public class AdminOrderMessageController {
    private final OrderMessageTemplateService templates;

    @GetMapping
    public List<OrderMessageTemplateService.Template> list() {
        return templates.list();
    }

    @PutMapping("/{key}")
    public OrderMessageTemplateService.Template update(@PathVariable String key,
            @Valid @RequestBody OrderMessageTemplateService.Update update) {
        return templates.update(key, update);
    }
}
