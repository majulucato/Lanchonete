package sistema.lanchonete.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sistema.lanchonete.stock.domain.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*)>0 " +
                    "FROM stock s " +
                    "WHERE s.stock_name = :stockName")
    boolean existsByItemName(@Param("stockName")String stockName);
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM stock s " +
                    "WHERE s.stock_name = :stockName")
    Stock findByItemName(@Param("stockName")String stockName);
}
