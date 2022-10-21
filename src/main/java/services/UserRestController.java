package services;

import conn.Connections;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/User")
public class UserRestController extends Food {
	
	@GET
	@Path("/getUser/{uid}")
	
    @Produces(MediaType.APPLICATION_JSON)
	
	public User getUserById(@PathParam("uid") String id) {
		getAllUsers();
		
		Customer user = (Customer) users.stream().filter(customer -> customer.getUser() == id).findFirst().get();
		return user;
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
