package fi.metropolia.sandraj.webstore.products;

import fi.metropolia.sandraj.webstore.suppliers.Suppliers;
import fi.metropolia.sandraj.webstore.suppliers.SuppliersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class ProductSupplierTests {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private SuppliersRepository suppliersRepository;

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    private Products product1;
    private Products product2;
    private Suppliers supplierA;
    private Suppliers supplierB;

    @BeforeEach
    void setUp() {
        product1 = productsRepository.findCurrentById(1).orElseThrow();
        product2 = productsRepository.findCurrentById(2).orElseThrow();
        product1.getSuppliers().clear();
        product2.getSuppliers().clear();
        supplierA = suppliersRepository.save(new Suppliers("Test Supplier A"));
        supplierB = suppliersRepository.save(new Suppliers("Test Supplier B"));
        productsRepository.flush();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private int linkRowCount(int productId, int supplierId) {
        return ((Number) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM productsuppliers WHERE product_id = ?1 AND supplier_id = ?2")
                .setParameter(1, productId)
                .setParameter(2, supplierId)
                .getSingleResult()).intValue();
    }

    @Test
    void productCanHaveManySuppliers() {
        product1.addSupplier(supplierA);
        product1.addSupplier(supplierB);
        flushAndClear();

        Products reloaded = productsRepository.findCurrentById(1).orElseThrow();
        assertEquals(2, reloaded.getSuppliers().size());
    }

    @Test
    void supplierCanHaveManyProducts() {
        product1.addSupplier(supplierA);
        product2.addSupplier(supplierA);
        flushAndClear();

        List<Products> products = productsRepository.findCurrentBySupplierId(supplierA.getId());
        assertEquals(2, products.size());

        Suppliers reloaded = suppliersRepository.findById(supplierA.getId()).orElseThrow();
        assertEquals(2, reloaded.getProducts().size());
    }

    @Test
    void productWithSeveralVersionsIsListedOnce() {
        int versions = ((Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM products WHERE id = 1").getSingleResult()).intValue();
        assertTrue(versions > 1);

        product1.addSupplier(supplierA);
        flushAndClear();

        assertEquals(1, linkRowCount(1, supplierA.getId()));
        assertEquals(1, productsRepository.findCurrentBySupplierId(supplierA.getId()).size());

        Suppliers reloaded = suppliersRepository.findById(supplierA.getId()).orElseThrow();
        assertEquals(1, reloaded.getProducts().size());
        Products fromSupplier = reloaded.getProducts().iterator().next();
        assertEquals(productsRepository.findCurrentById(1).orElseThrow().getPrice(), fromSupplier.getPrice());
    }

    @Test
    void removingSupplierDeletesOnlyTheLinkRow() {
        product1.addSupplier(supplierA);
        entityManager.flush();

        product1.removeSupplier(supplierA.getId());
        flushAndClear();

        assertEquals(0, linkRowCount(1, supplierA.getId()));
        assertTrue(productsRepository.findCurrentById(1).isPresent());
        assertTrue(suppliersRepository.existsById(supplierA.getId()));
    }

    @Test
    void getProductDoesNotIncludeSuppliers() throws Exception {
        product1.addSupplier(supplierA);
        flushAndClear();

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.suppliers").doesNotExist());
    }

    @Test
    void getSuppliersForProductListsSuppliers() throws Exception {
        product1.addSupplier(supplierA);
        flushAndClear();

        mockMvc.perform(get("/products/1/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Supplier A"))
                .andExpect(jsonPath("$[0].products").doesNotExist());
    }

    @Test
    void addSupplierEndpointCreatesLink() throws Exception {
        mockMvc.perform(post("/products/1/suppliers/" + supplierA.getId()))
                .andExpect(status().isOk());
        flushAndClear();

        assertEquals(1, linkRowCount(1, supplierA.getId()));
    }

    @Test
    void removeSupplierEndpointDeletesLink() throws Exception {
        product1.addSupplier(supplierA);
        product1.addSupplier(supplierB);
        flushAndClear();

        mockMvc.perform(delete("/products/1/suppliers/" + supplierA.getId()))
                .andExpect(status().isOk());
        entityManager.flush();

        assertEquals(0, linkRowCount(1, supplierA.getId()));
        assertEquals(1, linkRowCount(1, supplierB.getId()));
    }

    @Test
    void getProductsForSupplierListsLinkedProducts() throws Exception {
        product1.addSupplier(supplierA);
        product2.addSupplier(supplierA);
        flushAndClear();

        mockMvc.perform(get("/suppliers/" + supplierA.getId() + "/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

}
