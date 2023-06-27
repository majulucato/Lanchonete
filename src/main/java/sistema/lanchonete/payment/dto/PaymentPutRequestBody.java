package sistema.lanchonete.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentPutRequestBody {
    private Long paymentId;
    private BigDecimal pointsSale;
}
