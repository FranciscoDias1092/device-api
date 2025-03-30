package com.francisco.deviceapi.service;

import com.francisco.deviceapi.domain.Device;
import com.francisco.deviceapi.domain.enums.DeviceState;
import com.francisco.deviceapi.exception.*;
import com.francisco.deviceapi.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    /**
     * Saves a new {@link Device}.
     * <p>
     * Calls {@link DeviceRepository#save(Object)}.
     *
     * @param device the {@link Device} to be created
     * @return the saved {@link Device}
     */
    public Device createDevice(Device device) {
        if (deviceRepository.existsByNameAndBrand(device.getName(), device.getBrand())) {
            throw new DeviceAlreadyExistsException(
                    "A Device with name " + device.getName() +
                            " and brand " + device.getBrand() + " already exists!");
        }

        return deviceRepository.save(device);
    }

    /**
     * Retrieves a {@link Device} by ID.
     * <p>
     * Calls {@link DeviceRepository#findById(Object)}.
     * <p>
     * Throws a {@link DeviceNotFoundException} if {@link Device} doesn't exist.
     * <p>
     * {@link DeviceNotFoundException} is handled in {@link GlobalExceptionHandler#handleNotFoundException(NotFoundException)}
     *
     * @param id the ID of the {@link Device} to be retrieved.
     * @return {@link Device} if found.
     */
    public Device getDevice(Long id) {
        return deviceRepository.findById(id).orElseThrow(DeviceNotFoundException::new);
    }

    /**
     * Retrieves a {@link List} of {@link Device} by Brand and/or State if one is present.
     * <p>
     * Else, returns all {@link Device}.
     * <p>
     * Calls {@link DeviceRepository#findByBrandAndState(String, DeviceState)}.
     * <p>
     * Throws a {@link DeviceNotFoundException} if {@link Optional} is empty or {@link Device} {@link List} is empty.
     *
     * @param brand - the Brand of the {@link Device} to be retrieved.
     * @param state - Available, In use, or Inactive.
     * @return a {@link List} containing all {@link Device} with matching Brand and/or
     * State (if params in query) or a {@link List} containing all devices.
     */
    public List<Device> getDevices(String brand, DeviceState state) {
        List<Device> deviceList = deviceRepository.findByBrandAndState(brand, state)
                .orElseThrow(DeviceNotFoundException::new);

        if (deviceList.isEmpty()) {
            throw new DeviceNotFoundException();
        }

        return deviceList;
    }

    /**
     * Fully updates an existing {@link Device}, except for the creation time, which can't be
     * modified after creation.
     * <p>
     * Calls {@link DeviceRepository#save(Object)}.
     *
     * @param id the ID of the {@link Device} to be updated.
     * @param device a {@link Device} containing updated details.
     * @return the updated {@link Device}
     */
    public Device updateDevice(Long id, Device device) {
        Device savedDevice = getDevice(id);

        savedDevice.setName(device.getName());
        savedDevice.setBrand(device.getBrand());
        savedDevice.setState(device.getState());

        return deviceRepository.save(savedDevice);
    }

    /**
     * Partially updates an existing {@link Device}, except for the creation time, which can't be
     * modified after creation.
     *
     * Throws a {@link DeviceInUseException} if state is IN_USE and Brand and/or Name are to be changed.
     *
     * @param id the ID of the {@link Device} to be updated.
     * @param deviceDetails a {@link Device} containing updated details.
     * @return the updated {@link Device}
     */
    public Device patchDevice(Long id, Device deviceDetails) {
        Device persistedDevice = getDevice(id);

        Optional.ofNullable(deviceDetails.getState()).ifPresent(persistedDevice::setState);

        if (persistedDevice.getState() == DeviceState.IN_USE &&
                (deviceDetails.getName() != null || deviceDetails.getBrand() != null)) {
            throw new DeviceInUseException("Device is in use and its properties cannot be updated!");
        }

        Optional.ofNullable(deviceDetails.getName()).ifPresent(persistedDevice::setName);
        Optional.ofNullable(deviceDetails.getBrand()).ifPresent(persistedDevice::setBrand);

        return deviceRepository.save(persistedDevice);
    }

    /**
     * Deletes a {@link Device} by ID.
     * <p>
     * Calls {@link DeviceRepository#delete(Object)}.
     * <p>
     * Throws a {@link DeviceNotFoundException} if device doesn't exist.
     * <p>
     * {@link DeviceNotFoundException} is handled in {@link GlobalExceptionHandler#handleNotFoundException(NotFoundException)}.
     * <p>
     * Throws a {@link DeviceInUseException} if device doesn't exist.
     * <p>
     * {@link DeviceNotFoundException} is handled in {@link GlobalExceptionHandler#handleDeviceInUseException(DeviceInUseException)}.
     *
     * @param id the ID of the {@link Device} to be deleted.
     */
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id).orElseThrow(DeviceNotFoundException::new);

        if (device.getState() == DeviceState.IN_USE) {
            throw new DeviceInUseException("The device is in use and cannot be deleted!");
        }

        deviceRepository.delete(device);
    }
}
