package services;

import conn.Connections;
import models.*;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/")
public class Food {
    Connection connection = null;
    List<Store> stores = null;
    List<Product> products = new ArrayList<>();
    List<Customer> customers = new ArrayList<>();
    List<Cart> carts = new ArrayList<>();
    List<Order> orders = new ArrayList<>();
    List<Orderline> orderlines = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<PickupTime> pickupTimes = new ArrayList<>();

    List<PhoneNumber> phoneNumbers = new ArrayList<>();


    @PostConstruct
    public void init() {
        connection = Connections.getConnection();
    }

    //Get all Users from database
    @GET
    @Path("/getUsers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getAllUsers() {
        getStores();
        getCustomers();
        users.addAll(customers);
        users.addAll(stores);
        return users;
    }

    // Get all Stores from the database
    @GET
    @Path("/getStores")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Store> getStores() {
        stores = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM store;");
            ResultSet rs2 = ps.executeQuery();
            while (rs2.next()) {
                Store store = new Store();
                store.setId(rs2.getInt("id"));
                store.setEmail(rs2.getString("email"));
                store.setPassword(rs2.getString("password"));
                store.setAddress(rs2.getString("address"));
                store.setCity(rs2.getString("city"));
                store.setState(rs2.getString("state"));
                store.setZipCode(rs2.getString("zip_code"));
                store.setCountry(rs2.getString("country"));
                store.setCreatedDate(rs2.getDate("created_date").toLocalDate());
                store.setUpdatedDate(rs2.getDate("updated_date").toLocalDate());
                store.setUserName(rs2.getString("user_name"));
//                getPickupTimes();
                store.setPickupTimes(pickupTimes.stream().filter(pickupTime -> {
                    try {
                        return pickupTime.getStore().getId() == rs2.getInt("id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()));

//                getPhoneNumbers();
                store.setPhoneNumbers(phoneNumbers.stream().filter(p -> {
                    try {
                        return p.getUser().getId() == rs2.getInt("id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()));
                stores.add(store);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stores;
    }


    @GET
    @Path("/intro")
    @Produces(MediaType.TEXT_PLAIN)
    public String serviceName() {
        return "This is a Food service";
    }


    // Get all Products from the database

    @GET
    @Path("/getProducts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getProducts() {
        connection = Connections.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM product");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setOldPrice(rs.getDouble("old_price"));
                product.setNewPrice(rs.getDouble("new_price"));
                product.setCategory(FoodCategory.valueOf(rs.getString("category")));
                product.setQuantity(rs.getInt("quantity"));
                product.setAvailable(rs.getBoolean("available"));
                product.setDescription(rs.getString("description"));
                product.setExpireDate(rs.getDate("expire_date").toLocalDate());
                getStores();
                product.setStore(stores.stream().filter(s -> {
                    try {
                        return s.getId() == rs.getInt("store_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().get());
                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    // Get all Customers from the database
    @GET
    @Path("/getCustomers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> getCustomers() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM customer");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPassword(rs.getString("password"));
                customer.setAddress(rs.getString("address"));
                customer.setCity(rs.getString("city"));
                customer.setState(rs.getString("state"));
                customer.setZipCode(rs.getString("zip_code"));
                customer.setCountry(rs.getString("country"));
                customer.setCreatedDate(rs.getDate("created_date").toLocalDate());
                customer.setUpdatedDate(rs.getDate("updated_date").toLocalDate());
                customer.setUserName(rs.getString("user_name"));

//                getPhoneNumbers();
                customer.setPhoneNumbers(phoneNumbers.stream().filter(p -> {
                    try {
                        return p.getUser().getId() == rs.getInt("id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()));
                customers.add(customer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }


    // Get all Orders from the database
    @GET
    @Path("/getOrders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Order> getOrders() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM order;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setOrderDate(LocalDate.now());
                order.setTotal(rs.getDouble("total"));
                getCustomers();
                order.setCustomer(getCustomers().stream().filter(c -> {
                    try {
                        return c.getId() == rs.getInt("customer_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().get());
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    // Get all Orderlines from the database
    @GET
    @Path("/getOrderlines")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Orderline> getOrderlines() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM orderline;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Orderline orderline = new Orderline();
                orderline.setId(rs.getLong("id"));
                orderline.setQuantity(rs.getInt("quantity"));
                getOrders();
                orderline.setOrder(getOrders().stream().filter(o -> {
                    try {
                        return o.getId() == rs.getLong("order_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().get());
                getProducts();
                orderline.setProduct(getProducts().stream().filter(p -> {
                    try {
                        return p.getId() == rs.getInt("product_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().get());
                orderlines.add(orderline);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orderlines;
    }

    //Get all PhoneNumbers From Database
    @GET
    @Path("/getPhoneNumbers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PhoneNumber> getPhoneNumbers() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM phone_number;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setId(rs.getLong("id"));
                phoneNumber.setPhoneNumber(rs.getString("number"));
                phoneNumber.setType(rs.getString("type"));
                phoneNumber.setCountryCode(CountryCode.getByCode(rs.getString("country_code")));
                getCustomers();
                phoneNumber.setUser(getAllUsers().stream().filter(c -> {
                    try {
                        return c.getId() == rs.getInt("user_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().get());
                phoneNumbers.add(phoneNumber);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return phoneNumbers;
    }

    // Get all Carts from the database
    @GET
    @Path("/getCarts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cart> getCarts() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM cart;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Cart cart = new Cart();
                cart.setId(rs.getInt("id"));
                getStores();
                cart.setStore(stores.stream().filter(c -> {
                    try {
                        return c.getId() == rs.getInt("customer_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().get());
                getCustomers();
                cart.setCustomer(getCustomers().stream().filter(c -> {
                    try {
                        return c.getId() == rs.getInt("customer_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().get());
                carts.add(cart);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return carts;
    }


}
