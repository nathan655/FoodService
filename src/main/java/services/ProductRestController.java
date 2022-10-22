package services;

import conn.Connections;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/Product")
public class ProductRestController extends Food {
    @GET
    @Path("/getProduct/{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getProductForCustomer(@PathParam("uid") String id) {
        getProducts();
        getCustomers();
        List<Product> products = new ArrayList<>();

        if (customers.stream().filter(customer1 -> customer1.getUser().equals(id)).findFirst().isPresent()) {
            Customer customer = customers.stream().filter(customer1 -> customer1.getUser().equals(id)).findFirst().get();
            return products.stream().filter(product -> product.getStore().getCountry().equals(customer.getCountry())).collect(Collectors.toList());
        } else {
            return null;
        }
    }


    @POST
    @Path("/add/Product/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addProduct(Product product, @PathParam("id") int id) {
        connection = Connections.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO product (name, description, category, new_price, old_price, available, quantity,store_id,expire_date) VALUES (?, ?, ?, ?, ?, ?, ?,?,?)");
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory().toString());
            stmt.setDouble(4, product.getNewPrice());
            stmt.setDouble(5, product.getOldPrice());
            stmt.setBoolean(6, product.isAvailable());
            stmt.setInt(7, product.getQuantity());
            stmt.setInt(8, id);
            stmt.setDate(9, product.getExpireDate());
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
        return "Product added successfully";
    }

    @PUT
    @Path("/update/Product/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String updateProduct(Product product, @PathParam("id") int id) {
        connection = Connections.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE product SET name = ?, description = ?, category = ?, new_price = ?, old_price = ?, available = ?, quantity = ?,expire_date = ? WHERE id = ?");
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory().toString());
            stmt.setDouble(4, product.getNewPrice());
            stmt.setDouble(5, product.getOldPrice());
            stmt.setBoolean(6, product.isAvailable());
            stmt.setInt(7, product.getQuantity());
            stmt.setDate(8, product.getExpireDate());
            stmt.setInt(9, id);
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
        return "Product updated successfully";
    }

    @DELETE
    @Path("/delete/Product/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteProduct(@PathParam("id") int id) {
        connection = Connections.getConnection();
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM product WHERE id = ?");
            stmt.setInt(1, id);
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
        return "Product deleted successfully";
    }

    @GET
    @Path("/get/Product/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product getProduct(@PathParam("id") int id) {
        connection = Connections.getConnection();
        Product product = new Product();
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM product WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setCategory(FoodCategory.valueOf(rs.getString("category")));
                product.setNewPrice(rs.getDouble("new_price"));
                product.setOldPrice(rs.getDouble("old_price"));
                product.setAvailable(rs.getBoolean("available"));
                product.setQuantity(rs.getInt("quantity"));
                product.setExpireDate(rs.getDate("expire_date"));
                getStores();
                product.setStore(stores.stream().filter(s -> {
                    try {
                        return s.getId() == rs.getInt("store_id");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).findFirst().get());
            }
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
        return product;
    }

}
