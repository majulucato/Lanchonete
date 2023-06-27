package sistema.lanchonete.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sistema.lanchonete.product.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM product p " +
                    "WHERE p.product_name = :productName")
    Product findByProductName(@Param("productName")String productName);
    @Query(nativeQuery = true,
            value = "SELECT COUNT(*)>0 " +
                    "FROM product p " +
                    "WHERE p.product_name = :productName")
    boolean exitsProductName(@Param("productName")String productName);
}
