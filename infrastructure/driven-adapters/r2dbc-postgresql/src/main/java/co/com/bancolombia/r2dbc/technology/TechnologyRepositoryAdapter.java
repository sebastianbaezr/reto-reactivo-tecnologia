package co.com.bancolombia.r2dbc.technology;

import co.com.bancolombia.model.technology.Technology;
import co.com.bancolombia.model.technology.gateways.TechnologyRepository;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class TechnologyRepositoryAdapter extends ReactiveAdapterOperations<Technology, TechnologyData, Long, TechnologyR2dbcRepository>
        implements TechnologyRepository {

    private final DatabaseClient databaseClient;

    public TechnologyRepositoryAdapter(TechnologyR2dbcRepository repository, ObjectMapper mapper, DatabaseClient databaseClient) {
        super(repository, mapper, d -> mapper.map(d, Technology.class));
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Flux<Technology> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return databaseClient.sql("SELECT * FROM technologies WHERE id = ANY(CAST(:ids AS BIGINT[])) AND deleted_at IS NULL")
                .bind("ids", ids.toArray(new Long[0]))
                .map((row, metadata) -> row.get("id", Long.class))
                .all()
                .flatMap(id -> repository.findById(id))
                .map(this::toEntity);
    }

    @Override
    public Flux<Long> findExistingIdsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }
        return databaseClient.sql("SELECT id FROM technologies WHERE id = ANY(CAST(:ids AS BIGINT[])) AND deleted_at IS NULL")
                .bind("ids", ids.toArray(new Long[0]))
                .map((row, metadata) -> row.get("id", Long.class))
                .all();
    }

    @Override
    public Mono<Integer> softDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Mono.just(0);
        }
        return databaseClient.sql("UPDATE technologies SET deleted_at = CURRENT_TIMESTAMP WHERE id = ANY(CAST(:ids AS BIGINT[])) AND deleted_at IS NULL")
                .bind("ids", ids.toArray(new Long[0]))
                .fetch()
                .rowsUpdated()
                .map(Long::intValue);
    }

    @Override
    public Mono<Integer> restoreByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Mono.just(0);
        }
        return databaseClient.sql("UPDATE technologies SET deleted_at = NULL WHERE id = ANY(CAST(:ids AS BIGINT[])) AND deleted_at IS NOT NULL")
                .bind("ids", ids.toArray(new Long[0]))
                .fetch()
                .rowsUpdated()
                .map(Long::intValue);
    }
}
