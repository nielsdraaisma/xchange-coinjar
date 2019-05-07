package org.knowm.xchange.coinjar.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinjar.dto.data.CoinjarOrderBook;
import org.knowm.xchange.coinjar.dto.data.CoinjarTicker;

import java.io.IOException;

class CoinjarMarketDataServiceRaw extends CoinjarBaseService {

  CoinjarMarketDataServiceRaw(Exchange exchange) {
    super(exchange);
  }

  CoinjarTicker getTicker(String product) throws CoinjarException, IOException {
    return coinjarData.getTicker(product);
  }

  CoinjarOrderBook getOrderBook(String product) throws CoinjarException, IOException {
    return coinjarData.getOrderBook(product, 2);
  }
}
