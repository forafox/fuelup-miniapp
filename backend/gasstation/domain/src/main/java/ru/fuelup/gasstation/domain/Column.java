package ru.fuelup.gasstation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Column {
    private Long id;
    private Integer number;
    private ColumnStatus status;
    private List<String> availableFuelTypes;

    public boolean isAvailable() {
        return status == ColumnStatus.FREE || status == ColumnStatus.BUSY;
    }

    public enum ColumnStatus {
        FREE,
        BUSY,
        OFFLINE,
    }
}
