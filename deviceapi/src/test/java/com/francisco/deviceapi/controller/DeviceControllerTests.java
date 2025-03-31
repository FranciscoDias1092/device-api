package com.francisco.deviceapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.francisco.deviceapi.domain.Device;
import com.francisco.deviceapi.domain.enums.DeviceState;
import com.francisco.deviceapi.dto.CreateDeviceRequestDTO;
import com.francisco.deviceapi.dto.DeviceDTO;
import com.francisco.deviceapi.dto.PatchDeviceRequestDTO;
import com.francisco.deviceapi.exception.DeviceAlreadyExistsException;
import com.francisco.deviceapi.exception.DeviceInUseException;
import com.francisco.deviceapi.exception.DeviceNotFoundException;
import com.francisco.deviceapi.mapper.DeviceMapper;
import com.francisco.deviceapi.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class DeviceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeviceService deviceService;

    @MockitoBean
    private DeviceMapper deviceMapper;

    private Device device1;

    private Device device2;

    private DeviceDTO deviceDTO1;

    private CreateDeviceRequestDTO createDeviceRequestDTO1;

    private PatchDeviceRequestDTO patchDeviceRequestDTO1;

    private Device createDevice(Long id, String name, String brand, DeviceState state) {
        Device device=  Device.builder()
                .setName(name)
                .setBrand(brand)
                .setState(state)
                .build();

        device.setId(id);

        return device;
    }

    @BeforeEach
    public void setUp() {
        device1 = createDevice(100L, "Device 1", "Brand 1", DeviceState.AVAILABLE);
        device2 = createDevice(101L, "Device 2", "Brand 2", null);

        createDeviceRequestDTO1 = new CreateDeviceRequestDTO("Device 1", "Brand 1", DeviceState.AVAILABLE, LocalDate.now());
        patchDeviceRequestDTO1 = new PatchDeviceRequestDTO("Device 2", "Brand 2", null);
        deviceDTO1 = new DeviceDTO(100L, "Device 1",  "Brand 1", DeviceState.AVAILABLE, LocalDate.now());
    }

    @Test
    public void whenCreateDevice_thenReturnDeviceDTO() throws Exception {
        when(deviceMapper.createDeviceRequestDTOToDevice(createDeviceRequestDTO1)).thenReturn(device1);
        when(deviceService.createDevice(device1)).thenReturn(device1);
        when(deviceMapper.deviceToDeviceDTO(device1)).thenReturn(deviceDTO1);

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceDTO1)))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.name").value("Device 1"))
                .andExpect(jsonPath("$.brand").value("Brand 1"));

        verify(deviceMapper, times(1)).createDeviceRequestDTOToDevice(createDeviceRequestDTO1);
        verify(deviceService, times(1)).createDevice(device1);
        verify(deviceMapper, times(1)).deviceToDeviceDTO(device1);
    }

    @Test
    public void whenCreateDeviceThatAlreadyExists_thenReturnConflict() throws Exception {
        when(deviceMapper.createDeviceRequestDTOToDevice(any(CreateDeviceRequestDTO.class))).thenReturn(device1);
        when(deviceService.createDevice(any())).thenThrow(new DeviceAlreadyExistsException("A Device with name " + device1.getName() +
                " and brand " + device1.getBrand() + " already exists!"));

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDeviceRequestDTO1)))
                .andExpect(status().isConflict())
                .andExpect(content().string("{\"errorMessage\":\"A Device with name " + device1.getName() +
                        " and brand " + device1.getBrand() + " already exists!\"}"));

        verify(deviceMapper, times(1)).createDeviceRequestDTOToDevice(createDeviceRequestDTO1);
        verify(deviceService, times(1)).createDevice(device1);
        verify(deviceMapper, never()).deviceToDeviceDTO(device1);
    }

    @Test
    public void whenCreateDeviceWithIncompleteDTO_thenReturnBadRequest() throws Exception {
        CreateDeviceRequestDTO incompleteRequestDTO = new CreateDeviceRequestDTO(null, "Brand 1", DeviceState.AVAILABLE, LocalDate.now());

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incompleteRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"name\":\"must not be blank\"}"));

        verify(deviceService, never()).createDevice(any(Device.class));
    }

    @Test
    public void whenGetDeviceById_thenReturnDeviceDTO() throws Exception {
        when(deviceService.getDevice(anyLong())).thenReturn(device1);
        when(deviceMapper.deviceToDeviceDTO(device1)).thenReturn(deviceDTO1);

        mockMvc.perform(get("/api/v1/devices/{id}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.name").value("Device 1"))
                .andExpect(jsonPath("$.brand").value("Brand 1"));

        verify(deviceService, times(1)).getDevice(100L);
        verify(deviceMapper, times(1)).deviceToDeviceDTO(device1);
    }

    @Test
    public void whenGetDeviceByIdNotExists_thenReturnNotFound() throws Exception {
        when(deviceService.getDevice(anyLong())).thenThrow(new DeviceNotFoundException());

        mockMvc.perform(get("/api/v1/devices/{id}", 100L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"errorMessage\":\"Device not found!\"}"));

        verify(deviceService, times(1)).getDevice(100L);
        verify(deviceMapper, never()).deviceToDeviceDTO(any(Device.class));
    }

    @Test
    public void whenGetDevicesWithoutFilters_thenReturnAllDevices() throws Exception {
        List<DeviceDTO> deviceList = List.of(
            new DeviceDTO(101L, "Device 1", "Brand 1", DeviceState.AVAILABLE, null),
                new DeviceDTO(102L, "Device 1", "Brand 1", DeviceState.IN_USE, null)
        );

        when(deviceService.getDevices(null, null)).thenReturn(deviceList);

        mockMvc.perform(get("/api/v1/devices"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(101L))
                .andExpect(jsonPath("$[0].name").value("Device 1"))
                .andExpect(jsonPath("$[1].id").value(102L))
                .andExpect(jsonPath("$[1].state").value("IN_USE"));

        verify(deviceService, times(1)).getDevices(null, null);
    }

    @Test
    public void whenGetDevicesByBrand_thenReturnFilteredDevices() throws Exception {
        List<DeviceDTO> deviceList = List.of(
                new DeviceDTO(103L, "Device 3", "Brand 3", DeviceState.AVAILABLE, null)
        );

        when(deviceService.getDevices("Brand 3", null)).thenReturn(deviceList);

        mockMvc.perform(get("/api/v1/devices")
                        .param("brand", "Brand 3"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(103L))
                .andExpect(jsonPath("$[0].name").value("Device 3"));

        verify(deviceService, times(1)).getDevices("Brand 3", null);
    }

    @Test
    public void whenNoDevices_thenReturnNotFound() throws Exception {
        when(deviceService.getDevices(any(), any())).thenThrow(new DeviceNotFoundException());

        mockMvc.perform(get("/api/v1/devices")
                        .param("brand", "Brand 4"))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("{\"errorMessage\":\"Device not found!\"}"));

        verify(deviceService, times(1)).getDevices("Brand 4", null);
    }

    @Test
    public void whenUpdateDevice_thenReturnUpdatedDevice() throws Exception {
        Device mappedDevice = createDevice(101L, "Device 1", "Brand 1", DeviceState.AVAILABLE);
        Device updatedDevice = createDevice(101L, "Device 2", "Brand 2", DeviceState.AVAILABLE);
        DeviceDTO returnedDevice = new DeviceDTO(101L, "Device 2", "Brand 2", DeviceState.AVAILABLE, LocalDate.now());

        when(deviceMapper.createDeviceRequestDTOToDevice(any(CreateDeviceRequestDTO.class))).thenReturn(mappedDevice);
        when(deviceService.updateDevice(101L, mappedDevice)).thenReturn(updatedDevice);
        when(deviceMapper.deviceToDeviceDTO(updatedDevice)).thenReturn(returnedDevice);

        mockMvc.perform(put("/api/v1/devices/{id}", 101L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDeviceRequestDTO1)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.name").value("Device 2"))
                .andExpect(jsonPath("$.brand").value("Brand 2"));

        verify(deviceMapper, times(1)).createDeviceRequestDTOToDevice(createDeviceRequestDTO1);
        verify(deviceService, times(1)).updateDevice(101L, mappedDevice);
        verify(deviceMapper, times(1)).deviceToDeviceDTO(updatedDevice);
    }

    @Test
    public void whenUpdateDeviceNotExists_thenReturnNotFound() throws Exception {
        Device mappedDevice = createDevice(101L, "Device 1", "Brand 1", DeviceState.AVAILABLE);

        when(deviceMapper.createDeviceRequestDTOToDevice(any())).thenReturn(mappedDevice);
        when(deviceService.updateDevice(101L, mappedDevice)).thenThrow(new DeviceNotFoundException());

        mockMvc.perform(put("/api/v1/devices/{id}", 101L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDeviceRequestDTO1)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"errorMessage\":\"Device not found!\"}"));

        verify(deviceMapper, times(1)).createDeviceRequestDTOToDevice(createDeviceRequestDTO1);
        verify(deviceService, times(1)).updateDevice(101L, mappedDevice);
        verify(deviceMapper, never()).deviceToDeviceDTO(any());
    }

    @Test
    public void whenPatchDevice_thenReturnUpdatedDevice() throws Exception {
        Device mappedDevice = createDevice(101L, "Device 1", "Brand 1", DeviceState.AVAILABLE);
        Device updatedDevice = createDevice(101L, "Device 2", "Brand 2", DeviceState.AVAILABLE);
        DeviceDTO returnedDevice = new DeviceDTO(101L, "Device 2", "Brand 2", DeviceState.AVAILABLE, LocalDate.now());

        when(deviceMapper.patchDeviceRequestDTOToDevice(patchDeviceRequestDTO1)).thenReturn(mappedDevice);
        when(deviceService.patchDevice(101L, mappedDevice)).thenReturn(updatedDevice);
        when(deviceMapper.deviceToDeviceDTO(updatedDevice)).thenReturn(returnedDevice);

        mockMvc.perform(patch("/api/v1/devices/{id}", 101L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDeviceRequestDTO1)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.name").value("Device 2"))
                .andExpect(jsonPath("$.brand").value("Brand 2"));

        verify(deviceMapper, times(1)).patchDeviceRequestDTOToDevice(patchDeviceRequestDTO1);
        verify(deviceService, times(1)).patchDevice(101L, mappedDevice);
        verify(deviceMapper, times(1)).deviceToDeviceDTO(updatedDevice);
    }

    @Test
    public void whenPatchDeviceNotExists_thenReturnNotFound() throws Exception {
        Device mappedDevice = createDevice(101L, "Device 1", "Brand 1", DeviceState.AVAILABLE);

        when(deviceMapper.patchDeviceRequestDTOToDevice(any())).thenReturn(mappedDevice);
        when(deviceService.patchDevice(101L, mappedDevice)).thenThrow(new DeviceNotFoundException());

        mockMvc.perform(patch("/api/v1/devices/{id}", 101L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDeviceRequestDTO1)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"errorMessage\":\"Device not found!\"}"));

        verify(deviceMapper, times(1)).patchDeviceRequestDTOToDevice(patchDeviceRequestDTO1);
        verify(deviceService, times(1)).patchDevice(101L, mappedDevice);
        verify(deviceMapper, never()).deviceToDeviceDTO(any());
    }

    @Test
    public void whenPatchDeviceInUse_thenReturnConflict() throws Exception {
        Device mappedDevice = createDevice(101L, "Device 1", "Brand 1", DeviceState.AVAILABLE);

        when(deviceMapper.patchDeviceRequestDTOToDevice(any())).thenReturn(mappedDevice);
        when(deviceService.patchDevice(101L, mappedDevice)).thenThrow(new DeviceInUseException("Device is IN USE so its " +
                "properties cannot be updated!"));

        mockMvc.perform(patch("/api/v1/devices/{id}", 101L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDeviceRequestDTO1)))
                .andExpect(status().isConflict())
                .andExpect(content().string("{\"errorMessage\":\"Device is IN USE so its properties cannot be updated!\"}"));

        verify(deviceMapper, times(1)).patchDeviceRequestDTOToDevice(patchDeviceRequestDTO1);
        verify(deviceService, times(1)).patchDevice(101L, mappedDevice);
        verify(deviceMapper, never()).deviceToDeviceDTO(any());
    }

    @Test
    public void whenDeleteDevice_thenReturnNoContent() throws Exception {
        doNothing().when(deviceService).deleteDevice(100L);

        mockMvc.perform(delete("/api/v1/devices/{id}", 100L))
                .andExpect(status().isNoContent());

        verify(deviceService, times(1)).deleteDevice(100L);
    }

    @Test
    public void whenDeleteDeviceNotExists_thenReturnNotFound() throws Exception {
        doThrow(new DeviceNotFoundException()).when(deviceService).deleteDevice(100L);

        mockMvc.perform(delete("/api/v1/devices/{id}", 100L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{\"errorMessage\":\"Device not found!\"}"));

        verify(deviceService, times(1)).deleteDevice(100L);
    }

    @Test
    public void whenDeleteDeviceInUse_thenReturnConflict() throws Exception {
        doThrow(new DeviceInUseException("The device is in use and cannot be deleted!")).when(deviceService).deleteDevice(100L);

        mockMvc.perform(delete("/api/v1/devices/{id}", 100L))
                .andExpect(status().isConflict())
                .andExpect(content().string("{\"errorMessage\":\"The device is in use and cannot be deleted!\"}"));

        verify(deviceService, times(1)).deleteDevice(100L);
    }
}
