package org.knowm.xchange.coinjar.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinjar.CoinjarException;
import org.knowm.xchange.coinjar.dto.trading.CoinjarAccount;

import java.io.IOException;
import java.util.List;

class CoinjarAccountServiceRaw extends CoinjarBaseService {

  CoinjarAccountServiceRaw(Exchange exchange) {
    super(exchange);
  }

  List<CoinjarAccount> getAccounts() throws CoinjarException, IOException {
    return coinjarTrading.getAccounts(this.authorizationHeader);
  }
}
