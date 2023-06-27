package sistema.lanchonete.stock.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class AddStockQuantity {
    private String stockName;
    private BigDecimal stockQuantity;
}
