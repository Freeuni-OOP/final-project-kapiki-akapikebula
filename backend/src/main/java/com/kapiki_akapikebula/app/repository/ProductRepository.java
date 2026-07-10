package com.kapiki_akapikebula.app.repository;

import com.kapiki_akapikebula.app.dto.MatchedProductDTO;
import com.kapiki_akapikebula.app.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(
            value = """
            SELECT DISTINCT p FROM Product p
            JOIN FETCH p.shopProducts sp
            JOIN FETCH sp.shop
            WHERE (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                                   OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:minPrice IS NULL OR sp.price >= :minPrice)
              AND (:maxPrice IS NULL OR sp.price <= :maxPrice)
        """,
            countQuery = """
            SELECT COUNT(DISTINCT p) FROM Product p
            JOIN ShopProducts sp ON sp.product = p
            WHERE (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                                   OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:minPrice IS NULL OR sp.price >= :minPrice)
              AND (:maxPrice IS NULL OR sp.price <= :maxPrice)
        """
    )
    Page<Product> search(
            @Param("query") String query,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    Optional<Product> findByMatchKey(String matchKey);
    // Used to narrow down candidates before running the fuzzy comparison
    List<Product> findByNameContainingIgnoreCase(String token);

    @Query("SELECT p.id AS id, p.name AS name, p.imageUrl AS imageUrl, " +
        "MIN(sp.price) AS minPrice, MAX(sp.price) AS maxPrice, " +
        "COUNT(sp.id) AS storesCount " +
        "FROM Product p " +
        "JOIN p.shopProducts sp " +
        "GROUP BY p.id, p.name, p.imageUrl " +
        "HAVING COUNT(sp.id) > 1")
List<MatchedProductDTO> findMatchedProductsForHomePage();

}
