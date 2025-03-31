package com.francisco.deviceapi.mapper;

import com.francisco.deviceapi.domain.Device;
import com.francisco.deviceapi.domain.enums.DeviceState;
import com.francisco.deviceapi.dto.CreateDeviceRequestDTO;
import com.francisco.deviceapi.dto.DeviceDTO;
import com.francisco.deviceapi.dto.PatchDeviceRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    Device createDeviceRequestDTOToDevice(CreateDeviceRequestDTO dto);

    Device patchDeviceRequestDTOToDevice(PatchDeviceRequestDTO dto);

    DeviceDTO deviceToDeviceDTO(Device device);

    default DeviceState mapState(String state) {
        return switch (state) {
            case "in_use" -> DeviceState.IN_USE;
            case "inactive" -> DeviceState.INACTIVE;
            default -> DeviceState.AVAILABLE;
        };
    }
}
