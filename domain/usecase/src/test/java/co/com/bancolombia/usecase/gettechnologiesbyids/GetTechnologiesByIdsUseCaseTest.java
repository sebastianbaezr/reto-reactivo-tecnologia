package co.com.bancolombia.usecase.gettechnologiesbyids;

import co.com.bancolombia.model.technology.Technology;
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
@DisplayName("GetTechnologiesByIdsUseCase Tests")
class GetTechnologiesByIdsUseCaseTest {

    @Mock
    private TechnologyRepository technologyRepository;

    private GetTechnologiesByIdsUseCase getTechnologiesByIdsUseCase;

    @BeforeEach
    void setUp() {
        getTechnologiesByIdsUseCase = new GetTechnologiesByIdsUseCase(technologyRepository);
    }

    @Test
    @DisplayName("Should return technologies when all IDs exist")
    void testExecute_WithValidIds_ReturnsTechnologies() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 3L);
        Technology tech1 = createTechnology(1L, "Spring", "Java framework");
        Technology tech2 = createTechnology(2L, "React", "JS library");
        Technology tech3 = createTechnology(3L, "Vue", "JS framework");

        when(technologyRepository.findByIds(ids))
            .thenReturn(Flux.just(tech1, tech2, tech3));

        // Act & Assert
        StepVerifier.create(getTechnologiesByIdsUseCase.execute(ids))
            .expectNext(tech1)
            .expectNext(tech2)
            .expectNext(tech3)
            .verifyComplete();

        verify(technologyRepository).findByIds(ids);
    }

    @Test
    @DisplayName("Should return empty Flux when given empty list")
    void testExecute_WithEmptyList_ReturnsEmptyFlux() {
        // Arrange
        List<Long> ids = List.of();

        // Act & Assert
        StepVerifier.create(getTechnologiesByIdsUseCase.execute(ids))
            .verifyComplete();

        verify(technologyRepository, never()).findByIds(ids);
    }

    @Test
    @DisplayName("Should return empty Flux when no IDs exist in repository")
    void testExecute_WithNonExistingIds_ReturnsEmptyFlux() {
        // Arrange
        List<Long> ids = List.of(999L, 1000L);

        when(technologyRepository.findByIds(ids))
            .thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(getTechnologiesByIdsUseCase.execute(ids))
            .verifyComplete();

        verify(technologyRepository).findByIds(ids);
    }

    @Test
    @DisplayName("Should return only existing technologies when given mix of existing and non-existing IDs")
    void testExecute_WithMixedIds_ReturnsOnlyExisting() {
        // Arrange
        List<Long> ids = List.of(1L, 2L, 999L, 1000L);
        Technology tech1 = createTechnology(1L, "Spring", "Java framework");
        Technology tech2 = createTechnology(2L, "React", "JS library");

        when(technologyRepository.findByIds(ids))
            .thenReturn(Flux.just(tech1, tech2));

        // Act & Assert
        StepVerifier.create(getTechnologiesByIdsUseCase.execute(ids))
            .expectNext(tech1)
            .expectNext(tech2)
            .verifyComplete();

        verify(technologyRepository).findByIds(ids);
    }

    @Test
    @DisplayName("Should propagate repository error")
    void testExecute_RepositoryError_PropagatesError() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        RuntimeException testException = new RuntimeException("Database connection failed");

        when(technologyRepository.findByIds(ids))
            .thenReturn(Flux.error(testException));

        // Act & Assert
        StepVerifier.create(getTechnologiesByIdsUseCase.execute(ids))
            .expectError(RuntimeException.class)
            .verify();

        verify(technologyRepository).findByIds(ids);
    }

    @Test
    @DisplayName("Should handle single ID correctly")
    void testExecute_WithSingleId_ReturnsTechnology() {
        // Arrange
        List<Long> ids = List.of(5L);
        Technology tech = createTechnology(5L, "Python", "Programming language");

        when(technologyRepository.findByIds(ids))
            .thenReturn(Flux.just(tech));

        // Act & Assert
        StepVerifier.create(getTechnologiesByIdsUseCase.execute(ids))
            .expectNext(tech)
            .verifyComplete();

        verify(technologyRepository).findByIds(ids);
    }

    // Helper method
    private Technology createTechnology(Long id, String name, String description) {
        Technology tech = new Technology();
        tech.setId(id);
        tech.setName(name);
        tech.setDescription(description);
        return tech;
    }
}
