package sistema.lanchonete.sale.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import sistema.lanchonete.product.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SalePostRequestBody {
    @Min(0)
    @Max(10)
    private Integer tableNumber;
    @NotEmpty
    private String clientCpf;
    private List<String> productName;
    private List<BigDecimal> quantityRequested;
    private BigDecimal totalPrice;
    private ArrayList<Product> productId;
}