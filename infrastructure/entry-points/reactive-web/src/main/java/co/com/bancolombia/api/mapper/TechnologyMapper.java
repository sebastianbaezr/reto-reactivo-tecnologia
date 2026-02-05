package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.request.TechnologyRequest;
import co.com.bancolombia.api.dto.response.RestoreTechnologiesResponse;
import co.com.bancolombia.api.dto.response.SoftDeleteTechnologiesResponse;
import co.com.bancolombia.api.dto.response.TechnologyResponse;
import co.com.bancolombia.api.dto.response.TechnologySummary;
import co.com.bancolombia.api.dto.response.ValidateTechnologiesResponse;
import co.com.bancolombia.model.results.RestoreTechnologiesResult;
import co.com.bancolombia.model.results.SoftDeleteTechnologiesResult;
import co.com.bancolombia.model.results.ValidateTechnologiesResult;
import co.com.bancolombia.model.technology.Technology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TechnologyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Technology toEntity(TechnologyRequest request);

    TechnologyResponse toResponse(Technology entity);

    default TechnologySummary toSimpleResponse(Technology entity) {
        return new TechnologySummary(entity.getId(), entity.getName());
    }

    default ValidateTechnologiesResponse toValidateResponse(ValidateTechnologiesResult result) {
        return new ValidateTechnologiesResponse(
                result.isAllExist(),
                result.getExistingIds(),
                result.getNotFoundIds()
        );
    }

    default SoftDeleteTechnologiesResponse toSoftDeleteResponse(SoftDeleteTechnologiesResult result) {
        return SoftDeleteTechnologiesResponse.builder()
                .deletedCount(result.getDeletedCount())
                .deletedIds(result.getDeletedIds())
                .message(buildDeleteMessage(result.getDeletedCount()))
                .build();
    }

    default RestoreTechnologiesResponse toRestoreResponse(RestoreTechnologiesResult result) {
        return RestoreTechnologiesResponse.builder()
                .restoredCount(result.getRestoredCount())
                .restoredIds(result.getRestoredIds())
                .message(buildRestoreMessage(result.getRestoredCount()))
                .build();
    }

    private String buildDeleteMessage(Integer count) {
        return count + " technology(ies) soft-deleted successfully";
    }

    private String buildRestoreMessage(Integer count) {
        return count + " technology(ies) restored successfully";
    }
}
