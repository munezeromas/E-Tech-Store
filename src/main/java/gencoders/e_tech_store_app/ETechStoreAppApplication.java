package gencoders.e_tech_store_app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "E-Tech Store API",
                version = "1.0",
                description = "Complete API documentation for E-Tech Store Application"
        )
)
@SpringBootApplication
public class ETechStoreAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(ETechStoreAppApplication.class, args);
    }
}
