package gencoders.e_tech_store_app.publiccontrollers;

import gencoders.e_tech_store_app.category.CategoryService;
import gencoders.e_tech_store_app.config.MessageResponse;
import gencoders.e_tech_store_app.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PublicProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public PublicProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/public")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String memory,
            @RequestParam(required = false) String protection,
            @RequestParam(required = false) String screenType,
            @RequestParam(required = false) String screenSize,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String battery,
            @RequestParam(required = false) String search
    ) {
        try {
            return ResponseEntity.ok(
                    productService.getFilteredProducts(
                            page, size, sortBy, sortDir,
                            category, brand, memory, protection,
                            screenType, screenSize, minPrice, maxPrice,
                            battery, search
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching products: " + e.getMessage()));
        }
    }


    @GetMapping("/public/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Product not found"));
        }
    }

    @GetMapping("/public/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            return ResponseEntity.ok(categoryService.getAllCategories());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching categories: " + e.getMessage()));
        }
    }

    @GetMapping("/public/featured")
    public ResponseEntity<?> getFeaturedProducts() {
        try {
            return ResponseEntity.ok(productService.getFeaturedProducts());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching featured products: " + e.getMessage()));
        }
    }

    @GetMapping("/public/bestsellers")
    public ResponseEntity<?> getBestSellers() {
        try {
            return ResponseEntity.ok(productService.getBestSellers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching best sellers: " + e.getMessage()));
        }
    }

    @GetMapping("/public/new-arrivals")
    public ResponseEntity<?> getNewArrivals() {
        try {
            return ResponseEntity.ok(productService.getNewArrivals());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching new arrivals: " + e.getMessage()));
        }
    }
}