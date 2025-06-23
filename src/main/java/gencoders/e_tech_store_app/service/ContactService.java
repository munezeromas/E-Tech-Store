package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.model.ContactMessage;
import gencoders.e_tech_store_app.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;

    public ContactMessage saveMessage(ContactMessage message) {
        message.setCreatedAt(LocalDateTime.now());
        return contactRepository.save(message);
    }
}
