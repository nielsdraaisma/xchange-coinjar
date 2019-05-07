package org.knowm.xchange.coinjar.service;

import org.knowm.xchange.coinjar.CoinjarAdapters;
import org.knowm.xchange.coinjar.CoinjarExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam;
import org.knowm.xchange.service.marketdata.params.Params;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.knowm.xchange.coinjar.CoinjarAdapters.currencyPairToProduct;

public class CoinjarMarketDataService extends CoinjarMarketDataServiceRaw
    implements MarketDataService {

  public CoinjarMarketDataService(CoinjarExchange exchange) {
    super(exchange);
  }

  @Override
  public Ticker getTicker(CurrencyPair currencyPair, Object... args) throws IOException {
    return CoinjarAdapters.adaptTicker(
        super.getTicker(currencyPairToProduct(currencyPair)), currencyPair);
  }

  @Override
  public List<Ticker> getTickers(Params params) throws IOException {
    List<Ticker> res = new ArrayList<>();
    if (params instanceof CurrencyPairsParam) {
      for (CurrencyPair cp : ((CurrencyPairsParam) params).getCurrencyPairs()) {
        res.add(getTicker(cp));
      }
      return res;
    }
    return res;
  }

  @Override
  public OrderBook getOrderBook(CurrencyPair currencyPair, Object... args) throws IOException {
    return CoinjarAdapters.adaptOrderbook(
        super.getOrderBook(currencyPairToProduct(currencyPair)), currencyPair);
  }
}