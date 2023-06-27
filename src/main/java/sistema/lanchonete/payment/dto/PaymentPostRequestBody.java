package sistema.lanchonete.payment.dto;

import lombok.Data;
import sistema.lanchonete.client.domain.Client;

import java.math.BigDecimal;

@Data
public class PaymentPostRequestBody {
    private Client clientCpf;
    private BigDecimal pointsSale;
}
