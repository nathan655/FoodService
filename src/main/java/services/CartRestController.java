package services;


import com.google.zxing.WriterException;
import conn.Connections;
import models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/cartRest")
public class CartRestController extends Food {
    @GET
    @Path("test")
    public String test() throws IOException, WriterException {
        return "Prolly works"; //TODO replace this stub to something useful
    }

    @GET
    @Path("getCart/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Cart getCart(@PathParam("id") int id) {

        getCarts();
        return carts.stream().filter(cart -> cart.getId() == id).findFirst().get();
    }

    @POST
    @Path("addProduct/{id}/{customer_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Cart addProduct(@PathParam("id") int id, Product product, @PathParam("customer_id") int customer_id) throws SQLException {
        List<Orderline> orderlines = new ArrayList<>();
        getCarts();
        getStores();
        getCustomers();
        getProducts();
        Orderline orderline = new Orderline();
        orderline.setProduct(product);
        orderline.setQuantity(1);
        orderlines.add(orderline);
        connection = Connections.getConnection();
        if (carts.stream().filter(cart1 -> cart1.getId() == id).anyMatch(cart1 -> cart1.getStore().getId() == product.getStore().getId())) {
            Cart cart = carts.stream().filter(cart1 -> cart1.getId() == id).filter(cart1 -> cart1.getStore().getId() == product.getStore().getId()).findFirst().get();
            orderline.setCart(cart);
            cart.addOrderline(orderline);
            Time myTime = cart.getValidThrough();
            LocalTime localtime = myTime.toLocalTime();
            cart.setValidThrough(Time.valueOf(localtime.plusMinutes(12)));
            PreparedStatement stmt = connection.prepareStatement("UPDATE cart SET customer_id = ?, payment_type = ?, valid_through = ? WHERE id = ?");
            stmt.setInt(1, cart.getCustomer().getId());
            stmt.setString(2, cart.getPaymentType().toString());
            stmt.setTime(3, cart.getValidThrough());
            stmt.setInt(4, cart.getId());
            stmt.executeUpdate();
            PreparedStatement stmt1 = connection.prepareStatement("INSERT INTO order_line (quantity, product_id, cart_id) VALUES (?, ?, ?)");
            stmt1.setInt(1, orderline.getQuantity());
            stmt1.setInt(2, orderline.getProduct().getId());
            stmt1.setInt(3, orderline.getCart().getId());
            stmt1.executeUpdate();


            return cart;

        } else {
            Cart cart = new Cart();
            orderline.setCart(cart);
            cart.setOrderlines(orderlines);
            cart.setStore(product.getStore());
            cart.setCustomer(customers.stream().filter(customer -> customer.getId() == customer_id).findFirst().get());
            cart.setValidThrough(Time.valueOf(LocalTime.now()));
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO cart (store_id, customer_id, payment_type, valid_through) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, product.getStore().getId());
            stmt.setInt(2, customer_id);
            stmt.setString(3, cart.getPaymentType().toString());
            stmt.setTime(4, cart.getValidThrough());
            stmt.executeUpdate();
            PreparedStatement stmt1 = connection.prepareStatement("INSERT INTO order_line (quantity, product_id, cart_id) VALUES (?, ?, ?)");
            stmt1.setInt(1, orderline.getQuantity());
            stmt1.setInt(2, orderline.getProduct().getId());
            getCarts();
            List<Cart> CustomerCarts = carts.stream().filter(cart2 -> cart2.getCustomer().getId() == customer_id).toList();
            Cart cart1 = CustomerCarts.get(CustomerCarts.size() - 1);
            stmt1.setInt(3, cart1.getId());
            stmt1.executeUpdate();
            return cart;
        }
    }

    @DELETE
    @Path("deleteProduct/{id}/{product_id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteProduct(@PathParam("id") int id, @PathParam("product_id") int product_id) throws SQLException {
        connection = Connections.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM order_line WHERE cart_id = ? AND product_id = ?");
        stmt.setInt(1, id);
        stmt.setInt(2, product_id);
        stmt.executeUpdate();
        getCarts();
        return "Product deleted";
    }

    @DELETE
    @Path("deleteCart/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteCart(@PathParam("id") int id) throws SQLException {
        connection = Connections.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM cart WHERE id = ?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
        getCarts();
        return "Cart deleted";
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

    @PUT
    @Path("makeReserved/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String makeReserved(@PathParam("id") int id, PickupTime pickupTime) throws SQLException {
        connection = Connections.getConnection();
        getCart(id).makeReserved(pickupTime);
        getOrders();
        List<Order> CustomerOrders = orders.stream().filter(o -> o.getCustomer().getId() == getCart(id).getCustomer().getId()).toList();
        Order order = CustomerOrders.get(CustomerOrders.size() - 1);
        StringBuilder orderNumber = createOrderNumber();
        orderNumber.append("id_").append(order.getId());
        order.setOrderNumber(orderNumber.toString());
        PreparedStatement stmt = connection.prepareStatement("UPDATE order_table SET order_number = ? WHERE id = ?");
        stmt.setString(1, order.getOrderNumber());
        stmt.setInt(2, order.getId());
        stmt.executeUpdate();


        return "Order reserved";
    }

}
