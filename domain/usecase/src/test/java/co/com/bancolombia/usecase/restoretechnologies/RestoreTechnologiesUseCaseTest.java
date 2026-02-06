package co.com.bancolombia.usecase.restoretechnologies;

import co.com.bancolombia.model.results.RestoreTechnologiesResult;
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
@DisplayName("RestoreTechnologiesUseCase Tests")
class RestoreTechnologiesUseCaseTest {

    @Mock
    private TechnologyRepository technologyRepository;

    private RestoreTechnologiesUseCase restoreTechnologiesUseCase;

    @BeforeEach
    void setUp() {
        restoreTechnologiesUseCase = new RestoreTechnologiesUseCase(technologyRepository);
    }

    @Test
    @DisplayName("Should successfully restore all technologies and return correct count")
    void testExecute_SuccessfulRestore_ReturnsCorrectCount() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L);

        when(technologyRepository.restoreByIds(ids))
            .thenReturn(Mono.just(3));

        // Act & Assert
        StepVerifier.create(restoreTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getRestoredCount() == 3 &&
                result.getRestoredIds().size() == 3 &&
                result.getRestoredIds().containsAll(ids)
            )
            .verifyComplete();

        verify(technologyRepository).restoreByIds(ids);
    }

    @Test
    @DisplayName("Should return empty result when given empty list")
    void testExecute_EmptyList_ReturnsEmptyResult() {
        // Arrange
        List<Long> ids = List.of();

        // Act & Assert
        StepVerifier.create(restoreTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getRestoredCount() == 0 &&
                result.getRestoredIds().isEmpty()
            )
            .verifyComplete();

        verify(technologyRepository, never()).restoreByIds(ids);
    }

    @Test
    @DisplayName("Should return zero count when no IDs were restored")
    void testExecute_NonExistingIds_ReturnsZeroCount() {
        // Arrange
        List<Long> ids = List.of(999L, 1000L);

        when(technologyRepository.restoreByIds(ids))
            .thenReturn(Mono.just(0));

        // Act & Assert
        StepVerifier.create(restoreTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getRestoredCount() == 0 &&
                result.getRestoredIds().size() == 2 &&
                result.getRestoredIds().containsAll(ids)
            )
            .verifyComplete();

        verify(technologyRepository).restoreByIds(ids);
    }

    @Test
    @DisplayName("Should handle partial restore when only some technologies are restored")
    void testExecute_PartialRestore_ReturnsActualCount() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L, 4L);

        when(technologyRepository.restoreByIds(ids))
            .thenReturn(Mono.just(2));

        // Act & Assert
        StepVerifier.create(restoreTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getRestoredCount() == 2 &&
                result.getRestoredIds().size() == 4 &&
                result.getRestoredIds().containsAll(ids)
            )
            .verifyComplete();

        verify(technologyRepository).restoreByIds(ids);
    }

    @Test
    @DisplayName("Should propagate repository error")
    void testExecute_RepositoryError_PropagatesError() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        RuntimeException testException = new RuntimeException("Database connection failed");

        when(technologyRepository.restoreByIds(ids))
            .thenReturn(Mono.error(testException));

        // Act & Assert
        StepVerifier.create(restoreTechnologiesUseCase.execute(ids))
            .expectError(RuntimeException.class)
            .verify();

        verify(technologyRepository).restoreByIds(ids);
    }

    @Test
    @DisplayName("Should handle single ID restore correctly")
    void testExecute_WithSingleId_ReturnsResult() {
        // Arrange
        List<Long> ids = List.of(5L);

        when(technologyRepository.restoreByIds(ids))
            .thenReturn(Mono.just(1));

        // Act & Assert
        StepVerifier.create(restoreTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getRestoredCount() == 1 &&
                result.getRestoredIds().contains(5L)
            )
            .verifyComplete();

        verify(technologyRepository).restoreByIds(ids);
    }

    @Test
    @DisplayName("Should handle large list of IDs")
    void testExecute_WithLargeIdList_ReturnsCorrectResult() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        when(technologyRepository.restoreByIds(ids))
            .thenReturn(Mono.just(10));

        // Act & Assert
        StepVerifier.create(restoreTechnologiesUseCase.execute(ids))
            .expectNextMatches(result ->
                result.getRestoredCount() == 10 &&
                result.getRestoredIds().size() == 10
            )
            .verifyComplete();

        verify(technologyRepository).restoreByIds(ids);
    }
}
