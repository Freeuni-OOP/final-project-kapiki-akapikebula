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

    // Step 1: get a page of matching product IDs — pure SQL, no collection join,
    // so pagination and sorting work correctly at the DB level
    @Query("""
        SELECT DISTINCT p.id FROM Product p
        JOIN p.shopProducts sp
        WHERE (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                               OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))
          AND (:minPrice IS NULL OR sp.price >= :minPrice)
          AND (:maxPrice IS NULL OR sp.price <= :maxPrice)
    """)
    Page<Long> searchIds(
            @Param("query") String query,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    // When sorting by price, we need the DB to compute lowestPrice per product
// and sort by it — otherwise we can only sort within the current page in Java
    @Query("""
    SELECT p.id FROM Product p
    JOIN p.shopProducts sp
    WHERE (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                           OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))
      AND (:minPrice IS NULL OR sp.price >= :minPrice)
      AND (:maxPrice IS NULL OR sp.price <= :maxPrice)
    GROUP BY p.id
    ORDER BY MIN(sp.price) ASC
""")
    Page<Long> searchIdsSortByPriceAsc(
            @Param("query") String query,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("""
    SELECT p.id FROM Product p
    JOIN p.shopProducts sp
    WHERE (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                           OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))
      AND (:minPrice IS NULL OR sp.price >= :minPrice)
      AND (:maxPrice IS NULL OR sp.price <= :maxPrice)
    GROUP BY p.id
    ORDER BY MIN(sp.price) DESC
""")
    Page<Long> searchIdsSortByPriceDesc(
            @Param("query") String query,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    // Step 2: fetch full product data (with shop listings) for a specific list of IDs
    // IN clause means one query total, not N queries
    @Query("""
        SELECT DISTINCT p FROM Product p
        JOIN FETCH p.shopProducts sp
        JOIN FETCH sp.shop
        WHERE p.id IN :ids
    """)
    List<Product> findByIdsWithListings(@Param("ids") List<Long> ids);

    Optional<Product> findByMatchKey(String matchKey);
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
