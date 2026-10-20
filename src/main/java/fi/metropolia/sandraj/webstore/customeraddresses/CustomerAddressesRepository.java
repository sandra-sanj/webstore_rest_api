package fi.metropolia.sandraj.webstore.customeraddresses;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressesRepository extends JpaRepository<CustomerAddresses, Integer> {
    List<CustomerAddresses> findByCustomer_Id(Integer customerId);

    Optional<CustomerAddresses> findFirstByCustomer_IdOrderById(Integer customerId);
}
