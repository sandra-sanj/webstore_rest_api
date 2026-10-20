package fi.metropolia.sandraj.webstore.products;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductTests {

    @Autowired
    private ProductsRepository repository;

    @Autowired
    private MockMvc mockMvc;

    private Products product1;

    @BeforeEach
    void setUp() {
        product1 = repository.findCurrentById(1).orElseThrow();
    }

    @Test
    void productListHasEachProductOnce() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 1)]", hasSize(1)))
                .andExpect(jsonPath("$[0].categoryId").exists());
    }

    @Test
    void getProductShowsCurrentPriceWithoutSuppliers() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(product1.getPrice().doubleValue()))
                .andExpect(jsonPath("$.suppliers").doesNotExist());
    }

    @Test
    void nameSearchIsPartialAndIgnoresCase() throws Exception {
        String part = product1.getName().substring(1, 4).toUpperCase();

        mockMvc.perform(get("/products").param("name", part))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", everyItem(containsStringIgnoringCase(part))))
                .andExpect(jsonPath("$[?(@.id == 1)]", hasSize(1)));
    }

    @Test
    void categoryFilterReturnsOnlyThatCategory() throws Exception {
        int categoryId = product1.getCategoryId();

        mockMvc.perform(get("/products").param("categoryId", String.valueOf(categoryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].categoryId", everyItem(is(categoryId))))
                .andExpect(jsonPath("$[?(@.id == 1)]", hasSize(1)));
    }

    @Test
    void priceRangeIsInclusive() throws Exception {
        String price = product1.getPrice().toPlainString();

        mockMvc.perform(get("/products").param("minPrice", price).param("maxPrice", price))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].price", everyItem(is(product1.getPrice().doubleValue()))))
                .andExpect(jsonPath("$[?(@.id == 1)]", hasSize(1)));
    }

    @Test
    void inStockLeavesOutSoldOutProducts() throws Exception {
        mockMvc.perform(get("/products").param("inStock", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].stockQuantity", everyItem(greaterThan(0))));
    }
}
