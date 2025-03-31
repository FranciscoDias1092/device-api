package com.francisco.deviceapi.service;

import com.francisco.deviceapi.domain.Device;
import com.francisco.deviceapi.domain.enums.DeviceState;
import com.francisco.deviceapi.dto.DeviceDTO;
import com.francisco.deviceapi.exception.DeviceAlreadyExistsException;
import com.francisco.deviceapi.exception.DeviceInUseException;
import com.francisco.deviceapi.exception.DeviceNotFoundException;
import com.francisco.deviceapi.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTests {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private Device device1;

    private Device device2;

    private Device device3;

    private DeviceDTO deviceDTO1;

    private DeviceDTO deviceDTO2;

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
        device2 = createDevice("Device 2", "Brand 1", DeviceState.IN_USE);
        device3 = createDevice("Device 3", "Brand 1", DeviceState.AVAILABLE);

        deviceDTO1 = new DeviceDTO(1L, "Device 1",  "Brand 1", DeviceState.AVAILABLE, LocalDate.now());
        deviceDTO2 = new DeviceDTO(2L, "Device 3", "Brand 1", DeviceState.AVAILABLE, LocalDate.now());
    }

    @Test
    public void whenCreateDevice_thenDeviceCreated() {
        when(deviceRepository.existsByNameAndBrand(anyString(), anyString())).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenReturn(device1);

        Device createdDevice = deviceService.createDevice(device1);

        assertThat(createdDevice)
                .isNotNull()
                .extracting(Device::getName, Device::getBrand, Device::getState, Device::getCreationTime)
                .containsExactly(device1.getName(), device1.getBrand(), device1.getState(),device1.getCreationTime());
        verify(deviceRepository, times(1)).existsByNameAndBrand(anyString(), anyString());
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    public void whenCreateExistingDevice_thenThrowDeviceAlreadyExistsException() {
        when(deviceRepository.existsByNameAndBrand(anyString(), anyString())).thenReturn(true);

        assertThrows(DeviceAlreadyExistsException.class, () -> deviceService.createDevice(device1));
    }

    @Test
    public void whenGetExistingDevice_thenReturnDevice() {
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.of(device1));

        Device fetchedDevice = deviceService.getDevice(1L);

        assertThat(fetchedDevice)
                .isNotNull()
                .extracting(Device::getName, Device::getBrand, Device::getState, Device::getCreationTime)
                .containsExactly(device1.getName(), device1.getBrand(), device1.getState(),device1.getCreationTime());
        verify(deviceRepository, times(1)).findById(anyLong());
    }

    @Test
    public void whenGetNonExistingDevice_thenThrowDeviceNotFoundException() {
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> deviceService.getDevice(1L));
    }

    @Test
    public void whenGetExistingDevicesListByBrandAndState_thenReturnDeviceList() {
        when(deviceRepository.findByBrandAndState("Brand 1", DeviceState.AVAILABLE))
                .thenReturn(Optional.of(List.of(deviceDTO1, deviceDTO2)));

        List<DeviceDTO> fetchedDevices = deviceService.getDevices("Brand 1", DeviceState.AVAILABLE);

        assertThat(fetchedDevices)
                .isNotEmpty()
                .extracting(DeviceDTO::name, DeviceDTO::brand, DeviceDTO::state, DeviceDTO::creationTime)
                .containsExactly(
                        tuple(device1.getName(), device1.getBrand(), device1.getState(), device1.getCreationTime()),
                        tuple(device3.getName(), device3.getBrand(), device3.getState(), device3.getCreationTime())
                );
        verify(deviceRepository, times(1)).findByBrandAndState("Brand 1", DeviceState.AVAILABLE);
    }

    @Test
    public void whenGetNonExistingDeviceList_thenThrowDeviceNotFoundException() {
        when(deviceRepository.findByBrandAndState(eq(""), any(DeviceState.class)))
                .thenReturn(Optional.of(Collections.emptyList()));

        assertThrows(DeviceNotFoundException.class, () -> deviceService.getDevices("", DeviceState.INACTIVE));
    }

    @Test
    public void whenFullyUpdateDevice_thenUpdateDevice() {
        Device deviceDetails = Device.builder().setName("New Name").setBrand("Brand 1").setState(DeviceState.INACTIVE).build();

        when(deviceRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(device1));
        when(deviceRepository.save(any(Device.class))).thenReturn(device1);

        Device device = deviceService.updateDevice(1L, deviceDetails);

        assertThat(device)
                .isNotNull()
                .extracting(Device::getName, Device::getBrand, Device::getState, Device::getCreationTime)
                .containsExactly(
                        device1.getName(), device1.getBrand(), device1.getState(), device1.getCreationTime()
                );
    }

    @Test
    public void whenFullyUpdateNonExistingDevice_thenThrowDeviceNotFoundException() {
        Device deviceDetails = Device.builder().setName("New Name").setBrand("Brand 1").setState(DeviceState.INACTIVE).build();

        when(deviceRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> deviceService.updateDevice(1L, deviceDetails));
    }

    @Test
    public void whenPartiallyUpdateInUseDeviceBrandAndState_thenPartiallyUpdateDevice() {
        Device deviceDetails = Device.builder().setBrand("Brand 2").setState(DeviceState.INACTIVE).build();

        when(deviceRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(device2));
        when(deviceRepository.save(any(Device.class))).thenReturn(device2);

        Device device = deviceService.patchDevice(1L, deviceDetails);

        assertThat(device)
                .isNotNull()
                .extracting(Device::getName, Device::getBrand, Device::getState, Device::getCreationTime)
                .containsExactly(
                        device2.getName(), "Brand 2", DeviceState.INACTIVE, device2.getCreationTime()
                );
    }

    @Test
    public void whenPartiallyUpdateInUseDeviceBrand_thenThrowDeviceInUseException() {
        Device deviceDetails = Device.builder().setBrand("Brand 2").build();

        when(deviceRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(device2));

        assertThrows(DeviceInUseException.class, () -> deviceService.patchDevice(1L, deviceDetails));
    }

    @Test
    public void whenPartiallyUpdateNonExistingDevice_thenThrowDeviceNotFoundException() {
        Device deviceDetails = Device.builder().setName("New Name").setBrand("Brand 1").setState(DeviceState.INACTIVE).build();

        when(deviceRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> deviceService.patchDevice(1L, deviceDetails));
    }

    @Test
    public void whenDeleteExistingDevice_thenDeviceDeleted() {
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(device1));
        doNothing().when(deviceRepository).delete(any(Device.class));

        deviceService.deleteDevice(1L);
    }

    @Test
    public void whenDeleteNonExistingDevice_thenThrowDeviceNotFoundException() {
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> deviceService.deleteDevice(1L));
    }

    @Test
    public void whenDeleteExistingInUseDevice_thenThrowDeviceInUseException() {
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(device2));

        assertThrows(DeviceInUseException.class, () -> deviceService.deleteDevice(1L));
    }
}
