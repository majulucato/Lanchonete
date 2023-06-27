package sistema.lanchonete.recipe.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "recipe")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long recipeId;
    @Column(name = "recipe_name")
    private String recipeName;
    @Column(name = "recipe_ingredients")
    private List<String> recipeIngredients;//ingredients
    @Column(name = "recipe_ingredient_quantity")
    private List<BigDecimal> ingredientQuantity;//ingredients' quantity
    @Column(name = "recipe_ingredients_measure")
    private List<String> ingredientsMeasure;//unit measure of ingredients
}