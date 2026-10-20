package fi.metropolia.sandraj.webstore.customeraddresses;

import fi.metropolia.sandraj.webstore.customers.Customers;
import fi.metropolia.sandraj.webstore.customers.CustomersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerAddressTests {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomerAddressesRepository repository;

    @Autowired
    private MockMvc mockMvc;

    private Customers customer;

    @BeforeEach
    void setUp() {
        customer = customersRepository.saveAndFlush(
                new Customers("Test", "Tester", "test@test.com", "0123456789"));
    }

    private CustomerAddresses newAddress(String city) {
        return repository.saveAndFlush(
                new CustomerAddresses(customer, "Testikatu 1", "00100", city, "Finland"));
    }

    @Test
    void addAddress() throws Exception {
        mockMvc.perform(post("/customers/" + customer.getId() + "/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"streetAddress":"Testikatu 1","postalCode":"00100","city":"Helsinki","country":"Finland"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.customerId").value(customer.getId()));
    }

    @Test
    void addressShowsCustomerIdInsteadOfCustomer() throws Exception {
        int id = newAddress("Helsinki").getId();

        mockMvc.perform(get("/addresses/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customer.getId()))
                .andExpect(jsonPath("$.customer").doesNotExist());
    }

    @Test
    void listCustomerAddresses() throws Exception {
        newAddress("Helsinki");
        newAddress("Espoo");

        mockMvc.perform(get("/customers/" + customer.getId() + "/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void firstAddressIsTheOldest() throws Exception {
        int firstId = newAddress("Helsinki").getId();
        newAddress("Espoo");

        mockMvc.perform(get("/customers/" + customer.getId() + "/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(firstId));
    }

    @Test
    void updateAddress() throws Exception {
        int id = newAddress("Helsinki").getId();

        mockMvc.perform(put("/addresses/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"streetAddress":"Testikatu 1","postalCode":"02100","city":"Espoo","country":"Finland"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Espoo"));
    }
}
