package ru.fuelup.order.usecase.impl;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fuelup.common.annotations.Loggable;
import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.order.domain.Order;
import ru.fuelup.order.usecase.CreateOrder;
import ru.fuelup.order.usecase.command.OrderCommand;
import ru.fuelup.order.usecase.port.GasStationPort;
import ru.fuelup.order.usecase.port.OrderRepository;
import ru.fuelup.order.usecase.port.PartnerApiPort;
import ru.fuelup.order.usecase.port.PaymentPort;
import ru.fuelup.order.usecase.result.OrderResult;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CreateOrderImpl implements CreateOrder {

    private static final double PRICE_TOLERANCE = 0.01;

    private final OrderRepository orderRepository;
    private final GasStationPort gasStationPort;
    private final PartnerApiPort partnerApiPort;
    private final PaymentPort paymentPort;

    @Override
    @Loggable
    public Either<CreateOrderError, OrderResult> invoke(OrderCommand command) {
        return validatePrice(command)
                .flatMap(cmd -> preSaveOrder(cmd))
                .flatMap(order -> placeAtPartner(order, command))
                .flatMap(order -> createPayment(order, command))
                .map(this::toResult);
    }

    private Either<CreateOrderError, OrderCommand> validatePrice(OrderCommand command) {
        var fuelInfo = gasStationPort.getFuelPrice(command.getGasStationId(), command.getFuelType());
        if (fuelInfo.isEmpty()) {
            return Either.left(new CreateOrderError.GasStationNotFoundError());
        }

        double currentPrice = fuelInfo.get().getClientPrice();
        if (Math.abs(currentPrice - command.getClientFuelPrice()) > PRICE_TOLERANCE) {
            log.warn("Price mismatch for station={} fuel={}: client sent {}, actual {}",
                    command.getGasStationId(), command.getFuelType(),
                    command.getClientFuelPrice(), currentPrice);
            return Either.left(new CreateOrderError.PriceMismatchError(currentPrice, command.getClientFuelPrice()));
        }
        return Either.right(command);
    }

    private Either<CreateOrderError, Order> preSaveOrder(OrderCommand command) {
        var fuelInfo = gasStationPort.getFuelPrice(command.getGasStationId(), command.getFuelType()).get();

        var order = new Order(
                command.getCustomerId(),
                command.getRequestedAmount(),
                fuelInfo.getBasePrice(),
                fuelInfo.getDiscountedPrice(),
                fuelInfo.getClientPrice(),
                command.getFuelType(),
                command.getGasStationId(),
                command.getColumnNumber(),
                command.getPaymentType(),
                command.getIsTest(),
                command.getPlatform(),
                command.getSbpSubscriptionId()
        );
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(System.currentTimeMillis());

        try {
            var saved = orderRepository.save(order);
            return Either.right(saved);
        } catch (Exception e) {
            log.error("Failed to pre-save order", e);
            return Either.left(new CreateOrderError.PartnerApiUnavailableError());
        }
    }

    private Either<CreateOrderError, Order> placeAtPartner(Order order, OrderCommand command) {
        var partnerResult = partnerApiPort.createFuelingOrder(
                order.getOrderId(),
                command.getGasStationId(),
                command.getColumnNumber(),
                command.getFuelType(),
                command.getRequestedAmount(),
                order.getDiscountedFuelPrice()
        );

        if (partnerResult.isLeft()) {
            orderRepository.updateStatus(order.getOrderId(), OrderStatus.FAILED, "partner_error");
            return Either.left(new CreateOrderError.PartnerApiUnavailableError());
        }

        var result = partnerResult.get();
        order.setPartnerOrderId(result.getPartnerOrderId());
        order.setColumnId(result.getColumnId());
        order.setNozzleId(result.getNozzleId());
        order.setStatus(OrderStatus.PLACED);
        orderRepository.updatePartnerData(order);

        return Either.right(order);
    }

    private Either<CreateOrderError, Order> createPayment(Order order, OrderCommand command) {
        var paymentResult = paymentPort.createPayment(
                order.getOrderId(),
                order.getRequestedSum(),
                command.getPaymentType(),
                command.getSbpSubscriptionId()
        );

        if (paymentResult.isLeft()) {
            orderRepository.updateStatus(order.getOrderId(), OrderStatus.FAILED, "payment_error");
            return Either.left(new CreateOrderError.PaymentCreationError());
        }

        order.setPaymentUrl(paymentResult.get().getPaymentUrl());
        orderRepository.updatePaymentUrl(order.getOrderId(), order.getPaymentUrl());
        return Either.right(order);
    }

    private OrderResult toResult(Order order) {
        return OrderResult.builder()
                .orderId(order.getOrderId())
                .partnerOrderId(order.getPartnerOrderId())
                .paymentUrl(order.getPaymentUrl())
                .status(order.getStatus())
                .requestedSum(order.getRequestedSum())
                .fuelPrice(order.getFuelPrice())
                .discountedFuelPrice(order.getDiscountedFuelPrice())
                .build();
    }
}
