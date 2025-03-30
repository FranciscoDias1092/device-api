package com.francisco.deviceapi.config;

import com.francisco.deviceapi.converter.StringToDeviceStateConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConverterConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addConverter(new StringToDeviceStateConverter());
    }
}
