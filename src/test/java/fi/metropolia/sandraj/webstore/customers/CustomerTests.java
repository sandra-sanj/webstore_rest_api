package fi.metropolia.sandraj.webstore.customers;

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
class CustomerTests {

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private MockMvc mockMvc;

    private Customers newCustomer() {
        return customersRepository.saveAndFlush(
                new Customers("Test", "Tester", "test@test.com", "0123456789"));
    }

    @Test
    void createCustomer() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Test","lastName":"Tester","email":"test@test.com","phone":"0123456789"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Test"));
    }

    @Test
    void createdEmailIsTrimmedAndLowercase() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Test","lastName":"Tester","email":" Test@Test.COM ","phone":"0123456789"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void getCustomer() throws Exception {
        int id = newCustomer().getId();

        mockMvc.perform(get("/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.companyName").doesNotExist());
    }

    @Test
    void updateCustomer() throws Exception {
        int id = newCustomer().getId();

        mockMvc.perform(put("/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Test","lastName":"Updated","email":"test@test.com","phone":"0123456789"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"));
    }
}
