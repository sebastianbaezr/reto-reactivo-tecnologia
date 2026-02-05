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
public class RestoreTechnologiesResult {
    private Integer restoredCount;
    private List<Long> restoredIds;
}
