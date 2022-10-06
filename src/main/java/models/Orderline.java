package models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Orderline {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;    // Order is a reserved word in SQL, so we use "Order" instead
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;
    private LocalDate orderDate;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)
    private Cart cart;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        product.setAvailable(false);
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Double getTotal() {
        return product.getNewPrice() * quantity;
    }
}
