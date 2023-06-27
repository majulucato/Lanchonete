package sistema.lanchonete.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientPostRequestBody {
    private String clientName;
    @NotNull
    private String clientCpf;
    private String clientCnpj;
    private String clientPhone;
    private BigDecimal clientPoints;
}
