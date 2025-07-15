package gencoders.e_tech_store_app.product;

import gencoders.e_tech_store_app.category.CategoryService;
import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.storage.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;

    /* ---------- Public Queries ---------- */

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String q) {
        return productRepository.searchActiveProducts(q.toLowerCase());
    }

    @Transactional(readOnly = true)
    public List<Product> getNewArrivals() {
        return productRepository.findTop8ByActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrue();
    }

    @Transactional(readOnly = true)
    public List<Product> getDiscountedProducts() {
        return productRepository.findByDiscountPriceGreaterThanAndActiveTrue(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public Page<Product> getFilteredProducts(
            int page, int size, String sortBy, String sortDir,
            String category, String brand, String memory, String protection,
            String screenType, String screenSize,
            BigDecimal minPrice, BigDecimal maxPrice,
            String battery, String search) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.filterProducts(
                category, brand, memory, protection,
                screenType, screenSize, battery,
                minPrice, maxPrice, search, pageable
        );
    }


    @Transactional(readOnly = true)
    public List<Product> getBestSellers() {
        // Placeholder: Implement logic to fetch best-selling products (e.g., by total sales or order count)
        // Example: return productRepository.findTop10ByOrderBySalesCountDesc();
        // You need to add a 'salesCount' field or join with an order table in ProductRepository
        throw new UnsupportedOperationException("getBestSellers not implemented. Define sales metric in ProductRepository.");
    }

    /* ---------- Admin Queries ---------- */

    @Transactional(readOnly = true)
    public List<Product> getAllProductsForAdmin() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByStockQuantityLessThanEqual(threshold);
    }

    /* ---------- Mutations ---------- */

    public Product createProduct(ProductRequest req, List<MultipartFile> images) {
        Set<String> imageUrls = images.stream()
                .map(img -> cloudinaryService.uploadFile(img, "product_images"))
                .collect(Collectors.toSet());


        return productRepository.save(mapRequestToProduct(req));
    }

    public Product updateProduct(Long id, ProductRequest req) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        updateProductFromRequest(p, req);
        return productRepository.save(p);
    }

    public void deleteProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(p);
    }

    public Product updateStock(Long id, int qty) {
        Product p = getProductById(id);
        p.setStockQuantity(qty);
        p.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(p);
    }

    public Product updatePrice(Long id, double price) {
        Product p = getProductById(id);
        p.setPrice(BigDecimal.valueOf(price));
        p.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(p);
    }

    public Product applyDiscount(Long id, double percent) {
        Product p = getProductById(id);
        BigDecimal discount = p.getPrice().multiply(BigDecimal.valueOf(percent / 100D));
        p.setDiscountPrice(p.getPrice().subtract(discount));
        p.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(p);
    }

    /* ---------- Image Helpers ---------- */

    public Product addProductImages(Long id, List<MultipartFile> images) {
        Product p = getProductById(id);
        for (MultipartFile img : images) {
            String url = cloudinaryService.uploadFile(img, "products");
            p.getAdditionalImages().add(url);
        }
        return productRepository.save(p);
    }

    public Product removeProductImage(Long id, String url) {
        Product p = getProductById(id);
        if (p.getAdditionalImages().remove(url)) {
            cloudinaryService.deleteFile(url);
            productRepository.save(p);
        }
        return p;
    }

    public Product updateProductMainImage(Long id, MultipartFile newImg) {
        Product p = getProductById(id);

        if (p.getImageUrl() != null) {
            cloudinaryService.deleteFile(p.getImageUrl());
        }

        String url = cloudinaryService.uploadFile(newImg, "products");
        p.setImageUrl(url);
        return productRepository.save(p);
    }

    /* ---------- Internal Mappers ---------- */

    private Product mapRequestToProduct(ProductRequest r) {
        Product p = Product.builder()
                .name(r.getName())
                .description(r.getDescription())
                .price(r.getPrice())
                .discountPrice(r.getDiscountPrice())
                .stockQuantity(r.getStockQuantity())
                .imageUrl(r.getImageUrl())
                .additionalImages(defaultSet(r.getAdditionalImages()))
                .category(categoryService.getCategoryById(r.getCategoryId()))
                .active(true)
                .build();

        if (r.getSpecifications() != null) {
            r.getSpecifications().forEach(p::addSpecification);
        }
        return p;
    }

    private void updateProductFromRequest(Product p, ProductRequest r) {
        p.setName(r.getName());
        p.setDescription(r.getDescription());
        p.setPrice(r.getPrice());
        p.setDiscountPrice(r.getDiscountPrice());
        p.setStockQuantity(r.getStockQuantity());
        p.setImageUrl(r.getImageUrl());
        p.setAdditionalImages(defaultSet(r.getAdditionalImages()));
        p.setCategory(categoryService.getCategoryById(r.getCategoryId()));
        p.setUpdatedAt(LocalDateTime.now());

        p.getSpecifications().clear();
        if (r.getSpecifications() != null) r.getSpecifications().forEach(p::addSpecification);
    }

    private <T> Set<T> defaultSet(Set<T> set) {
        return set == null ? new HashSet<>() : set;
    }
}