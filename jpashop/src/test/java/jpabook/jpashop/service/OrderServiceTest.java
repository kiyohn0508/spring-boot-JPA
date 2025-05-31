package jpabook.jpashop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepositoy;

@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepositoy orderRepositoy;

    @Test
    public void 상품주문() throws Exception{
        // given
        Member member = createMember();

        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        // when 
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        
        // then
        Order getOrder = orderRepositoy.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus(),"상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(),"주문한 상품 종류 수가 정확해야 한다.");
        assertEquals(10000*orderCount, getOrder.getTotalPrice(), "주문 갸격은 가격 * 수량이다.");
        assertEquals(8,book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception{
        // given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;
        
        // when, then
        assertThrows(NotEnoughStockException.class,
         () -> orderService.order(member.getId(), item.getId(), orderCount));
    }

    @Test
    public void 주문취소() throws Exception{
        // given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);
        
        // then
        Order getOrder = orderRepositoy.findOne(orderId);

        assertEquals(OrderStatus.CANCEL ,getOrder.getStatus(), "주문 취소시 상태는 CANCEL이다." );
        assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야한다.");
    }

    private Member createMember(){
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQiantity){
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQiantity);
        em.persist(book);
        return book;
    }
}
