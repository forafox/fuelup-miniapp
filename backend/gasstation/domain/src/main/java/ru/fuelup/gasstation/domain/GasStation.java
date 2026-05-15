package ru.fuelup.gasstation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class GasStation {
    private UUID id;
    private String brandCode;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private GasStationStatus status;
    private GasStationServiceType serviceType;
    private List<Fuel> fuels;
    private List<Column> columns;
    private boolean discountApplicable;
    private boolean sbpEnabled;

    public boolean isOpen() {
        return status == GasStationStatus.ACTIVE;
    }
}
