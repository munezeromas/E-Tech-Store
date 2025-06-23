package gencoders.e_tech_store_app.controller;

import gencoders.e_tech_store_app.model.ContactMessage;
import gencoders.e_tech_store_app.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<String> submitContact(@Valid @RequestBody ContactMessage message) {
        contactService.saveMessage(message);
        return ResponseEntity.ok("Message received");
    }
}