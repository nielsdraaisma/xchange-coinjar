package org.knowm.xchange.coinjar;

import org.knowm.xchange.coinjar.dto.CoinjarOrder;
import org.knowm.xchange.coinjar.dto.trading.CoinjarAccount;
import org.knowm.xchange.coinjar.dto.trading.CoinjarOrderRequest;
import org.knowm.xchange.coinjar.dto.trading.CoinjarProduct;
import org.knowm.xchange.coinjar.service.CoinjarException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Produces({"application/json"})
@Path("/")
public interface CoinjarTrading {

  @GET
  @Path("/products")
  List<CoinjarProduct> getProducts() throws CoinjarException, IOException;

  @GET
  @Path("/accounts")
  List<CoinjarAccount> getAccounts(@HeaderParam("Authorization") String authHeader)
      throws CoinjarException, IOException;

  @POST
  @Path("/orders")
  @Consumes(MediaType.APPLICATION_JSON)
  CoinjarOrder placeOrder(
      @HeaderParam("Authorization") String authHeader, CoinjarOrderRequest request)
      throws CoinjarException, IOException;

  @GET
  @Path("/orders/all")
  List<CoinjarOrder> getAllOrders(@HeaderParam("Authorization") String authHeader)
      throws CoinjarException, IOException;

  @GET
  @Path("/orders/{id}")
  CoinjarOrder getOrder(
      @HeaderParam("Authorization") String authHeader, @PathParam("id") String id);

  @DELETE
  @Path("/orders/{id}")
  CoinjarOrder cancelOrder(
      @HeaderParam("Authorization") String authHeader, @PathParam("id") String id);
}
