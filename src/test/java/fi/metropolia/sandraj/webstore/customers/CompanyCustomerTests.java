package fi.metropolia.sandraj.webstore.customers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
class CompanyCustomerTests {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    private CompanyCustomers newCompanyCustomer() {
        return customersRepository.saveAndFlush(new CompanyCustomers(
                "Test", "Tester", "test@test.com", "0123456789", "Test Oy", "1234567-8"));
    }

    @Test
    void createCompanyCustomer() throws Exception {
        mockMvc.perform(post("/customers/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Test","lastName":"Tester","email":"test@test.com",
                                 "phone":"0123456789","companyName":"Test Oy","companyId":"1234567-8"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.companyName").value("Test Oy"));
    }

    @Test
    void getCompanyCustomerIncludesCompanyFields() throws Exception {
        int id = newCompanyCustomer().getId();
        // Read back from both tables, not from the saved object
        entityManager.clear();

        mockMvc.perform(get("/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.companyName").value("Test Oy"))
                .andExpect(jsonPath("$.companyId").value("1234567-8"));
    }

    @Test
    void updateCompanyCustomer() throws Exception {
        int id = newCompanyCustomer().getId();

        mockMvc.perform(put("/customers/company/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Test","lastName":"Tester","email":"test@test.com",
                                 "phone":"0123456789","companyName":"Updated Oy","companyId":"7654321-0"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Updated Oy"));
    }

    @Test
    void updateCompanyCustomerIgnoresPrivateCustomers() throws Exception {
        int id = customersRepository.saveAndFlush(
                new Customers("Test", "Tester", "test@test.com", "0123456789")).getId();

        mockMvc.perform(put("/customers/company/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"companyName\":\"Test Oy\",\"companyId\":\"1234567-8\"}"))
                .andExpect(status().isNotFound());
    }
}
