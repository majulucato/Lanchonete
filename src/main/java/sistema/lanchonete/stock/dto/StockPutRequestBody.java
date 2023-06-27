package sistema.lanchonete.stock.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockPutRequestBody {
    private Long stockId;
    private String stockName;
    private BigDecimal stockQuantity;
    private Boolean recipe;
}
