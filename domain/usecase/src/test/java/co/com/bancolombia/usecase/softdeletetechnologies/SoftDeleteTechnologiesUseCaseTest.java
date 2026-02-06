package co.com.bancolombia.usecase.softdeletetechnologies;

import co.com.bancolombia.model.results.SoftDeleteTechnologiesResult;
import co.com.bancolombia.model.technology.gateways.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SoftDeleteTechnologiesUseCase Tests")
class SoftDeleteTechnologiesUseCaseTest {

    @Mock
    private TechnologyRepository technologyRepository;

    private SoftDeleteTechnologiesUseCase softDeleteTechnologiesUseCase;

    @BeforeEach
    void setUp() {
        softDeleteTechnologiesUseCase = new SoftDeleteTechnologiesUseCase(technologyRepository);
    }

    @Test
    @DisplayName("Should successfully delete all technologies and return correct count")
    void testExecute_SuccessfulDelete_ReturnsCorrectCount() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L);

        when(technologyRepository.softDeleteByIds(ids))
            .thenReturn(Mono.just(3));

        // Act & Assert
        StepVerifier.create(softDeleteTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getDeletedCount() == 3 &&
                result.getDeletedIds().size() == 3 &&
                result.getDeletedIds().containsAll(ids)
            )
            .verifyComplete();

        verify(technologyRepository).softDeleteByIds(ids);
    }

    @Test
    @DisplayName("Should return empty result when given empty list")
    void testExecute_EmptyList_ReturnsEmptyResult() {
        // Arrange
        List<Long> ids = List.of();

        // Act & Assert
        StepVerifier.create(softDeleteTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getDeletedCount() == 0 &&
                result.getDeletedIds().isEmpty()
            )
            .verifyComplete();

        verify(technologyRepository, never()).softDeleteByIds(ids);
    }

    @Test
    @DisplayName("Should return zero count when no IDs were deleted")
    void testExecute_NonExistingIds_ReturnsZeroCount() {
        // Arrange
        List<Long> ids = List.of(999L, 1000L);

        when(technologyRepository.softDeleteByIds(ids))
            .thenReturn(Mono.just(0));

        // Act & Assert
        StepVerifier.create(softDeleteTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getDeletedCount() == 0 &&
                result.getDeletedIds().size() == 2 &&
                result.getDeletedIds().containsAll(ids)
            )
            .verifyComplete();

        verify(technologyRepository).softDeleteByIds(ids);
    }

    @Test
    @DisplayName("Should handle partial delete when only some technologies are deleted")
    void testExecute_PartialDelete_ReturnsActualCount() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L, 4L);

        when(technologyRepository.softDeleteByIds(ids))
            .thenReturn(Mono.just(2));

        // Act & Assert
        StepVerifier.create(softDeleteTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getDeletedCount() == 2 &&
                result.getDeletedIds().size() == 4 &&
                result.getDeletedIds().containsAll(ids)
            )
            .verifyComplete();

        verify(technologyRepository).softDeleteByIds(ids);
    }

    @Test
    @DisplayName("Should propagate repository error")
    void testExecute_RepositoryError_PropagatesError() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        RuntimeException testException = new RuntimeException("Database connection failed");

        when(technologyRepository.softDeleteByIds(ids))
            .thenReturn(Mono.error(testException));

        // Act & Assert
        StepVerifier.create(softDeleteTechnologiesUseCase.execute(ids))
            .expectError(RuntimeException.class)
            .verify();

        verify(technologyRepository).softDeleteByIds(ids);
    }

    @Test
    @DisplayName("Should handle single ID deletion correctly")
    void testExecute_WithSingleId_ReturnsResult() {
        // Arrange
        List<Long> ids = List.of(5L);

        when(technologyRepository.softDeleteByIds(ids))
            .thenReturn(Mono.just(1));

        // Act & Assert
        StepVerifier.create(softDeleteTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getDeletedCount() == 1 &&
                result.getDeletedIds().contains(5L)
            )
            .verifyComplete();

        verify(technologyRepository).softDeleteByIds(ids);
    }

    @Test
    @DisplayName("Should handle large list of IDs")
    void testExecute_WithLargeIdList_ReturnsCorrectResult() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        when(technologyRepository.softDeleteByIds(ids))
            .thenReturn(Mono.just(10));

        // Act & Assert
        StepVerifier.create(softDeleteTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getDeletedCount() == 10 &&
                result.getDeletedIds().size() == 10
            )
            .verifyComplete();

        verify(technologyRepository).softDeleteByIds(ids);
    }
}
