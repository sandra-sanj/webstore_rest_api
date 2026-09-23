package fi.metropolia.sandraj.webstore.orders;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByCustomer_Id(int customerId);
}
