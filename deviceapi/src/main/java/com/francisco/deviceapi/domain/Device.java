package com.francisco.deviceapi.domain;

import com.francisco.deviceapi.domain.enums.DeviceState;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "device_sequence"
    )
    @SequenceGenerator(
            name = "device_sequence",
            sequenceName = "device_sequence",
            allocationSize = 1
    )
    private Long id;

    private String name;

    private String brand;

    @Enumerated(EnumType.STRING)
    private DeviceState state;

    private LocalDate creationTime;

    private Device(Builder builder) {
        this.name = builder.name;
        this.brand = builder.brand;
        this.state = builder.state;
        this.creationTime = LocalDate.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String brand;
        private DeviceState state;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder setState(DeviceState state) {
            this.state = state;
            return this;
        }

        public Device build() {
            return new Device(this);
        }
    }
}
