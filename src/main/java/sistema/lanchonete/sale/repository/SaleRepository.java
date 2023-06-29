package sistema.lanchonete.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sistema.lanchonete.client.domain.Client;
import sistema.lanchonete.sale.domain.Sale;

import java.math.BigDecimal;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*)>0 " +
                    "FROM sale s " +
                    "WHERE s.client_cpf = :clientCpf")
    Boolean existsClientCpf(@Param("clientCpf") String clientCpf);
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM sale s " +
                    "WHERE s.client_cpf = :clientCpf")
    Sale findByClientCpf(@Param("clientCpf") String clientCpf);
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM sale s " +
                    "WHERE s.client_cpf = :clientCpf " +
                    "AND s.sale_status = false")
    List<Sale> findByClientCpfAndStatusOpen(@Param("clientCpf") Long clientCpf);
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*)>0 " +
                    "FROM sale s " +
                    "WHERE s.client_cpf = :clientCpf " +
                    "AND s.sale_status = false")
    Boolean existsByClientCpfAndStatusOpen(@Param("clientCpf") Long clientCpf);
    @Query(nativeQuery = true,
            value = "SELECT SUM(s.total_price) " +
                    "FROM sale s " +
                    "WHERE s.client_cpf = :clientCpf " +
                    "AND s.sale_status = false")
    BigDecimal sumOpenOrders(@Param("clientCpf") Long clientCpf);
}
