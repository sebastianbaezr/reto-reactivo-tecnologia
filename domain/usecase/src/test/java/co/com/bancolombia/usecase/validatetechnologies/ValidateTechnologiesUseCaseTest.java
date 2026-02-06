package co.com.bancolombia.usecase.validatetechnologies;

import co.com.bancolombia.model.results.ValidateTechnologiesResult;
import co.com.bancolombia.model.technology.gateways.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidateTechnologiesUseCase Tests")
class ValidateTechnologiesUseCaseTest {

    @Mock
    private TechnologyRepository technologyRepository;

    private ValidateTechnologiesUseCase validateTechnologiesUseCase;

    @BeforeEach
    void setUp() {
        validateTechnologiesUseCase = new ValidateTechnologiesUseCase(technologyRepository);
    }

    @Test
    @DisplayName("Should return allExist=true when all IDs exist")
    void testExecute_AllIdsExist_ReturnsAllExistTrue() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L);

        when(technologyRepository.findExistingIdsByIds(ids))
            .thenReturn(Flux.just(1L, 2L, 3L));

        // Act & Assert
        StepVerifier.create(validateTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.isAllExist() &&
                result.getExistingIds().size() == 3 &&
                result.getNotFoundIds().isEmpty()
            )
            .verifyComplete();

        verify(technologyRepository).findExistingIdsByIds(ids);
    }

    @Test
    @DisplayName("Should return allExist=false when no IDs exist")
    void testExecute_NoIdsExist_ReturnsAllExistFalse() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L);

        when(technologyRepository.findExistingIdsByIds(ids))
            .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(validateTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                !result.isAllExist() &&
                result.getExistingIds().isEmpty() &&
                result.getNotFoundIds().size() == 3 &&
                result.getNotFoundIds().containsAll(ids)
            )
            .verifyComplete();

        verify(technologyRepository).findExistingIdsByIds(ids);
    }

    @Test
    @DisplayName("Should return correct lists when only some IDs exist")
    void testExecute_PartialIdsExist_ReturnsCorrectLists() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L);
        List<Long> existingIds = List.of(1L, 3L);

        when(technologyRepository.findExistingIdsByIds(ids))
            .thenReturn(Flux.fromIterable(existingIds));

        // Act & Assert
        StepVerifier.create(validateTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                !result.isAllExist() &&
                result.getExistingIds().size() == 2 &&
                result.getExistingIds().containsAll(existingIds) &&
                result.getNotFoundIds().size() == 1 &&
                result.getNotFoundIds().contains(2L)
            )
            .verifyComplete();

        verify(technologyRepository).findExistingIdsByIds(ids);
    }

    @Test
    @DisplayName("Should return empty result when given empty list")
    void testExecute_EmptyList_ReturnsEmptyResult() {
        // Arrange
        List<Long> ids = List.of();

        // Act & Assert
        StepVerifier.create(validateTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.isAllExist() &&
                result.getExistingIds().isEmpty() &&
                result.getNotFoundIds().isEmpty()
            )
            .verifyComplete();

        verify(technologyRepository, never()).findExistingIdsByIds(ids);
    }

    @Test
    @DisplayName("Should handle duplicate IDs correctly")
    void testExecute_DuplicateIds_HandlesCorrectly() {
        // Arrange
        List<Long> ids = List.of(1L, 1L, 2L);
        List<Long> existingIds = List.of(1L, 2L);

        when(technologyRepository.findExistingIdsByIds(ids))
            .thenReturn(Flux.fromIterable(existingIds));

        // Act & Assert
        StepVerifier.create(validateTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.isAllExist() &&
                result.getExistingIds().size() == 2 &&
                result.getNotFoundIds().isEmpty()
            )
            .verifyComplete();

        verify(technologyRepository).findExistingIdsByIds(ids);
    }

    @Test
    @DisplayName("Should propagate repository error")
    void testExecute_RepositoryError_PropagatesError() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        RuntimeException testException = new RuntimeException("Database connection failed");

        when(technologyRepository.findExistingIdsByIds(ids))
            .thenReturn(Flux.error(testException));

        // Act & Assert
        StepVerifier.create(validateTechnologiesUseCase.execute(ids))
            .expectError(RuntimeException.class)
            .verify();

        verify(technologyRepository).findExistingIdsByIds(ids);
    }

    @Test
    @DisplayName("Should handle single ID correctly")
    void testExecute_WithSingleId_ReturnsResult() {
        // Arrange
        List<Long> ids = List.of(5L);

        when(technologyRepository.findExistingIdsByIds(ids))
            .thenReturn(Flux.just(5L));

        // Act & Assert
        StepVerifier.create(validateTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.isAllExist() &&
                result.getExistingIds().contains(5L) &&
                result.getNotFoundIds().isEmpty()
            )
            .verifyComplete();

        verify(technologyRepository).findExistingIdsByIds(ids);
    }

    @Test
    @DisplayName("Should handle large list of IDs")
    void testExecute_WithLargeIdList_ReturnsCorrectResult() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        List<Long> existingIds = List.of(1L, 3L, 5L, 7L, 9L);

        when(technologyRepository.findExistingIdsByIds(ids))
            .thenReturn(Flux.fromIterable(existingIds));

        // Act & Assert
        StepVerifier.create(validateTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                !result.isAllExist() &&
                result.getExistingIds().size() == 5 &&
                result.getNotFoundIds().size() == 5
            )
            .verifyComplete();

        verify(technologyRepository).findExistingIdsByIds(ids);
    }
}
