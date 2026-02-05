package co.com.bancolombia.usecase.gettechnologiesbyids;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class GetTechnologiesByIdsUseCase {
    private final TechnologyRepository technologyRepository;

    public Flux<Technology> execute(List<Long> ids) {
        return Flux.fromIterable(ids)
                .switchIfEmpty(Flux.empty())
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMapMany(technologyRepository::findByIds)
                .switchIfEmpty(Flux.empty());
    }
}
