package com.giunei.my_museum.features.game.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giunei.my_museum.features.game.dto.StoreInfo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Converter
public class StoreInfoListConverter implements AttributeConverter<List<StoreInfo>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<StoreInfo> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Error converting StoreInfo list to JSON", e);
            return null;
        }
    }

    @Override
    public List<StoreInfo> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            // Try to parse as List<StoreInfo> (new format)
            return objectMapper.readValue(dbData, new TypeReference<List<StoreInfo>>() {});
        } catch (JsonProcessingException e) {
            // If that fails, try to parse as List<String> (old format) and convert
            try {
                List<String> oldFormat = objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
                return oldFormat.stream()
                        .map(this::convertStringToStoreInfo)
                        .collect(Collectors.toList());
            } catch (JsonProcessingException e2) {
                log.error("Error converting JSON to StoreInfo list (tried both formats)", e2);
                return null;
            }
        }
    }

    private StoreInfo convertStringToStoreInfo(String storeName) {
        String url = null;
        if ("Steam".equalsIgnoreCase(storeName)) {
            // For Steam, we can generate URL if we have appId
            // But we don't have access to appId here, so keep null
            // The URL will be populated during next Steam sync
            url = null;
        }
        return new StoreInfo(storeName, url);
    }
}
