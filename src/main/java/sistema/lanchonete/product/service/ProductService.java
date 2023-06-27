package sistema.lanchonete.product.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import sistema.lanchonete.product.domain.Product;
import sistema.lanchonete.product.dto.ProductPostRequestBody;
import sistema.lanchonete.product.dto.ProductPutRequestBody;
import sistema.lanchonete.product.repository.ProductRepository;
import sistema.lanchonete.recipe.domain.Recipe;
import sistema.lanchonete.recipe.repository.RecipeRepository;
import sistema.lanchonete.stock.domain.Stock;
import sistema.lanchonete.stock.repository.StockRepository;

import java.math.BigDecimal;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final RecipeRepository recipeRepository;

    @Transactional(readOnly = true)
    public Page<Product> listAll(Pageable pageable) {
        return getProductRepository().findAll(pageable);
    }

    public Product findByIdOrThrowBackBadRequestException(long id) {
        return getProductRepository().findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
    }
    public Product findByNameOrThrowBackBadRequestException(String productName) {
        if (getProductRepository().exitsProductName(productName)==true){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product "+productName+" already exists");
        }
        return getProductRepository().findByProductName(productName);
    }

    @Transactional
    public void delete(long id) {
        getProductRepository().delete(findByIdOrThrowBackBadRequestException(id));
    }

    @Transactional
    public Product save(@Validated ProductPostRequestBody productPostRequestBody) {
        findByNameOrThrowBackBadRequestException(productPostRequestBody.getProductName());
        if (productPostRequestBody.getRecipe() == false){
            createStock(productPostRequestBody.getProductName());
            productPostRequestBody.setStockId(stockRepository.findByItemName(productPostRequestBody.getProductName()));
        }
        if(productPostRequestBody.getRecipe() == true){
            createRecipe(productPostRequestBody.getProductName());
            productPostRequestBody.setStockId(null); //stock will be for ingredients only
            productPostRequestBody.setRecipeId(
                    recipeRepository.findByRecipeName(productPostRequestBody.getProductName()));
        }
        return getProductRepository().save(Product.builder().productName(productPostRequestBody.getProductName())
                .productValue(productPostRequestBody.getProductValue())
                .productCost(productPostRequestBody.getProductCost()).recipe(productPostRequestBody.getRecipe())
                .stockId(productPostRequestBody.getStockId()).recipeId(productPostRequestBody.getRecipeId())
                .build());
    }

    @Transactional
    public void replace(ProductPutRequestBody productPutRequestBody) {
        Product product = findByIdOrThrowBackBadRequestException(productPutRequestBody.getProductId());
        product.setProductId(productPutRequestBody.getProductId());
        product.setProductName(productPutRequestBody.getProductName());
        product.setProductValue(productPutRequestBody.getProductValue());
        product.setProductCost(productPutRequestBody.getProductCost());
        product.setRecipe(productPutRequestBody.getRecipe());
        getProductRepository().save(product);
    }
    private Stock createStock(String productName) {
        Stock stock = new Stock();
        stock.setStockName(productName);
        stock.setStockQuantity(BigDecimal.valueOf(0));
        stock.setRecipe(false);
        return getStockRepository().save(stock);
    }
    private Recipe createRecipe(String productName){
        //add recipe ingredients manually; create stock of ingredients by recipe;
        Recipe recipe = new Recipe();
        recipe.setRecipeName(productName);
        return getRecipeRepository().save(recipe);
    }
}
