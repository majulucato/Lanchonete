package sistema.lanchonete.product.dto;

import lombok.Data;
import sistema.lanchonete.recipe.domain.Recipe;
import sistema.lanchonete.stock.domain.Stock;

import java.math.BigDecimal;

@Data
public class ProductPostRequestBody {
    private String productName;
    private BigDecimal productValue;
    private BigDecimal productCost;
    private Boolean recipe;
    private Stock stockId;
    private Recipe recipeId;
}
