package sistema.lanchonete.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPutRequestBody {
    private Long productId;
    private String productName;
    private BigDecimal productValue;
    private BigDecimal productCost;
    private Boolean recipe;
}
