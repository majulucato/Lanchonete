package sistema.lanchonete.sale.service;

import jakarta.validation.Valid;
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
import sistema.lanchonete.client.domain.Client;
import sistema.lanchonete.client.repository.ClientRepository;
import sistema.lanchonete.client.service.ClientService;
import sistema.lanchonete.payment.domain.Payment;
import sistema.lanchonete.payment.repository.PaymentRepository;
import sistema.lanchonete.product.domain.Product;
import sistema.lanchonete.product.repository.ProductRepository;
import sistema.lanchonete.product.service.ProductService;
import sistema.lanchonete.recipe.domain.Recipe;
import sistema.lanchonete.recipe.repository.RecipeRepository;
import sistema.lanchonete.recipe.service.RecipeService;
import sistema.lanchonete.sale.domain.Sale;
import sistema.lanchonete.sale.dto.FinishOpenOrders;
import sistema.lanchonete.sale.dto.SalePostRequestBody;
import sistema.lanchonete.sale.dto.SalePutRequestBody;
import sistema.lanchonete.sale.repository.SaleRepository;
import sistema.lanchonete.sale.util.DateUtil;
import sistema.lanchonete.stock.domain.Stock;
import sistema.lanchonete.stock.repository.StockRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class SaleService {
    private final SaleRepository saleRepository;
    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final DateUtil dateUtil;
    private final StockRepository stockRepository;
    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;
    private final PaymentRepository paymentRepository;

    public SaleRepository getSaleRepository() {
        return saleRepository;
    }

    @Transactional(readOnly = true)
    public Page<Sale> listAll(Pageable pageable) {
        return getSaleRepository().findAll(pageable);
    }

    public Sale findByIdOrThrowBackBadRequestException(Long id) {
        return getSaleRepository().findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request not found"));
    }
    public Sale findByCpfOrThrowBackBadRequestException(String clientCpf) {
        if (!getSaleRepository().existsClientCpf(clientCpf)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No history of orders found for Client "+clientCpf);
        }
        return getSaleRepository().findByClientCpf(clientCpf);
    }

    @Transactional
    public void delete(Long id){
        getSaleRepository().delete(findByIdOrThrowBackBadRequestException(id));
    }

    @Transactional
    public void replace(SalePutRequestBody salePutRequestBody) {
        delete(salePutRequestBody.getSaleId());
        Payment payment = getPaymentRepository().findBySaleId(salePutRequestBody.getSaleId());
        getPaymentRepository().deleteById(payment.getPaymentId());
        System.out.println("Previous request delete, you can now post a new order");
    }

    @Transactional
    public Sale save(@Validated SalePostRequestBody salePostRequestBody) {
        validateClient(salePostRequestBody.getClientCpf());
        Client client = clientService.findByCpfOrThrowBackBadRequestException(salePostRequestBody.getClientCpf());
        salePostRequestBody.setTotalPrice(BigDecimal.valueOf(0));
        if (salePostRequestBody.getStatus()==null){
            salePostRequestBody.setStatus(true);
        }
        List<Product> productIds = new ArrayList<>();
        for (int i = 0; i < salePostRequestBody.getProductName().size(); i++) {
            Product product = getProductService().findByNameOrThrowBackBadRequestException(
                    salePostRequestBody.getProductName().get(i));
            productIds.add(product);
            if (!product.getRecipe()){
                validateProductStock(product.getProductName(), salePostRequestBody.getQuantityRequested().get(i));
            } else {
               validateRecipeIngredientsStock(product.getProductName(),
                       salePostRequestBody.getQuantityRequested().get(i));
            }
            BigDecimal sumPoints = salePostRequestBody.getTotalPrice().add(product.getProductCost()
                                   .multiply(salePostRequestBody.getQuantityRequested().get(i)));
            salePostRequestBody.setTotalPrice(sumPoints);
        }
        validateStatusSituation(client.getClientCpf(), salePostRequestBody.getStatus(),
                                salePostRequestBody.getTotalPrice());
        Sale sale = getSaleRepository().save(Sale.builder().tableNumber(salePostRequestBody.getTableNumber())
                .clientCpf(client).status(salePostRequestBody.getStatus())
                .productName(salePostRequestBody.getProductName())
                .quantityRequested(salePostRequestBody.getQuantityRequested())
                .productId(productIds)
                .saleDate(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()))
                .totalPrice(salePostRequestBody.getTotalPrice()).build());
        Payment payment = getPaymentRepository().findByClientCpfAndTime(sale.getClientCpf().getClientId(),
                                                                        sale.getSaleDate());
        payment.setSaleId(sale);
        return sale;
    }

    private void validateStatusSituation(String clientCpf, Boolean status, BigDecimal totalPrice) {
        Client client = clientService.findByCpfOrThrowBackBadRequestException(clientCpf);
        BigDecimal sumOpenOrdersTotalPrice = getSaleRepository().sumOpenOrders(client.getClientId());
        if (sumOpenOrdersTotalPrice==null){sumOpenOrdersTotalPrice = BigDecimal.ZERO;}
        BigDecimal sumOpenOrdersAndNewOrder = (sumOpenOrdersTotalPrice.add(totalPrice));
        if (sumOpenOrdersAndNewOrder.compareTo(client.getClientPoints())>0){
            if (sumOpenOrdersTotalPrice == BigDecimal.ZERO){
                BigDecimal pointsMissing = (totalPrice.subtract(client.getClientPoints()));
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Client "+clientCpf+" doesn't have enough points to finish the payment." +
                                "\nTotal points = "+totalPrice+"\nClient Points = "+client.getClientPoints()+
                                "\nPoints needed to finish payment = "+pointsMissing);

            }else {
                BigDecimal pointsMissing = (sumOpenOrdersAndNewOrder.subtract(client.getClientPoints()));
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Client "+clientCpf+" doesn't have enough points to finish the payment of open order(s) and add a new order." +
                                "\nTotal Points of existing order(s) = "+sumOpenOrdersTotalPrice+
                                "\nTotal Points of new order + existing order(s) = "+sumOpenOrdersAndNewOrder+
                                "\nClient Points = "+client.getClientPoints()+
                                "\nPoints needed to finish payment = "+pointsMissing);
            }
        }
        if (!status){
            if (sumOpenOrdersTotalPrice.compareTo(client.getClientPoints())<=0){
                Payment payment = createPayment(clientCpf,totalPrice);
                payment.setStatus(false);
                getPaymentRepository().save(payment);
            }
        } else {
            if (sumOpenOrdersAndNewOrder.compareTo(client.getClientPoints())<=0){
                createPayment(clientCpf,totalPrice);
                client.setClientPoints(client.getClientPoints().subtract(totalPrice));
                getClientRepository().save(client);
            }
        }
    }

    private void validateClient(String clientCpf) {
        //search if client exists, if not create client before proceeding with order;
        if (!clientRepository.existsClientCpf(clientCpf)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client doesn't exists in database, please " +
                    "register the client before proceeding with the request");
        }
    }

    private void validateRecipeIngredientsStock(String productName, BigDecimal quantityRequested){
        Recipe recipe = getRecipeService().findByRecipeNameOrThrowBackBadRequestException(productName);
        for (int i = 0; i < recipe.getRecipeIngredients().size(); i++){
            BigDecimal ingredientQuantity = (recipe.getIngredientQuantity().get(i).multiply(quantityRequested));
            validateProductStock(recipe.getRecipeIngredients().get(i), ingredientQuantity);
        }
    }
    private void validateProductStock(String productName, BigDecimal quantityRequested) {
        Product product = getProductService().findByNameOrThrowBackBadRequestException(productName);
        Stock stock = stockRepository.findByItemName(product.getProductName());
        BigDecimal quantityMissing = quantityRequested.subtract(stock.getStockQuantity());
        if (quantityMissing.compareTo(BigDecimal.ZERO) <= 0 ){
            stock.setStockQuantity(stock.getStockQuantity().subtract(quantityRequested));
            getStockRepository().save(stock);
        }
        if (quantityMissing.compareTo(BigDecimal.ZERO) > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There is no Stock of products to produce the order. \n Missing "+quantityMissing+" from item "+
                            product.getProductName()+" to finish the order");
        }
    }
    private Payment createPayment(String clientCpf, BigDecimal totalPrice) {
        Payment payment = new Payment();
        Client client = clientService.findByCpfOrThrowBackBadRequestException(clientCpf);
        payment.setClientCpf(client);
        payment.setPointsSale(totalPrice);
        payment.setPaymentDate((dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now())));
        payment.setStatus(true);
        return getPaymentRepository().save(payment);
    }
    @Transactional
    public void finishOpenOrders(FinishOpenOrders finishOpenOrders) {
        Client client = clientService.findByCpfOrThrowBackBadRequestException(finishOpenOrders.getClientCpf());
        if (!getSaleRepository().existsByClientCpfAndStatusOpen(client.getClientId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The Client has no open orders to pay");
        }
        List<Sale> sale = getSaleRepository().findByClientCpfAndStatusOpen(client.getClientId());
        BigDecimal sumOpenOrdersTotalPrice = getSaleRepository().sumOpenOrders(client.getClientId());
        client.setClientPoints(client.getClientPoints().subtract(sumOpenOrdersTotalPrice));
        for (int i =0; i< sale.size();i++){
            Payment payment = getPaymentRepository().findBySaleId(sale.get(i).getSaleId());
            payment.setStatus(true);
            sale.get(i).setStatus(true);
                     getPaymentRepository().save(payment);
            getSaleRepository().save(sale.get(i));
        }
        getClientRepository().save(client);
    }
}
