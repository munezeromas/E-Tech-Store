package gencoders.e_tech_store_app.role;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Initialize default roles when the application starts
    @PostConstruct
    public void initDefaultRoles() {
        for (ERole role : ERole.values()) {
            if (!roleRepository.existsByName(role)) {
                roleRepository.save(new Role(role));
            }
        }
    }

    // Fetch default user role
    public Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: ROLE_USER not found. Did you initialize the database?"));
        roles.add(userRole);
        return roles;
    }

    // Fetch admin role
    public Set<Role> getAdminRoles() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: ROLE_ADMIN not found. Did you initialize the database?"));
        roles.add(adminRole);
        return roles;
    }

    // Check if a role exists
    public boolean existsByName(ERole role) {
        return roleRepository.existsByName(role);
    }
}
