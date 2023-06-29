package sistema.lanchonete.sale.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sistema.lanchonete.client.domain.Client;
import sistema.lanchonete.product.domain.Product;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "sale")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long saleId;
    @Min(1)
    @Max(15)
    @Column(name = "table_number")
    private Integer tableNumber;
    @ManyToOne
    @NotNull
    @JoinColumn(name = "client_cpf")
    private Client clientCpf;
    @Column(name = "product_name")
    private List<String> productName;
    @Column(name = "quantity_requested")
    private List<BigDecimal> quantityRequested;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @ManyToMany
    @JoinColumn(name = "product_id")
    private List<Product> productId;
    @Column(name = "sale_status")
    private Boolean status; // 0 - iniciado; 1 - pago;
    @Column(name = "sale_date")
    private String saleDate;
}