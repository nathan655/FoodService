package models;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;

;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getValidThrough() {
        return validThrough;
    }

    public void setValidThrough(LocalDate validThrough) {
        this.validThrough = validThrough;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public ArrayList<Orderline> getOrderlines() {
        return orderlines;
    }

    public void setOrderlines(ArrayList<Orderline> orderlines) {
        this.orderlines = orderlines;
    }

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private LocalDate validThrough;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @OneToMany(mappedBy = "cart")
    private ArrayList<Orderline> orderlines;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Order makeReserved() {
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderlines(orderlines);
        order.setStore(store);
        order.setPaymentType(paymentType);
        StringBuilder orderNumber = createOrderNumber();
        orderNumber.append("id_").append(order.getId());
        order.setOrderNumber(orderNumber.toString());
        return order;
    }

    private StringBuilder createOrderNumber() {
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final char[] ALPHANUMERIC = (letters + letters.toLowerCase() + "0123456789").toCharArray();
        final int length = 12;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            builder.append(ALPHANUMERIC[(int) (Math.random() * ALPHANUMERIC.length)]);
        }
        return builder;


    }

    public void expire() {
        if (LocalDate.now().isAfter(validThrough)) {
            for (Orderline orderline : orderlines) {
                orderline.getProduct().setAvailable(true);
            }
        }
    }
}
