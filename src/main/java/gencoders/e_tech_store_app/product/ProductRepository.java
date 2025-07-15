package gencoders.e_tech_store_app.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Public queries (only active products)
    List<Product> findByActiveTrue();

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(Long id);

    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.active = true")
    List<Product> searchActiveProducts(String query);

    List<Product> findTop8ByActiveTrueOrderByCreatedAtDesc();

    List<Product> findByDiscountPriceGreaterThanAndActiveTrue(BigDecimal price);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.active = true")
    List<Product> findAllInStockAndActive();

    // Admin queries (all products regardless of status)
    @Override
    Optional<Product> findById(Long id);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold")
    List<Product> findByStockQuantityLessThanEqual(int threshold);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Bulk operations
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :amount WHERE p.id = :id")
    int adjustStockQuantity(Long id, int amount);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.price = :newPrice, p.updatedAt = :now WHERE p.id = :id")
    int updateProductPrice(Long id, BigDecimal newPrice, LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.active = :active, p.updatedAt = :now WHERE p.id = :id")
    int setProductActiveStatus(Long id, boolean active, LocalDateTime now);

    // Advanced search for admin dashboard
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:active IS NULL OR p.active = :active)")
    Page<Product> searchProductsAdmin(String name,
                                      BigDecimal minPrice,
                                      BigDecimal maxPrice,
                                      Long categoryId,
                                      Boolean active,
                                      Pageable pageable);

    // Analytics queries
    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true")
    long countActiveProducts();

    @Query("SELECT SUM(p.stockQuantity) FROM Product p WHERE p.active = true")
    Long getTotalInventoryCount();

    @Query("SELECT COALESCE(SUM(p.stockQuantity * p.price), 0) FROM Product p WHERE p.active = true")
    BigDecimal getTotalInventoryValue();
    @Query("SELECT p FROM Product p WHERE " +
            "(:brand IS NULL OR LOWER(p.brand) = LOWER(:brand)) AND " +
            "(:memory IS NULL OR LOWER(p.memory) = LOWER(:memory)) AND " +
            "(:protection IS NULL OR LOWER(p.protection) = LOWER(:protection)) AND " +
            "(:screenType IS NULL OR LOWER(p.screenType) = LOWER(:screenType)) AND " +
            "(:screenSize IS NULL OR LOWER(p.screenSize) = LOWER(:screenSize)) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:battery IS NULL OR LOWER(p.batteryCapacity) = LOWER(:battery))")
    Page<Product> filterProducts(
            String brand,
            String memory,
            String protection,
            String screenType,
            String screenSize,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String battery,
            Pageable pageable
    );
    List<Product> findByFeaturedTrue();

    // ➜ flexible filtering used by getFilteredProducts
    @Query("""
           SELECT p FROM Product p
           WHERE p.active = true
             AND (:category IS NULL OR LOWER(p.category.name) = LOWER(:category))
             AND (:brand    IS NULL OR LOWER(p.brand)        = LOWER(:brand))
             AND (:memory   IS NULL OR LOWER(p.memory)       = LOWER(:memory))
             AND (:protection IS NULL OR LOWER(p.protection) = LOWER(:protection))
             AND (:screenType IS NULL OR LOWER(p.screenType) = LOWER(:screenType))
             AND (:screenSize IS NULL OR LOWER(p.screenSize) = LOWER(:screenSize))
             AND (:battery  IS NULL OR LOWER(p.batteryCapacity) = LOWER(:battery))
             AND (:minPrice IS NULL OR p.price >= :minPrice)
             AND (:maxPrice IS NULL OR p.price <= :maxPrice)
             AND (:q IS NULL OR
                    LOWER(p.name) LIKE LOWER(CONCAT('%',:q,'%')) OR
                    LOWER(p.description) LIKE LOWER(CONCAT('%',:q,'%')))
           """)
    Page<Product> filterProducts(String category,
                                 String brand,
                                 String memory,
                                 String protection,
                                 String screenType,
                                 String screenSize,
                                 String battery,
                                 BigDecimal minPrice,
                                 BigDecimal maxPrice,
                                 String q,
                                 Pageable pageable);
}