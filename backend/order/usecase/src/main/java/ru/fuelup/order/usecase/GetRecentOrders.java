package ru.fuelup.order.usecase;

import java.util.List;
import java.util.Map;

public interface GetRecentOrders {
    List<Map<String, Object>> invoke(Long messengerUserId, String platform, int limit);
}
