package services;

import conn.Connections;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/User")
public class UserRestController extends Food {

//	@GET
//	@Path("/getUser/{uid}")
//
//    @Produces(MediaType.APPLICATION_JSON)
//
//	public User getUserById(@PathParam("uid") String id) {
//		getAllUsers();
//
//		Customer user = (Customer) users.stream().filter(customer -> Objects.equals(customer.getUser(), id)).findFirst().get();
//		return user;
//	}

    @GET
    @Path("changeToStore/{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Store changeToStore(@PathParam("uid") String uid) {
        getCustomers();
        if (customers.stream().anyMatch(customer1 -> customer1.getUser().equals(uid))) {
            Customer customer = customers.stream().filter(customer1 -> customer1.getUser().equals(uid)).findFirst().get();
            Store store = new Store();
            store.setCountry(customer.getCountry());
            store.setCity(customer.getCity());
            store.setUserName(customer.getUserName());
            store.setPassword(customer.getPassword());
            store.setAddress(customer.getAddress());
            store.setPhone(customer.getPhone());
            store.setCreatedDate(LocalDate.now());
            store.setPhone(customer.getPhone());
            store.setZipCode(customer.getZipCode());
            store.setUser(customer.getUser());
            store.setEmail(customer.getEmail());
            store.setState(customer.getState());
            connection = Connections.getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO store (country, city, user_name, password, address, phone, created_date, zip_code, user, email, state) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setString(1, store.getCountry());
                statement.setString(2, store.getCity());
                statement.setString(3, store.getUserName());
                statement.setString(4, store.getPassword());
                statement.setString(5, store.getAddress());
                statement.setString(6, store.getPhone());
                statement.setDate(7, Date.valueOf(store.getCreatedDate()));
                statement.setString(8, store.getZipCode());
                statement.setString(9, store.getUser());
                statement.setString(10, store.getEmail());
                statement.setString(11, store.getState());
                statement.executeUpdate();
                PreparedStatement statement1 = connection.prepareStatement("DELETE FROM customer WHERE user = ?");
                statement1.setString(1, uid);
                statement1.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return store;
        } else {
            return null;
        }


    }

    @Path("/getUser/{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getUserById(@PathParam("uid") String id) {
        getCustomers();
        if (customers.stream().anyMatch(customer -> Objects.equals(customer.getUser(), id))) {
            return customers.stream().filter(customer -> Objects.equals(customer.getUser(), id)).findFirst().get();
        } else {
            Customer customer = new Customer();
            customer.setUser("There is no user with this id");
            customer.setCountry("Finland");
            return customer;
        }
    }

    @POST
    @Path("/add/User")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addUser(Customer user) {
        connection = Connections.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO customer (email, password, country, city, user ) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getCountry());
            stmt.setString(4, user.getCity());
            stmt.setString(5, user.getUser());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "User added successfully";
    }
}
