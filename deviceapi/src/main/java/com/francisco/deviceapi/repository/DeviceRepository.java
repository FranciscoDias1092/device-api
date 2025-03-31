package com.francisco.deviceapi.repository;

import com.francisco.deviceapi.domain.Device;
import com.francisco.deviceapi.domain.enums.DeviceState;
import com.francisco.deviceapi.dto.DeviceDTO;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * Finds all {@link Device} by Brand.
     *
     * @param brand the Brand of the {@link Device} to be retrieved.
     * @return an {@link Optional} with a {@link List} containing all {@link Device} with matching Brand.
     */
    Optional<List<Device>> findByBrand(String brand);

    /**
     * Find all {@link Device} by State.
     *
     * @param state - Available, In use, or Inactive.
     * @return an {@link Optional} with a {@link List} containing all {@link Device} with matching State.
     */
    Optional<List<Device>> findByState(DeviceState state);

    /**
     * Find all {@link Device} by a Brand and/or State (if in query) or all {@link Device}.
     *
     * @param brand - the Brand of the {@link Device} to be retrieved.
     * @param state - Available, In use, or Inactive.
     * @return an {@link Optional} with a {@link List} containing all {@link Device} with matching Brand and/or
     * State (if params in query) or an {@link Optional} with a {@link List} containing all devices.
     */
    @Query("SELECT new com.francisco.deviceapi.dto.DeviceDTO(" +
            "d.id, " +
            "d.name, " +
            "d.brand, " +
            "d.state, " +
            "d.creationTime) " +
            "FROM Device d " +
            "WHERE (:brand IS NULL OR d.brand = :brand)" +
            " AND (:state IS NULL OR d.state = :state)")
    Optional<List<DeviceDTO>> findByBrandAndState(@Param("brand") String brand, @Param("state") DeviceState state);

    /**
     * Checks if a {@link Device} exists with Brand and Name.
     *
     * @param name - the Name of the {@link Device} to be retrieved.
     * @param brand - the Brand of the {@link Device} to be retrieved.
     * @return an {@link Optional} with retrieved {@link Device} (if present) or an empty {@link Optional}.
     */
    boolean existsByNameAndBrand(String name, String brand);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Device d WHERE d.id = :id")
    Optional<Device> findByIdForUpdate(@Param("id") Long id);
}
