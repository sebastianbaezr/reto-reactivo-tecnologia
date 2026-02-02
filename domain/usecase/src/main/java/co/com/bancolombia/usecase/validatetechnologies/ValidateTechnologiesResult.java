package co.com.bancolombia.usecase.validatetechnologies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ValidateTechnologiesResult {
    private List<Long> existingIds;
    private List<Long> notFoundIds;
    private boolean allExist;
}
