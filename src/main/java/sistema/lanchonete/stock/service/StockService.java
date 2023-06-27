package sistema.lanchonete.stock.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sistema.lanchonete.stock.domain.Stock;
import sistema.lanchonete.stock.dto.AddStockQuantity;
import sistema.lanchonete.stock.dto.StockPutRequestBody;
import sistema.lanchonete.stock.repository.StockRepository;

import java.math.BigDecimal;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public StockRepository getStockRepository() {
        return stockRepository;
    }

    @Transactional(readOnly = true)
    public Page<Stock> listAll(Pageable pageable) {
        return getStockRepository().findAll(pageable);
    }

    public Stock findByIdOrThrowBackBadRequestException(Long id) {
        return getStockRepository().findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item's Stock not found"));
    }
    public void findByNameOrThrowBackBadRequestException(String name) {
        if (!getStockRepository().existsByItemName(name)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock for "+name+" not found");
        }
    }

    @Transactional
    public void delete(Long id){
        getStockRepository().delete(findByIdOrThrowBackBadRequestException(id));
    }

    @Transactional
    public void replace(StockPutRequestBody stockPutRequestBody) {
        Stock stock = findByIdOrThrowBackBadRequestException(stockPutRequestBody.getStockId());
        stock.setStockId(stock.getStockId());
        stock.setStockName(stockPutRequestBody.getStockName());
        stock.setStockQuantity(stockPutRequestBody.getStockQuantity());
        stock.setRecipe(stockPutRequestBody.getRecipe());
        validateExistsInStock(stockPutRequestBody.getStockName());
        getStockRepository().save(stock);
    }

    private void validateExistsInStock(String stockName) {
        findByNameOrThrowBackBadRequestException(stockName);
    }

    public void addStockQuantity(AddStockQuantity addStockQuantity) {
        findByNameOrThrowBackBadRequestException(addStockQuantity.getStockName());
        Stock stock = getStockRepository().findByItemName(addStockQuantity.getStockName());
        stock.setStockQuantity(addStockQuantity.getStockQuantity());
        getStockRepository().save(stock);
    }
}
