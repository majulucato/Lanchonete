package sistema.lanchonete.payment.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sistema.lanchonete.client.domain.Client;

import java.math.BigDecimal;
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;
    @ManyToOne
    @JoinColumn(name = "client_cpf")
    private Client clientCpf;
    @Column(name = "payment_points_sale")
    private BigDecimal pointsSale;
    @Column(name = "payment_date")
    private String paymentDate;
    @Column(name = "payment_status")
    private Boolean status; // 0 - started; 1 - finished;
}