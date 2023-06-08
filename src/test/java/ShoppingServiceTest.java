import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingService;
import shopping.ShoppingServiceImpl;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

class ShoppingServiceTest {
    private final ProductDao productDAO = Mockito.mock(ProductDao.class);
    private final ShoppingService shoppingService = new ShoppingServiceImpl(productDAO);

    /**
     * Тестирование невозможности покупки товаров, если корзина пуста
     */
    @Test
    void testCanBuyProductsIfCartIsEmpty() throws BuyException {
        Cart cart = Mockito.mock(Cart.class);

        when(cart.getProducts()).thenReturn(new HashMap<>());


        Assertions.assertFalse(shoppingService.buy(cart));

        verify(productDAO, never()).save(any());
    }

    /**
     * Тестирование возможности покупки товара, если корзина не пуста и все товары в корзине есть в наличии в нужном количестве.
     */
    @Test
    void testCanBuyProductsIfCartIsNotEmpty() throws BuyException {
        Customer customer = new Customer(0, "8-800-555-35-35");
        Cart cart = new Cart(customer);
        Product firstTestProduct = new Product();
        Product secondTestProduct = new Product();
        firstTestProduct.addCount(5);
        secondTestProduct.addCount(5);
        cart.add(firstTestProduct, 2);
        cart.add(secondTestProduct, 2);


        Assertions.assertTrue(shoppingService.buy(cart));
        Assertions.assertEquals(3, firstTestProduct.getCount());
        Assertions.assertEquals(3, secondTestProduct.getCount());
        verify(productDAO, times(2)).save(any(Product.class));
    }

    /**
     * Тестирование невозможности покупки товара, если покупка конкретного товара невозможна по причине отстутсвия нужного кол-во на складе
     */
    @Test
    void testCantBuyProducts() {
        Cart cart = Mockito.mock(Cart.class);
        Product testProduct = new Product();
        testProduct.addCount(3);

        Mockito.when(cart.getProducts()).thenReturn(Map.of(testProduct, 5));

        Assertions.assertThrows(BuyException.class, () -> shoppingService.buy(cart));

        verify(productDAO, never()).save(any());
    }

    /**
     * Тестирование получения продукта по имени
     */
    @Test
    void testCanGetProductByName() {
        String productName = "test";
        shoppingService.getProductByName(productName);

        verify(productDAO).getByName(productName);
    }

    /**
     * Тестирование получения всех продуктов
     */
    @Test
    void testCanGetAllProducts() {
        shoppingService.getAllProducts();

        verify(productDAO).getAll();
    }

    /**
     * Тестирование получения корзины покупателя
     */
    @Test
    void testCanGetCart() {
        Customer customer = new Customer(0, "8-800-555-35-35");
        Cart cart = new Cart(customer);

        Cart cart1 = shoppingService.getCart(customer);

        Assertions.assertNotNull(cart1);


    }
}