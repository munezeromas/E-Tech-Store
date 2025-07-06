package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.model.Address;
import gencoders.e_tech_store_app.model.User;
import gencoders.e_tech_store_app.payload.request.AddressRequest;
import gencoders.e_tech_store_app.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public List<Address> getUserAddresses(User user) {
        return addressRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
    }

    @Transactional(readOnly = true)
    public Address getDefaultAddress(User user) {
        return addressRepository.findByUserAndIsDefaultTrue(user)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", "default"));
    }

    @Transactional
    public Address createAddress(AddressRequest request, User user) {
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setPhone(request.getPhone());
        address.setUser(user);

        // Set as default if first address
        address.setDefault(addressRepository.countByUser(user) == 0);

        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(Long id, AddressRequest request) {
        Address address = getAddressById(id);

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setPhone(request.getPhone());

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long id) {
        Address address = getAddressById(id);

        if (address.isDefault()) {
            List<Address> otherAddresses = addressRepository.findByUserAndIdNot(address.getUser(), id);
            if (!otherAddresses.isEmpty()) {
                Address newDefault = otherAddresses.get(0);
                newDefault.setDefault(true);
                addressRepository.save(newDefault);
            }
        }

        addressRepository.delete(address);
    }

    @Transactional
    public Address setDefaultAddress(Long id, User user) {
        addressRepository.findByUserAndIsDefaultTrue(user).ifPresent(currentDefault -> {
            currentDefault.setDefault(false);
            addressRepository.save(currentDefault);
        });

        Address newDefault = getAddressById(id);
        newDefault.setDefault(true);
        return addressRepository.save(newDefault);
    }
}