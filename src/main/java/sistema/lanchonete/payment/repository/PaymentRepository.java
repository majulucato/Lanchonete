package sistema.lanchonete.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sistema.lanchonete.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM payment p " +
                    "WHERE p.client_cpf = :clientCpf")
    Page<Payment> findByClientCpf(@Param("clientCpf") String clientCpf, Pageable pageable);
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*)>0 " +
                    "FROM payment p " +
                    "WHERE p.client_cpf = :clientCpf")
    boolean existsByClientCpf(@Param("clientCpf")String clientCpf);
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM payment p " +
                    "WHERE p.client_cpf = :clientCpf " +
                    "AND p.payment_date = :paymentDate")
    Payment findByClientCpfAndTime(@Param("clientCpf")Long clientCpf, @Param("paymentDate")String paymentDate);
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM payment p " +
                    "WHERE p.sale_id = :saleId")
    Payment findBySaleId(@Param("saleId")Long saleId);
}
