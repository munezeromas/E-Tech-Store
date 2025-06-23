package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactMessage, Long> {
}
