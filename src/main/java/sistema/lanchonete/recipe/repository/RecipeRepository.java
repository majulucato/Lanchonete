package sistema.lanchonete.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sistema.lanchonete.recipe.domain.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM recipe r " +
                    "WHERE r.recipe_name = :recipeName")
    Recipe findByRecipeName(@Param("recipeName") String recipeName);
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*)>0 " +
                    "FROM recipe r " +
                    "WHERE r.recipe_name = :recipeName")
    boolean exitsRecipeName(@Param("recipeName") String recipeName);
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM recipe r " +
                    "WHERE r.recipe_ingredients = :recipeIngredients")
    Recipe findByRecipesIngredient(@Param("recipeIngredients") String recipeIngredients);
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*)>0 " +
                    "FROM recipe r " +
                    "WHERE r.recipe_ingredients = :recipeIngredients")
    boolean exitsRecipesIngredient(@Param("recipeIngredients") String recipeIngredients);
}
