package sistema.lanchonete.sale.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import sistema.lanchonete.product.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SalePutRequestBody {
    private Long saleId;
    @Min(0)
    @Max(10)
    private Integer tableNumber;
    private String clientCpf;
    private List<Product> productName;
    private List<BigDecimal> quantityRequested;
    private BigDecimal totalPrice;
    private ArrayList<Product> productId;
}