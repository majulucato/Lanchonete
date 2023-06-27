package sistema.lanchonete.recipe.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sistema.lanchonete.product.domain.Product;
import sistema.lanchonete.product.repository.ProductRepository;
import sistema.lanchonete.recipe.domain.Recipe;
import sistema.lanchonete.recipe.dto.AddIngredients;
import sistema.lanchonete.recipe.dto.RecipePutRequestBody;
import sistema.lanchonete.recipe.repository.RecipeRepository;
import sistema.lanchonete.stock.domain.Stock;
import sistema.lanchonete.stock.repository.StockRepository;

import java.math.BigDecimal;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<Recipe> listAll(Pageable pageable) {
        return getRecipeRepository().findAll(pageable);
    }

    public Recipe findByIdOrThrowBackBadRequestException(long id) {
        return getRecipeRepository().findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recipe not found"));
    }
    public Recipe findByRecipeNameOrThrowBackBadRequestException(String recipeName) {
        if (!getRecipeRepository().exitsRecipeName(recipeName)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recipe "+recipeName+" doesn't exists");
        }
        return getRecipeRepository().findByRecipeName(recipeName);
    }
    public Recipe findByRecipesIngredientOrThrowBackBadRequestException(String recipeIngredients) {
        if (!getRecipeRepository().exitsRecipesIngredient(recipeIngredients)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recipe with ingredient(s) "+recipeIngredients+" not found");
        }
        return getRecipeRepository().findByRecipesIngredient(recipeIngredients);
    }

    @Transactional
    public void delete(long id) {
        getRecipeRepository().delete(findByIdOrThrowBackBadRequestException(id));
    }

    @Transactional
    public void replace(RecipePutRequestBody recipePutRequestBody) {
        Recipe recipe = findByRecipeNameOrThrowBackBadRequestException(recipePutRequestBody.getRecipeName());
        recipe.setRecipeId(recipe.getRecipeId());
        recipe.setRecipeName(recipePutRequestBody.getRecipeName());
        recipe.setRecipeIngredients(recipePutRequestBody.getRecipeIngredients());
        recipe.setIngredientQuantity(recipePutRequestBody.getIngredientQuantity());
        recipe.setIngredientsMeasure(recipePutRequestBody.getIngredientsMeasure());
        for(int i=0; i<recipePutRequestBody.getRecipeIngredients().size();i++){
            if (!getStockRepository().existsByItemName(recipePutRequestBody.getRecipeIngredients().get(i))){
                createStock(recipePutRequestBody.getRecipeIngredients().get(i));
            }
        }
        getRecipeRepository().save(recipe);
    }

    public void addIngredients(AddIngredients addIngredients) {
        Recipe recipe = findByRecipeNameOrThrowBackBadRequestException(addIngredients.getRecipeName());
        recipe.setRecipeName(recipe.getRecipeName());
        recipe.setRecipeIngredients(addIngredients.getRecipeIngredients());
        recipe.setIngredientQuantity(addIngredients.getIngredientQuantity());
        recipe.setIngredientsMeasure(addIngredients.getIngredientsMeasure());
        for(int i=0; i<addIngredients.getRecipeIngredients().size();i++){
            if (!getProductRepository().exitsProductName(addIngredients.getRecipeIngredients().get(i))){
                createProduct(addIngredients.getRecipeIngredients().get(i));
            }
        }
        for(int i=0; i<addIngredients.getRecipeIngredients().size();i++){
            if (!getStockRepository().existsByItemName(addIngredients.getRecipeIngredients().get(i))){
                createStock(addIngredients.getRecipeIngredients().get(i));
            }
        }
        getRecipeRepository().save(recipe);
    }

    private Product createProduct(String productName) {
        Product product = new Product();
        product.setProductName(productName);
        product.setProductValue(BigDecimal.valueOf(0));
        product.setProductCost(BigDecimal.valueOf(0));
        product.setRecipe(false);
        return getProductRepository().save(product);
    }

    private Stock createStock(String productName) {
        Stock stock = new Stock();
        stock.setStockName(productName);
        stock.setStockQuantity(BigDecimal.valueOf(0));
        stock.setRecipe(true);
        return getStockRepository().save(stock);
    }
}
