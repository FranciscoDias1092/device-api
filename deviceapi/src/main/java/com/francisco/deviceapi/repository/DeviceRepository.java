package com.francisco.deviceapi.repository;

import com.francisco.deviceapi.domain.Device;
import com.francisco.deviceapi.domain.enums.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * Finds all {@link Device} by a Brand.
     *
     * @param brand the brand of the {@link Device} to be retrieved.
     * @return an {@link Optional} with a {@link List} containing all {@link Device} with matching Brand.
     */
    Optional<List<Device>> findByBrand(String brand);

    /**
     * Find all {@link Device} by a State.
     *
     * @param state - Available, In use, or Inactive.
     * @return an {@link Optional} with a {@link List} containing all {@link Device} with matching State.
     */
    Optional<List<Device>> findByState(DeviceState state);
}
