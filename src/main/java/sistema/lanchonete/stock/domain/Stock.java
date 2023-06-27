package sistema.lanchonete.stock.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;
    @Column(name = "stock_name")
    private String stockName;
    @Column(name = "stock_quantity")
    private BigDecimal stockQuantity;
    @Column(name = "recipe")
    private Boolean recipe;
}