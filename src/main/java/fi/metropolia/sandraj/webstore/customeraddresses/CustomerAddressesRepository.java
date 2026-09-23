package fi.metropolia.sandraj.webstore.customeraddresses;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerAddressesRepository extends JpaRepository<CustomerAddresses, Integer> {
    List<CustomerAddresses> findByCustomer_Id(Integer customerId);
}
