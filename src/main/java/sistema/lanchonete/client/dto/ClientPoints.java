package sistema.lanchonete.client.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ClientPoints {
    private Long clientId;
    private BigDecimal clientPoints;
}
