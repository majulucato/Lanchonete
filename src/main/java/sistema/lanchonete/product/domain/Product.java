package sistema.lanchonete.product.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sistema.lanchonete.recipe.domain.Recipe;
import sistema.lanchonete.stock.domain.Stock;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    @NotNull
    @Column(name = "product_name")
    private String productName;
    @Min(0)
    @Column(name = "product_value")
    private BigDecimal productValue;
    @Min(0)
    @Column(name = "product_cost")
    private BigDecimal productCost;
    @Column(name = "recipe")
    private Boolean recipe;
    @OneToOne
    @JoinColumn(name = "stock_id")
    private Stock stockId;
    @OneToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipeId;
}