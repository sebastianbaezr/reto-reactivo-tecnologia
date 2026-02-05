package co.com.bancolombia.model.results;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoftDeleteTechnologiesResult {
    private Integer deletedCount;
    private List<Long> deletedIds;
}
