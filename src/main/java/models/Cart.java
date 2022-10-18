package models;

import conn.Connections;

import javax.persistence.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;


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

    public Time getValidThrough() {
        return validThrough;
    }

    public void setValidThrough(Time validThrough) {
        this.validThrough = validThrough;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public List<Orderline> getOrderlines() {
        return orderlines;
    }

    public void addOrderline(Orderline orderline) {
        orderlines.add(orderline);
    }

    public void setOrderlines(List<Orderline> orderlines) {
        this.orderlines = orderlines;
    }

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private Time validThrough;
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @OneToMany(mappedBy = "cart")
    private List<Orderline> orderlines;

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

    public Order makeReserved(PickupTime pickupTime) throws SQLException {
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderlines(orderlines);
        order.setStore(store);
        order.setPaymentType(paymentType);
        Connection connection = Connections.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO order_table (customer_id, store_id, payment_type, order_date, pickup_time, order_number, paid,total,status) VALUES (?, ?, ?,  ?, ?, ?, ?, ?, ?)");
        preparedStatement.setInt(1, customer.getId());
        preparedStatement.setInt(2, store.getId());
        preparedStatement.setString(3, paymentType.toString());
        preparedStatement.setDate(4, java.sql.Date.valueOf(LocalDateTime.now().toLocalDate()));
        preparedStatement.setInt(5, pickupTime.getId());
        preparedStatement.setString(6, order.getOrderNumber());
        preparedStatement.setBoolean(7, false);
        preparedStatement.setDouble(8, order.getTotal());
        preparedStatement.setString(9, "SORTING");
        preparedStatement.executeUpdate();

        return order;
    }


    public void expire() {
        if (validThrough.after(Time.valueOf(String.valueOf(LocalDateTime.now())))) {
            for (Orderline orderline : orderlines) {
                orderline.getProduct().setAvailable(true);
            }
        }
    }
}
