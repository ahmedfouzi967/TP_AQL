package exercice2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderDao orderDao;

    private OrderService orderService;
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        // Utilisation de vrais objets pour maximiser le coverage
        orderService = new OrderService(orderDao);
        orderController = new OrderController(orderService);
    }


    @Test
    void testCreateOrder_fullIntegration_callsDao() {
        Order order = new Order(1, "Laptop", 2);

        orderController.createOrder(order);

        verify(orderDao, times(1)).saveOrder(order);
    }


    @Test
    void testOrder_getters() {
        Order order = new Order(10, "Phone", 3);

        assertEquals(10, order.getId());
        assertEquals("Phone", order.getProduct());
        assertEquals(3, order.getQuantity());
    }


    @Test
    void testOrder_toString() {
        Order order = new Order(1, "Tablet", 5);
        String str = order.toString();

        assertNotNull(str);
        assertTrue(str.contains("Tablet"));
    }


    @Test
    void testCreateOrder_multipleOrders_allSaved() {
        Order order1 = new Order(1, "Laptop", 1);
        Order order2 = new Order(2, "Mouse", 3);
        Order order3 = new Order(3, "Keyboard", 2);

        orderController.createOrder(order1);
        orderController.createOrder(order2);
        orderController.createOrder(order3);

        verify(orderDao, times(1)).saveOrder(order1);
        verify(orderDao, times(1)).saveOrder(order2);
        verify(orderDao, times(1)).saveOrder(order3);
    }


    @Test
    void testCreateOrder_neverCalledIfNotInvoked() {
        verify(orderDao, never()).saveOrder(any());
    }


    @Test
    void testCreateOrder_zeroQuantity() {
        Order order = new Order(99, "FreeItem", 0);

        orderController.createOrder(order);

        assertEquals(0, order.getQuantity());
        verify(orderDao, times(1)).saveOrder(order);
    }
}
