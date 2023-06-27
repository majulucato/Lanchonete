package sistema.lanchonete.sale.service;

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
import sistema.lanchonete.sale.domain.Sale;
import sistema.lanchonete.sale.dto.SalePostRequestBody;
import sistema.lanchonete.sale.dto.SalePutRequestBody;
import sistema.lanchonete.sale.repository.SaleRepository;
import sistema.lanchonete.sale.util.DateUtil;
import sistema.lanchonete.stock.domain.Stock;
import sistema.lanchonete.stock.repository.StockRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
        System.out.println("Previous request delete, you can now post a new order");
    }

    @Transactional
    public Sale save(@Validated SalePostRequestBody salePostRequestBody) {
        validateClient(salePostRequestBody.getClientCpf());//ok
        Client client = clientService.findByCpfOrThrowBackBadRequestException(salePostRequestBody.getClientCpf());
        ArrayList<Product> productList = new ArrayList<>();
        salePostRequestBody.setTotalPrice(BigDecimal.valueOf(0));
        for (int i = 0; i < salePostRequestBody.getProductName().size(); i++) {
            Product product = getProductService().findByNameOrThrowBackBadRequestException(
                    salePostRequestBody.getProductName().get(i));
            productList.add(product);
            salePostRequestBody.setProductId(productList);
            if (!product.getRecipe()){
                validateProductStock(product.getProductName(), salePostRequestBody.getQuantityRequested().get(i));
            } else {
               validateRecipeIngredientsStock(product.getProductName(),
                       salePostRequestBody.getQuantityRequested().get(i));
            }
            BigDecimal sumPoints = salePostRequestBody.getTotalPrice().add(product.getProductCost());
            salePostRequestBody.setTotalPrice(sumPoints);
            salePostRequestBody.setProductId(productList);
        }
        validatePaymentPoints(salePostRequestBody.getClientCpf(), salePostRequestBody.getTotalPrice());
        return getSaleRepository().save(Sale.builder().tableNumber(salePostRequestBody.getTableNumber())
                .clientCpf(client)
                .productName(salePostRequestBody.getProductName())
                .quantityRequested(salePostRequestBody.getQuantityRequested())
                .totalPrice(salePostRequestBody.getTotalPrice()).build()); // total price = price to pay for the order
    }
    private void validateClient(String clientCpf) {
        //search if client exists, if not create client before proceeding with order;
        if (!clientRepository.existsClientCpf(clientCpf)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client doesn't exists in database, please " +
                    "register the client before proceeding with the request");
        }
    }
    private void validatePaymentPoints(String clientCpf, BigDecimal totalPrice){
        Client client = clientService.findByCpfOrThrowBackBadRequestException(clientCpf);
        if (totalPrice.compareTo(client.getClientPoints())>0){
            //return value of points missing to complete the payment.
            BigDecimal pointsMissing = totalPrice.subtract(client.getClientPoints());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Client "+clientCpf+" doesn't have enough points to finish the payment." +
                            "\nTotal points = "+totalPrice+"\nClient Points = "+client.getClientPoints()+
                            "\nPoints needed to finish payment = "+pointsMissing);
        }
        if (totalPrice.compareTo(client.getClientPoints())<=0){
            //subtract total price from points and finish payment
            createPayment(clientCpf,totalPrice);
            client.setClientPoints(client.getClientPoints().subtract(totalPrice));
            getClientRepository().save(client);
        }
    }
    private void validateRecipeIngredientsStock(String productName, BigDecimal quantityRequested){
        Recipe recipe = getRecipeRepository().findByRecipeName(productName);
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
        payment.setClientCpf(clientService.findByCpfOrThrowBackBadRequestException(clientCpf));
        payment.setPointsSale(totalPrice);
        payment.setPaymentDate((dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now())));
        payment.setStatus(false);
        return getPaymentRepository().save(payment);
    }
}
