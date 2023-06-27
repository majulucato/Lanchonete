package sistema.lanchonete.recipe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sistema.lanchonete.recipe.domain.Recipe;
import sistema.lanchonete.recipe.dto.AddIngredients;
import sistema.lanchonete.recipe.dto.RecipePutRequestBody;
import sistema.lanchonete.recipe.service.RecipeService;

@RestController
@RequestMapping("recipe")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeService getRecipeService() {
        return recipeService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<Recipe>> listAll(@PageableDefault(page=0, size=10,
            sort = "recipeId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(getRecipeService().listAll(pageable));
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<Recipe> findById(@PathVariable long id){
        return ResponseEntity.ok(getRecipeService().findByIdOrThrowBackBadRequestException(id));
    }
    @GetMapping(path = "/{recipeIngredients}")
    public ResponseEntity<Recipe> findByRecipesIngredient(@PathVariable String recipeIngredients){
        return ResponseEntity.ok(getRecipeService()
                .findByRecipesIngredientOrThrowBackBadRequestException(recipeIngredients));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        getRecipeService().delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(path = "/update-recipe")
    public ResponseEntity<Recipe> replace(@RequestBody RecipePutRequestBody recipePutRequestBody){
        getRecipeService().replace(recipePutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(path = "/add-ingredients")
    public ResponseEntity<Recipe> addIngredients(@RequestBody AddIngredients addIngredients){
        getRecipeService().addIngredients(addIngredients);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
