import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingServiceImpl;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

class ShoppingServiceTest {
    private final ProductDao productDAO = Mockito.mock(ProductDao.class);

    private final ShoppingServiceImpl shoppingService = new ShoppingServiceImpl(productDAO);


    /**
     * Тестирование возможности покупки товаров, если корзина пуста
     */

    @Test
    void testCanBuyProductsIfCartIsEmpty() {
        Cart cart = Mockito.mock(Cart.class);

        when(cart.getProducts()).thenReturn(new HashMap<>());

        try {
            Assertions.assertFalse(shoppingService.buy(cart));
        } catch (BuyException e) {
            throw new RuntimeException(e);
        }

        verify(productDAO, never()).save(any());

    }

    /**
     * Тестирование возможности покупки товара, если корзина не пуста и все товары в корзине есть в наличии в нужном количестве.
     */
    @Test
    void testCanBuyProductsIfCartIsNotEmpty() {
        Customer customer = new Customer(0, "8-800-555-35-35");
        Cart cart = new Cart(customer);
        Product firstTestProduct = new Product();
        Product secondTestProduct = new Product();
        firstTestProduct.addCount(5);
        secondTestProduct.addCount(5);
        cart.add(firstTestProduct, 2);
        cart.add(secondTestProduct, 2);

        boolean isSuccessfully;
        try {
            isSuccessfully = shoppingService.buy(cart);
        } catch (BuyException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertTrue(isSuccessfully);
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
     * Тестирование логики покупки товара -> кол-во товара на складе должно уменьшиться
     */
    @Test
    void testAmountOfProductShouldDecreaseWhenBuy() {
        Customer customer = new Customer(0, "8-800-555-35-35");
        Cart cart = new Cart(customer);
        Product testProduct = new Product();
        testProduct.addCount(5);
        cart.add(testProduct, 2);

        try {
            shoppingService.buy(cart);
        } catch (BuyException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(3, testProduct.getCount());
    }


    /**
     * Тестирование получения продукта по имени
     */
    @Test
    void testCanGetProductByName() {
        shoppingService.getProductByName(anyString());

        verify(productDAO).getByName(anyString());
    }

    /**
     * Тестирование получения всех продуктов
     */
    @Test
    void testCanGetAllProducts() {
        shoppingService.getAllProducts();

        verify(productDAO).getAll();
    }
}