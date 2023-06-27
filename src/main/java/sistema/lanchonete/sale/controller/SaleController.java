package sistema.lanchonete.sale.controller;

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
import sistema.lanchonete.sale.domain.Sale;
import sistema.lanchonete.sale.dto.SalePostRequestBody;
import sistema.lanchonete.sale.dto.SalePutRequestBody;
import sistema.lanchonete.sale.service.SaleService;

@RestController
@RequestMapping("sale")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    public SaleService getSaleService() {
        return saleService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<Sale>> listAll(@PageableDefault(page=0, size=10,
            sort = "saleId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(getSaleService().listAll(pageable));
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<Sale> findById(@PathVariable long id){
        return ResponseEntity.ok(getSaleService().findByIdOrThrowBackBadRequestException(id));
    }
    @GetMapping(path = "/{clientCpf}")
    public ResponseEntity<Sale> findByClientCpf(@PathVariable String clientCpf){
        return ResponseEntity.ok(getSaleService().findByCpfOrThrowBackBadRequestException(clientCpf));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        getSaleService().delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping
    public ResponseEntity<Sale> save(@RequestBody SalePostRequestBody salePostRequestBody){
        return ResponseEntity.ok(getSaleService().save(salePostRequestBody));
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<Sale> replace(@PathVariable long id,
                                           @RequestBody SalePutRequestBody salePutRequestBody){
        getSaleService().replace(salePutRequestBody);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
