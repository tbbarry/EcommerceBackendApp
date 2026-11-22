package com.backend.ecommerce.entity;

public enum DeliveryType {
    HOME,
    HAND_TO_HAND,
    PICKUP_POINT,
    LOCKER;

    public static boolean supportedAtCheckout(DeliveryType type) {
        return type == HOME || type == HAND_TO_HAND;
    }
}
