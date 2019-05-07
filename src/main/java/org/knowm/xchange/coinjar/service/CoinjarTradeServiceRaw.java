package org.knowm.xchange.coinjar.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinjar.dto.CoinjarOrder;
import org.knowm.xchange.coinjar.dto.trading.CoinjarOrderRequest;

import java.io.IOException;
import java.util.List;

class CoinjarTradeServiceRaw extends CoinjarBaseService {

  CoinjarTradeServiceRaw(Exchange exchange) {
    super(exchange);
  }

  protected CoinjarOrder placeOrder(CoinjarOrderRequest request)
      throws CoinjarException, IOException {
    return coinjarTrading.placeOrder(authorizationHeader, request);
  }

  protected CoinjarOrder getOrder(String id) throws CoinjarException, IOException {
    return coinjarTrading.getOrder(authorizationHeader, id);
  }

  protected List<CoinjarOrder> getAllOrders() throws CoinjarException, IOException {
    return coinjarTrading.getAllOrders(authorizationHeader);
  }
}
