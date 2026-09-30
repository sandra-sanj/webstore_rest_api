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
class CustomerTests {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    private Customers newCustomer() {
        Customers customer = new Customers("Test", "Tester", "test@test.com", "0123456789");
        return customersRepository.saveAndFlush(customer);
    }

    private int rowCount(String table, int id) {
        return ((Number) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM " + table + " WHERE id = ?1")
                .setParameter(1, id)
                .getSingleResult()).intValue();
    }

    @Test
    void createCustomerEndpointSavesCustomer() throws Exception {
        String body = """
                {"firstName":"Test","lastName":"Tester","email":"test@test.com","phone":"0123456789"}
                """;

        String response = mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andReturn().getResponse().getContentAsString();

        int id = JsonPath.read(response, "$.id");
        entityManager.flush();

        assertEquals(1, rowCount("customers", id));
        assertEquals(0, rowCount("companycustomers", id));
    }

    @Test
    void getCustomerReturnsCustomer() throws Exception {
        int id = newCustomer().getId();
        entityManager.clear();

        mockMvc.perform(get("/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.companyName").doesNotExist());
    }

    @Test
    void updateCustomerEndpointChangesFields() throws Exception {
        int id = newCustomer().getId();
        entityManager.clear();

        String body = """
                {"firstName":"Test","lastName":"Updated","email":"updated@test.com","phone":"0123456789"}
                """;

        mockMvc.perform(put("/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"));
        entityManager.flush();
        entityManager.clear();

        Customers reloaded = customersRepository.findById(id).orElseThrow();
        assertEquals("Updated", reloaded.getLastName());
        assertEquals("updated@test.com", reloaded.getEmail());
    }
}
