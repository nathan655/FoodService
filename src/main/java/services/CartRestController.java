package services;


import com.google.zxing.WriterException;
import models.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/cartRest")
public class CartRestController extends Food {
    @GET
    @Path("test")
    public String test() throws IOException, WriterException {
        Product product = new Product();
        Orderline orderline = new Orderline();
        Customer customer = new Customer();
        Cart cart = new Cart();
        cart.makeReserved();
        return "Prolly works"; //TODO replace this stub to something useful
    }

    @GET
    @Path("getCart/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Cart getCart(int id) {
        getCarts();
        return carts.stream().filter(cart -> cart.getId() == id).findFirst().get();
    }

}
