package co.com.bancolombia.usecase.softdeletetechnologies;

import co.com.bancolombia.model.results.SoftDeleteTechnologiesResult;
import co.com.bancolombia.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SoftDeleteTechnologiesUseCase {
    private final TechnologyRepository technologyRepository;

    public Mono<SoftDeleteTechnologiesResult> execute(List<Long> ids) {
        return Mono.just(ids)
                .filter(list -> !list.isEmpty())
                .flatMap(technologyRepository::softDeleteByIds)
                .map(deletedCount -> buildResult(ids, deletedCount))
                .defaultIfEmpty(buildEmptyResult());
    }

    private SoftDeleteTechnologiesResult buildResult(List<Long> ids, Integer deletedCount) {
        return SoftDeleteTechnologiesResult.builder()
                .deletedCount(deletedCount)
                .deletedIds(ids)
                .build();
    }

    private SoftDeleteTechnologiesResult buildEmptyResult() {
        return SoftDeleteTechnologiesResult.builder()
                .deletedCount(0)
                .deletedIds(List.of())
                .build();
    }
}
