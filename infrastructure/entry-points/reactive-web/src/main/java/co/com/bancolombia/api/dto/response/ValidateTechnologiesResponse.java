package co.com.bancolombia.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ValidateTechnologiesResponse(
    @JsonProperty("allExist") boolean allExist,
    @JsonProperty("existingIds") List<Long> existingIds,
    @JsonProperty("notFoundIds") List<Long> notFoundIds
) {}
