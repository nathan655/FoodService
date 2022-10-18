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

@Path("/Product")
public class ProductRestController extends Food{


    @POST
    @Path("/add/Product")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addProduct(Product product) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO product (name, description, category, new_price, old_price, available, quantity,store_id) VALUES (?, ?, ?, ?, ?, ?, ?,1)");
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory().toString());
            stmt.setDouble(4, product.getNewPrice());
            stmt.setDouble(5, product.getOldPrice());
            stmt.setBoolean(6, product.isAvailable());
            stmt.setInt(7, product.getQuantity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Product added successfully";
    }

}
