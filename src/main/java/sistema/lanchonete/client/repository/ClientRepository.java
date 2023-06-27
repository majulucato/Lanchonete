package sistema.lanchonete.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sistema.lanchonete.client.domain.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*)>0 " +
                    "FROM client c " +
                    "WHERE c.client_name = :clientName")
    Boolean existsClientName(@Param("clientName") String clientName);
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*)>0 " +
                    "FROM client c " +
                    "WHERE c.client_cpf = :clientCpf")
    Boolean existsClientCpf(@Param("clientCpf") String clientCpf);
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM client c " +
                    "WHERE c.client_name = :clientName")
    Client findByClientName(@Param("clientName") String clientName);
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM client c " +
                    "WHERE c.client_cpf = :clientCpf")
    Client findByClientCpf(@Param("clientCpf") String clientCpf);
}
