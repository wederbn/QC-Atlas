package org.planqk.atlas.web.dtos;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.QuantumImplementation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeName("QUANTUM")
public class QuantumAlgorithmDto extends AlgorithmDto {

    private boolean nisqReady;

    @NotNull(message = "QuantumComputationModel must not be null!")
    private QuantumComputationModel quantumComputationModel;

    private Set<QuantumResourceDto> requiredQuantumResources = new HashSet<>();
    private String speedUp;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = WRITE_ONLY)
    private Set<QuantumImplementation> implementations = new HashSet<>();

}
