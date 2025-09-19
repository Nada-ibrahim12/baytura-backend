package org.os.bayturabackend.DTOs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PropertyRequestDTO extends PropertyDetailsRequestDTO {
    // For (create/update) – backend will set status and owner
}
