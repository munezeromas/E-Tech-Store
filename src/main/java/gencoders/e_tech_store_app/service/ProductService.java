package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.model.Product;
import gencoders.e_tech_store_app.model.ProductSpecification;
import gencoders.e_tech_store_app.payload.request.ProductRequest;
import gencoders.e_tech_store_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String query) {
        return productRepository.searchProducts(query.toLowerCase());
    }

    @Transactional(readOnly = true)
    public List<Product> getNewArrivals() {
        return productRepository.findTop8ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Product> getDiscountedProducts() {
        return productRepository.findByDiscountPriceGreaterThan(BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findAllInStock();
    }

    public Product createProduct(ProductRequest request) {
        Product product = mapRequestToProduct(request);
        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, ProductRequest request) {
        Product product = getProductById(productId);
        updateProductFromRequest(product, request);
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    private Product mapRequestToProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .category(categoryService.getCategoryById(request.getCategoryId()))
                .build();

        if (request.getSpecifications() != null) {
            request.getSpecifications().forEach(spec ->
                    product.addSpecification(new ProductSpecification(spec.getSpecKey(), spec.getSpecValue())));
        }

        return product;
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(categoryService.getCategoryById(request.getCategoryId()));

        // Update specifications
        product.getSpecifications().clear();
        if (request.getSpecifications() != null) {
            request.getSpecifications().forEach(spec ->
                    product.addSpecification(new ProductSpecification(spec.getSpecKey(), spec.getSpecValue())));
        }
    }
}