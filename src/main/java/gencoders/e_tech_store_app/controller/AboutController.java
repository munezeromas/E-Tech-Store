package gencoders.e_tech_store_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/about")
public class AboutController {

    @GetMapping
    public ResponseEntity<Map<String, String>> getAboutInfo() {
        return ResponseEntity.ok(Map.of(
                "title", "About Our Store",
                "content", "We are the best e-tech store...",
                "mission", "To provide quality tech products..."
        ));
    }
}
