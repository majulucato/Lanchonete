package sistema.lanchonete.sale.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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
    @Column(name = "table_number")
    private Integer tableNumber;
    @ManyToOne
    @NotEmpty
    @JoinColumn(name = "client_cpf")
    private Client clientCpf;
    @ManyToOne
    @JoinColumn(name = "product_name")
    private Product productName;
    @Column(name = "quantity_requested")
    private List<BigDecimal> quantityRequested;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
}