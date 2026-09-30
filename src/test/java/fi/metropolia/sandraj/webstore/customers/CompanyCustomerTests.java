package fi.metropolia.sandraj.webstore.customers;

import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class CompanyCustomerTests {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    private CompanyCustomers newCompanyCustomer() {
        CompanyCustomers company = new CompanyCustomers(
                "Test", "Tester", "test@test.com", "0123456789", "Test Oy", "1234567-8");
        return customersRepository.saveAndFlush(company);
    }

    private int rowCount(String table, int id) {
        return ((Number) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM " + table + " WHERE id = ?1")
                .setParameter(1, id)
                .getSingleResult()).intValue();
    }

    @Test
    void savingCompanyCustomerWritesBothTables() {
        int id = newCompanyCustomer().getId();

        assertEquals(1, rowCount("customers", id));
        assertEquals(1, rowCount("companycustomers", id));
    }

    @Test
    void companyCustomerIsCompanyCustomerClass() {
        int id = newCompanyCustomer().getId();
        entityManager.clear();

        Customers loaded = customersRepository.findById(id).orElseThrow();

        CompanyCustomers company = assertInstanceOf(CompanyCustomers.class, loaded);
        assertEquals("Test Oy", company.getCompanyName());
        assertEquals("1234567-8", company.getCompanyId());
        assertEquals("Test", company.getFirstName());
    }

    @Test
    void deletingCompanyCustomerDeletesBothRows() {
        int id = newCompanyCustomer().getId();
        entityManager.clear();

        customersRepository.delete(customersRepository.findById(id).orElseThrow());
        customersRepository.flush();

        assertEquals(0, rowCount("companycustomers", id));
        assertEquals(0, rowCount("customers", id));
    }

    @Test
    void createCompanyCustomerEndpointSavesSubclass() throws Exception {
        String body = """
                {"firstName":"Test","lastName":"Tester","email":"test@test.com",
                 "phone":"0123456789","companyName":"Test Oy","companyId":"1234567-8"}
                """;

        String response = mockMvc.perform(post("/customers/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.companyName").value("Test Oy"))
                .andExpect(jsonPath("$.companyId").value("1234567-8"))
                .andReturn().getResponse().getContentAsString();

        int id = JsonPath.read(response, "$.id");
        entityManager.flush();
        entityManager.clear();

        assertInstanceOf(CompanyCustomers.class, customersRepository.findById(id).orElseThrow());
        assertEquals(1, rowCount("companycustomers", id));
    }

    @Test
    void getCompanyCustomerIncludesCompanyFields() throws Exception {
        int id = newCompanyCustomer().getId();
        entityManager.clear();

        mockMvc.perform(get("/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.companyName").value("Test Oy"))
                .andExpect(jsonPath("$.companyId").value("1234567-8"));
    }

}
