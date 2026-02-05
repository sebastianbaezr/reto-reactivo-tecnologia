package co.com.bancolombia.usecase.registertechnology;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateways.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterTechnologyUseCase {
    private final TechnologyRepository technologyRepository;

    public Mono<Technology> execute(Technology technology) {
        return Mono.just(technology)
                .filter(t -> isNameValid(t.getName()))
                .switchIfEmpty(Mono.error(new BusinessException(DomainErrorCode.NAME_REQUIRED)))
                .filter(t -> isDescriptionValid(t.getDescription()))
                .switchIfEmpty(Mono.error(new BusinessException(DomainErrorCode.DESCRIPTION_REQUIRED)))
                .filterWhen(t -> technologyRepository.existsByName(t.getName()).map(exists -> !exists))
                .switchIfEmpty(Mono.error(new BusinessException(DomainErrorCode.TECHNOLOGY_NAME_ALREADY_EXISTS)))
                .flatMap(technologyRepository::save);
    }

    private boolean isNameValid(String name) {
        return name != null && !name.isBlank() && name.length() <= 50;
    }

    private boolean isDescriptionValid(String description) {
        return description != null && !description.isBlank() && description.length() <= 90;
    }
}
