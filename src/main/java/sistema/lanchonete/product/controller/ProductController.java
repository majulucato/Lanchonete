package sistema.lanchonete.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sistema.lanchonete.product.domain.Product;
import sistema.lanchonete.product.dto.ProductPostRequestBody;
import sistema.lanchonete.product.dto.ProductPutRequestBody;
import sistema.lanchonete.product.service.ProductService;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    public ProductService getProductService() {
        return productService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<Product>> listAll(@PageableDefault(page=0, size=10,
            sort = "productId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(getProductService().listAll(pageable));
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<Product> findById(@PathVariable long id){
        return ResponseEntity.ok(getProductService().findByIdOrThrowBackBadRequestException(id));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        getProductService().delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping
    public ResponseEntity<Product> save(@RequestBody ProductPostRequestBody productPostRequestBody){
        return ResponseEntity.ok(getProductService().save(productPostRequestBody));
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> replace(@PathVariable long id,
                                           @RequestBody ProductPutRequestBody productPutRequestBody){
        getProductService().replace(productPutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
