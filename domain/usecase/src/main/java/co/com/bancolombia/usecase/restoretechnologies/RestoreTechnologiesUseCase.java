package co.com.bancolombia.usecase.restoretechnologies;

import co.com.bancolombia.model.results.RestoreTechnologiesResult;
import co.com.bancolombia.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class RestoreTechnologiesUseCase {
    private final TechnologyRepository technologyRepository;

    public Mono<RestoreTechnologiesResult> execute(List<Long> ids) {
        return Mono.just(ids)
                .filter(list -> !list.isEmpty())
                .flatMap(technologyRepository::restoreByIds)
                .map(restoredCount -> buildResult(ids, restoredCount))
                .defaultIfEmpty(buildEmptyResult());
    }

    private RestoreTechnologiesResult buildResult(List<Long> ids, Integer restoredCount) {
        return RestoreTechnologiesResult.builder()
                .restoredCount(restoredCount)
                .restoredIds(ids)
                .build();
    }

    private RestoreTechnologiesResult buildEmptyResult() {
        return RestoreTechnologiesResult.builder()
                .restoredCount(0)
                .restoredIds(List.of())
                .build();
    }
}
