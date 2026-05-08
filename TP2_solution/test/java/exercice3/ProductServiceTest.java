package exercice3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductApiClient productApiClient;

    @InjectMocks
    private ProductService productService;


    @Test
    void testGetProduct_success() {
        // ARRANGE
        Product mockProduct = new Product("P001", "Laptop", 999.99);
        when(productApiClient.getProduct("P001")).thenReturn(mockProduct);

        // ACT
        Product result = productService.getProduct("P001");

        // ASSERT
        assertNotNull(result);
        assertEquals("P001", result.getId());
        assertEquals("Laptop", result.getName());
        assertEquals(999.99, result.getPrice(), 0.001);

        // Vérifie que getProduct a bien été appelé avec le bon argument
        verify(productApiClient, times(1)).getProduct("P001");
    }


    @Test
    void testGetProduct_invalidFormat_returnsNull() {
        // ARRANGE
        when(productApiClient.getProduct("P999")).thenReturn(null);

        // ACT
        Product result = productService.getProduct("P999");

        // ASSERT
        assertNull(result, "Le produit devrait être null en cas de format incompatible");
        verify(productApiClient, times(1)).getProduct("P999");
    }


    @Test
    void testGetProduct_apiFailure_throwsApiException() {
        when(productApiClient.getProduct("P404"))
                .thenThrow(new ApiException("API unavailable: timeout"));

        ApiException exception = assertThrows(ApiException.class, () ->
                productService.getProduct("P404")
        );

        assertEquals("API unavailable: timeout", exception.getMessage());
        verify(productApiClient, times(1)).getProduct("P404");
    }


    @Test
    void testProduct_getters() {
        Product product = new Product("P010", "Monitor", 299.99);

        assertEquals("P010",   product.getId());
        assertEquals("Monitor", product.getName());
        assertEquals(299.99,   product.getPrice(), 0.001);
    }


    @Test
    void testProduct_toString() {
        Product product = new Product("P020", "Headphones", 89.99);
        String str = product.toString();

        assertNotNull(str);
        assertTrue(str.contains("Headphones"));
        assertTrue(str.contains("P020"));
    }


    @Test
    void testApiException_withCause() {
        Throwable cause = new RuntimeException("network error");
        ApiException ex = new ApiException("wrapped", cause);

        assertEquals("wrapped", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }


    @Test
    void testGetProduct_multipleProducts() {
        Product p1 = new Product("P001", "Laptop", 999.99);
        Product p2 = new Product("P002", "Tablet", 499.99);
        when(productApiClient.getProduct("P001")).thenReturn(p1);
        when(productApiClient.getProduct("P002")).thenReturn(p2);

        assertEquals("Laptop", productService.getProduct("P001").getName());
        assertEquals("Tablet", productService.getProduct("P002").getName());

        verify(productApiClient).getProduct("P001");
        verify(productApiClient).getProduct("P002");
    }
}
