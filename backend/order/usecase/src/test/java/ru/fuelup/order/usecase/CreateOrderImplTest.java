package ru.fuelup.order.usecase;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.fuelup.common.order.OrderPaymentType;
import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.common.platform.Platform;
import ru.fuelup.order.domain.Order;
import ru.fuelup.order.usecase.command.OrderCommand;
import ru.fuelup.order.usecase.impl.CreateOrderImpl;
import ru.fuelup.order.usecase.port.GasStationPort;
import ru.fuelup.order.usecase.port.OrderRepository;
import ru.fuelup.order.usecase.port.PartnerApiPort;
import ru.fuelup.order.usecase.port.PaymentPort;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderImplTest {

    @Mock OrderRepository orderRepository;
    @Mock GasStationPort gasStationPort;
    @Mock PartnerApiPort partnerApiPort;
    @Mock PaymentPort paymentPort;

    @InjectMocks CreateOrderImpl sut;

    @Test
    void priceMismatch_shouldReturnError() {
        var stationId = UUID.randomUUID();
        var fuelInfo = new GasStationPort.FuelInfo("AI95", 60.0, 57.5, 57.5);

        when(gasStationPort.getFuelPrice(stationId, "AI95")).thenReturn(Optional.of(fuelInfo));

        var command = OrderCommand.builder()
                .customerId(UUID.randomUUID())
                .gasStationId(stationId)
                .columnNumber(2)
                .fuelType("AI95")
                .requestedAmount(20.0)
                .clientFuelPrice(55.0)  // клиент видел 55, актуальная цена 57.5
                .paymentType(OrderPaymentType.SBP)
                .platform(Platform.TELEGRAM)
                .isTest(false)
                .build();

        var result = sut.invoke(command);

        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isInstanceOf(CreateOrder.CreateOrderError.PriceMismatchError.class);
        var err = (CreateOrder.CreateOrderError.PriceMismatchError) result.getLeft();
        assertThat(err.getActual()).isEqualTo(57.5);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void partnerError_shouldSaveFailedOrderAndReturnError() {
        var stationId = UUID.randomUUID();
        var customerId = UUID.randomUUID();
        var fuelInfo = new GasStationPort.FuelInfo("AI95", 60.0, 57.5, 57.5);
        var savedOrder = new Order(customerId, 20.0, 60.0, 57.5, 57.5,
                "AI95", stationId, 1, OrderPaymentType.SBP, false, Platform.TELEGRAM, null);
        savedOrder.setOrderId(UUID.randomUUID());
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setCreatedAt(System.currentTimeMillis());

        when(gasStationPort.getFuelPrice(stationId, "AI95")).thenReturn(Optional.of(fuelInfo));
        when(orderRepository.save(any())).thenReturn(savedOrder);
        when(partnerApiPort.createFuelingOrder(any(), any(), any(), any(), any(), any()))
                .thenReturn(Either.left("connection refused"));

        var command = OrderCommand.builder()
                .customerId(customerId)
                .gasStationId(stationId)
                .columnNumber(1)
                .fuelType("AI95")
                .requestedAmount(20.0)
                .clientFuelPrice(57.5)
                .paymentType(OrderPaymentType.SBP)
                .platform(Platform.TELEGRAM)
                .isTest(false)
                .build();

        var result = sut.invoke(command);

        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isInstanceOf(CreateOrder.CreateOrderError.PartnerApiUnavailableError.class);
        verify(orderRepository).updateStatus(eq(savedOrder.getOrderId()), eq(OrderStatus.FAILED), anyString());
    }
}
