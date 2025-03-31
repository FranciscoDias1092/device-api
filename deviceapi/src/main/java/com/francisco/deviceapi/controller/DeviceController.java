package com.francisco.deviceapi.controller;

import com.francisco.deviceapi.domain.Device;
import com.francisco.deviceapi.domain.enums.DeviceState;
import com.francisco.deviceapi.dto.CreateDeviceRequestDTO;
import com.francisco.deviceapi.dto.DeviceDTO;
import com.francisco.deviceapi.dto.PatchDeviceRequestDTO;
import com.francisco.deviceapi.exception.DeviceInUseException;
import com.francisco.deviceapi.mapper.DeviceMapper;
import com.francisco.deviceapi.service.DeviceService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {

    private final DeviceService deviceService;

    private final DeviceMapper deviceMapper;

    /**
     * Creates a new {@link Device} from the provided data.
     * <p>
     * Calls {@link DeviceService#createDevice(Device)}.
     *
     * @param createDeviceRequestDTO the request DTO containing the {@link Device} details.
     * @return {@link ResponseEntity} containing {@link DeviceDTO} with the created {@link Device} and http status 201.
     */
    @PostMapping
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Device successfully created.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value =
                                    "{" +
                                            "\"id\": \"1234\", " +
                                            "\"name\": \"Device Name\", " +
                                            "\"brand\": \"Device brand\", " +
                                            "\"state\": \"AVAILABLE\", " +
                                            "\"creationTime\": \"21-03-2025\"" +
                                            "}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"name\": \"must not be blank\"}"))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Device already exists.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"errorMessage\": \"A Device with name DeviceName " +
                                    "and brand DeviceBrand already exists!\"}"))
            )
    })
    @Validated
    public ResponseEntity<DeviceDTO> createDevice(@RequestBody @Valid CreateDeviceRequestDTO createDeviceRequestDTO) {
        Device device = deviceMapper.createDeviceRequestDTOToDevice(createDeviceRequestDTO);
        DeviceDTO deviceDTO = deviceMapper.deviceToDeviceDTO(deviceService.createDevice(device));
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceDTO);
    }

    /**
     * Retrieves a {@link Device} by ID.
     * <p>
     * Calls {@link DeviceService#getDevice(Long)}.
     *
     * @param id the id of the {@link Device} to be retrieved.
     * @return {@link ResponseEntity} containing containing {@link DeviceDTO} with the {@link Device} details if found and http status 200.
     */
    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device successfully fetched.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value =
                                    "{" +
                                            "\"id\": \"1234\", " +
                                            "\"name\": \"Device Name\", " +
                                            "\"brand\": \"Device brand\", " +
                                            "\"state\": \"AVAILABLE\", " +
                                            "\"creationTime\": \"21-03-2025\"" +
                                            "}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request.",
                    content = @Content(mediaType ="application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Device not found.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"errorMessage\": \"Device not found!\"}"))
            )
    })
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable(name = "id") Long id) {
        Device device = deviceService.getDevice(id);
        DeviceDTO deviceDTO = deviceMapper.deviceToDeviceDTO(device);
        return ResponseEntity.ok(deviceDTO);
    }

    /**
     * Retrieves a {@link List} of {@link Device} by Brand and/or State or all if query params are not present.
     * <p>
     * Calls {@link DeviceService#getDevice(Long)}.
     *
     * @return {@link ResponseEntity} containing {@link List} of {@link DeviceDTO} with the {@link Device} details if any found that
     * match the criteria and a http status 200.
     */
    @GetMapping
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device(s) successfully fetched.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value =
                                    "[{" +
                                            "\"id\": \"1234\", " +
                                            "\"name\": \"Device Name 1\", " +
                                            "\"brand\": \"Device Brand 2\", " +
                                            "\"state\": \"AVAILABLE\", " +
                                            "\"creationTime\": \"21-03-2025\"" +
                                            "}," +
                                            "{" +
                                            "\"id\": \"1235\", " +
                                            "\"name\": \"Device Name 2\", " +
                                            "\"brand\": \"Device Brand 2\", " +
                                            "\"state\": \"AVAILABLE\", " +
                                            "\"creationTime\": \"21-03-2025\"" +
                                            "}" +
                                            "]"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request.",
                    content = @Content(mediaType ="application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No devices found.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"errorMessage\": \"No devices found!.\"}"))
            )
    })
    public ResponseEntity<List<DeviceDTO>> getDevices(
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "state", required = false) DeviceState state) {
        List<DeviceDTO> deviceList = deviceService.getDevices(brand, state);
        return ResponseEntity.ok(deviceList);
    }

    /**
     * Fully updates a {@link Device}.
     *
     * @param id - the id of the {@link Device} to be updated
     * @param createDeviceRequestDTO - contains the {@link Device} details.
     * @return the updated {@link Device} and a http status 200 if successfully updated.
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device successfully updated.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value =
                                    "{" +
                                            "\"id\": \"1234\", " +
                                            "\"name\": \"Device Name\", " +
                                            "\"brand\": \"Device brand\", " +
                                            "\"state\": \"AVAILABLE\", " +
                                            "\"creationTime\": \"21-03-2025\"" +
                                            "}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"brand\": \"must not be blank\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Device not found.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"errorMessage\": \"Device not found!\"}"))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid CreateDeviceRequestDTO createDeviceRequestDTO) {
        Device device = deviceMapper.createDeviceRequestDTOToDevice(createDeviceRequestDTO);
        DeviceDTO deviceDTO = deviceMapper.deviceToDeviceDTO(deviceService.updateDevice(id, device));
        return ResponseEntity.ok(deviceDTO);
    }

    /**
     * Partially updates a {@link Device}.
     * <p>
     * If the {@link Device} is IN USE throws a {@link DeviceInUseException}
     *
     * @param id - the id of the {@link Device} to be updated
     * @param patchDeviceRequestDTO - contains the {@link Device} details (Name, Brand, State).
     * @return the updated {@link Device} and a http status 200 if successfully updated.
     */
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device successfully updated.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value =
                                    "{" +
                                            "\"id\": \"1234\", " +
                                            "\"name\": \"Device Name\", " +
                                            "\"brand\": \"Device brand\", " +
                                            "\"state\": \"AVAILABLE\", " +
                                            "\"creationTime\": \"21-03-2025\"" +
                                            "}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request.",
                    content = @Content(mediaType ="application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Device not found.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"errorMessage\": \"Device not found!\"}"))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Device is IN USE so its details cannot be updated.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"errorMessage\": \"Device is IN USE so its " +
                                    "properties cannot be updated!\"}"))
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<DeviceDTO> patchDevice(
            @PathVariable(name = "id") Long id,
            @RequestBody @Valid PatchDeviceRequestDTO patchDeviceRequestDTO) {
        Device device = deviceMapper.patchDeviceRequestDTOToDevice(patchDeviceRequestDTO);
        DeviceDTO deviceDTO = deviceMapper.deviceToDeviceDTO(deviceService.patchDevice(id, device));
        return ResponseEntity.ok(deviceDTO);
    }

    /**
     * Deletes a {@link Device} by its id
     * <p>
     * If the {@link Device} is successfully deleted, returns with a http status 204.
     * <p>
     * Calls {@link DeviceService#deleteDevice(Long)}.
     *
     * @param id the id of the {@link Device} to be deleted.
     * @return a {@link ResponseEntity} with http status 204 if the {@link Device} is successfully deleted.
     */
    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Device successfully deleted."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request.",
                    content = @Content(mediaType ="application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Device not found.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"errorMessage\": \"Device not found!\"}"))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Device is IN USE and it cannot be deleted.",
                    content = @Content(mediaType ="application/json",
                            examples = @ExampleObject(value = "{\"errorMessage\": \"The device is in use and cannot be deleted!\"}"))
            )
    })
    public ResponseEntity<Void> deleteDevice(@PathVariable(name = "id") Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}
