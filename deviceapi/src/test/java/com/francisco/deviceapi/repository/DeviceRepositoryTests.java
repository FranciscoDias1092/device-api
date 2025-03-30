package com.francisco.deviceapi.repository;

import com.francisco.deviceapi.domain.Device;
import com.francisco.deviceapi.domain.enums.DeviceState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class DeviceRepositoryTests {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Device device1;

    private Device device2;

    private Device device3;

    private Device createDevice(String name, String brand, DeviceState state) {
        return Device.builder()
                .setName(name)
                .setBrand(brand)
                .setState(state)
                .build();
    }

    @BeforeEach
    void setup() {
        device1 = createDevice("Device 1", "Brand 1", DeviceState.AVAILABLE);
        testEntityManager.persistAndFlush(device1);

        device2 = createDevice("Device 2", "Brand 1", DeviceState.AVAILABLE);
        testEntityManager.persistAndFlush(device2);

        device3 = createDevice("Device 3", "Brand 2", DeviceState.IN_USE);
        testEntityManager.persistAndFlush(device3);
    }

    @Test
    public void whenFindDeviceByValidId_thenReturnDevice() {
        Optional<Device> deviceOptional = deviceRepository.findById(device1.getId());

        assertTrue(deviceOptional.isPresent());
        assertThat(deviceOptional.get())
                .isNotNull()
                .extracting(Device::getName, Device::getBrand, Device::getState, Device::getCreationTime)
                .containsExactly(device1.getName(), device1.getBrand(), device1.getState(),device1.getCreationTime());
    }

    @Test
    public void whenFindDeviceByInvalidId_thenReturnEmptyOptional() {
        Optional<Device> deviceOptional = deviceRepository.findById(123L);

        assertTrue(deviceOptional.isEmpty());
    }

    @Test
    public void whenFindDeviceByValidBrand_thenReturnDeviceListWithMatchingBrand() {
        Optional<List<Device>> deviceListOptional = deviceRepository.findByBrand("Brand 1");

        assertTrue(deviceListOptional.isPresent());
        assertFalse(deviceListOptional.get().isEmpty());
        assertThat(deviceListOptional.get())
                .hasSize(2);
        assertThat(deviceListOptional.get())
                .extracting(Device::getName)
                .containsExactly(device1.getName(), device2.getName());
    }

    @Test
    public void whenFindDeviceByInvalidBrand_thenReturnEmptyOptional() {
        Optional<List<Device>> deviceListOptional = deviceRepository.findByBrand("Brand 3");

        assertTrue(deviceListOptional.isPresent());
        assertTrue(deviceListOptional.get().isEmpty());
    }

    @Test
    public void whenFindDeviceByValidState_thenReturnDeviceListWithMatchingState() {
        Optional<List<Device>> availableDeviceListOptional = deviceRepository.findByState(DeviceState.AVAILABLE);

        assertTrue(availableDeviceListOptional.isPresent());
        assertFalse(availableDeviceListOptional.get().isEmpty());
        assertThat(availableDeviceListOptional.get())
                .hasSize(2);
        assertThat(availableDeviceListOptional.get())
                .extracting(Device::getName)
                .containsExactly(device1.getName(), device2.getName());

        Optional<List<Device>> inUseDeviceListOptional = deviceRepository.findByState(DeviceState.IN_USE);

        assertTrue(inUseDeviceListOptional.isPresent());
        assertFalse(inUseDeviceListOptional.get().isEmpty());
        assertThat(inUseDeviceListOptional.get())
                .hasSize(1);
        assertThat(inUseDeviceListOptional.get())
                .extracting(Device::getName)
                .containsExactly(device3.getName());
    }

    @Test
    public void whenFindDeviceByInvalidState_thenReturnEmptyOptional() {
        Optional<List<Device>> inactiveDeviceListOptional = deviceRepository.findByState(DeviceState.INACTIVE);

        assertTrue(inactiveDeviceListOptional.isPresent());
        assertTrue(inactiveDeviceListOptional.get().isEmpty());
    }

    @Test
    public void whenDeleteDevice_thenDeviceDeleted() {
        Long device1Id = device1.getId();

        deviceRepository.delete(device1);

        Optional<Device> deviceOptional = deviceRepository.findById(device1Id);

        assertTrue(deviceOptional.isEmpty());
    }
}
