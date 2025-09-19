package org.os.bayturabackend.DTOs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestCreateDTO extends PropertyDetailsRequestDTO {
    // Customer will create this, backend sets status and customer
}
