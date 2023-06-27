package sistema.lanchonete.recipe.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class AddIngredients {
    private String recipeName;
    private List<String> recipeIngredients;//ingredients
    private List<BigDecimal> ingredientQuantity;//ingredients' quantity
    private List<String> ingredientsMeasure;//unit measure of ingredients
}