package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.model.ERole;
import gencoders.e_tech_store_app.model.Role;
import gencoders.e_tech_store_app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        return roles;
    }
}