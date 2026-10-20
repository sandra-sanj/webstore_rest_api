package fi.metropolia.sandraj.webstore.productcategories;

import fi.metropolia.sandraj.webstore.products.ProductsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductCategoryTests {

    @Autowired
    private ProductCategoriesRepository repository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listsAllCategories() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value((int) repository.count()));
    }

    @Test
    void getCategory() throws Exception {
        ProductCategories category = repository.findAll().get(0);

        mockMvc.perform(get("/categories/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(category.getName()));
    }

    @Test
    void categoryProductsAreCurrentVersionsOfThatCategoryOnly() throws Exception {
        int categoryId = productsRepository.findCurrentById(1).orElseThrow().getCategoryId();

        mockMvc.perform(get("/categories/" + categoryId + "/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].categoryId", everyItem(is(categoryId))))
                .andExpect(jsonPath("$[?(@.id == 1)]", hasSize(1)));
    }
}
