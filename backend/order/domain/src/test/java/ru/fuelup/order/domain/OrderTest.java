package ru.fuelup.order.domain;

import org.junit.jupiter.api.Test;
import ru.fuelup.common.order.OrderPaymentType;
import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.common.platform.Platform;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void newOrder_shouldHaveNullStatus() {
        var order = new Order(
                UUID.randomUUID(), 20.0, 55.5, 53.2, 53.2,
                "AI95", UUID.randomUUID(), 3,
                OrderPaymentType.SBP, false, Platform.TELEGRAM, null
        );
        assertThat(order.getStatus()).isNull();
        assertThat(order.isTerminal()).isFalse();
    }

    @Test
    void completedOrder_shouldBeTerminal() {
        var order = new Order(
                UUID.randomUUID(), null, null, OrderStatus.COMPLETED,
                null, OrderPaymentType.SBP, null, null, null, null,
                null, null, null, "AI95", UUID.randomUUID(),
                UUID.randomUUID(), null, 1, null, 0L, 0L,
                false, Platform.TELEGRAM, null, null, null, null
        );
        assertThat(order.isTerminal()).isTrue();
    }

    @Test
    void failedOrder_shouldBeTerminal() {
        var order = new Order(
                UUID.randomUUID(), null, null, OrderStatus.FAILED,
                "partner_error", OrderPaymentType.SBP, null, null, null, null,
                null, null, null, "DT", UUID.randomUUID(),
                UUID.randomUUID(), null, 2, null, 0L, 0L,
                false, Platform.MAX, null, null, null, null
        );
        assertThat(order.isTerminal()).isTrue();
        assertThat(order.getFailReason()).isEqualTo("partner_error");
    }

    @Test
    void pendingOrder_shouldNotBeTerminal() {
        var order = new Order(
                UUID.randomUUID(), null, null, OrderStatus.PENDING,
                null, OrderPaymentType.SBP, 30.0, null, null, null,
                60.0, null, null, "AI95", UUID.randomUUID(),
                UUID.randomUUID(), null, 1, null, System.currentTimeMillis(), 0L,
                false, Platform.TELEGRAM, null, null, null, null
        );
        assertThat(order.isTerminal()).isFalse();
    }
}
