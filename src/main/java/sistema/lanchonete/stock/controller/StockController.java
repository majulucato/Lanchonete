package sistema.lanchonete.stock.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sistema.lanchonete.stock.domain.Stock;
import sistema.lanchonete.stock.dto.AddStockQuantity;
import sistema.lanchonete.stock.dto.StockPutRequestBody;
import sistema.lanchonete.stock.service.StockService;

@RestController
@RequestMapping("stock")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;
    public StockService getStockService() {
        return stockService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<Stock>> listAll(@PageableDefault(page=0, size=10,
            sort = "stockId", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(getStockService().listAll(pageable));
    }
    @GetMapping(path = "/{id}")
    public ResponseEntity<Stock> findById(@PathVariable long id){
        return ResponseEntity.ok(getStockService().findByIdOrThrowBackBadRequestException(id));
    }
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        getStockService().delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity<Stock> replace(@PathVariable long id,
                                           @RequestBody StockPutRequestBody stockPutRequestBody){
        getStockService().replace(stockPutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(path = "/add-stock")
    public ResponseEntity<Stock> addStockQuantity(@RequestBody AddStockQuantity addStockQuantity){
        getStockService().addStockQuantity(addStockQuantity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
