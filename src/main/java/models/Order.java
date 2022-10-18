package models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Order {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String orderNumber;
    private boolean pickup;
    private boolean paid;

    public void setTotal(double total) {
        this.total = total;
    }

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private LocalDate pickupTime, orderDate;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    private PaymentType paymentType;
    private double total;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Orderline> orderlines = new ArrayList<>();

    public boolean isPaid() {
        return paid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isPickup() {
        return pickup;
    }

    public void setPickup(boolean pickup) {
        this.pickup = pickup;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalDate pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Enumerated(EnumType.STRING)

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public double getTotal() {
        for (Orderline orderline : orderlines) {
            total += orderline.getTotal();
        }
        return total;
    }


    public List<Orderline> getOrderlines() {
        return orderlines;
    }

    public void setOrderlines(List<Orderline> orderlines) {
        this.orderlines = orderlines;
    }

    private boolean isPicked() {
        return pickup;
    }
}
