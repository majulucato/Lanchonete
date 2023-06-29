package sistema.lanchonete.client.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;
    @NotNull
    @Column(name = "client_name")
    private String clientName;
    @NotNull
    @Min(10)
    @Column(name = "client_cpf")
    private String clientCpf;
    @Column(name = "client_cnpj")
    private String clientCnpj;
    @Min(8)
    @Column(name = "client_phone")
    private String clientPhone;
    @Min(0)
    @Column(name = "client_points")
    private BigDecimal clientPoints;
}