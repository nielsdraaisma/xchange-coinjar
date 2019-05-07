package org.knowm.xchange.coinjar.service;

import org.knowm.xchange.coinjar.CoinjarAdapters;
import org.knowm.xchange.coinjar.CoinjarExchange;
import org.knowm.xchange.coinjar.dto.CoinjarOrder;
import org.knowm.xchange.coinjar.dto.trading.CoinjarOrderRequest;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.knowm.xchange.coinjar.CoinjarAdapters.currencyPairToProduct;
import static org.knowm.xchange.coinjar.CoinjarAdapters.orderTypeToBuySell;

public class CoinjarTradeService extends CoinjarTradeServiceRaw implements TradeService {

  public CoinjarTradeService(CoinjarExchange exchange) {
    super(exchange);
  }

  @Override
  public String placeLimitOrder(LimitOrder limitOrder) throws IOException {
    CoinjarOrderRequest request =
        new CoinjarOrderRequest(
            currencyPairToProduct(limitOrder.getCurrencyPair()),
            "LMT",
            orderTypeToBuySell(limitOrder.getType()),
            limitOrder.getLimitPrice().stripTrailingZeros().toPlainString(),
            limitOrder.getOriginalAmount().stripTrailingZeros().toPlainString(),
            "GTC");
    CoinjarOrder coinjarOrder = placeOrder(request);
    return coinjarOrder.oid.toString();
  };

  @Override
  public Collection<Order> getOrder(String... orderIds) throws IOException {
    String orderId = orderIds[0]; // Lists.newArrayList(orderIds).get(0);
    CoinjarOrder coinjarOrder = getOrder(orderId);
    return Collections.singletonList(CoinjarAdapters.adaptOrderToLimitOrder(coinjarOrder));
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {
    List<UserTrade> trades =
        getAllOrders().stream()
            .map(CoinjarAdapters::adaptOrderToUserTrade)
            .collect(Collectors.toList());
    return new UserTrades(trades, UserTrades.TradeSortType.SortByID);
  }

  @Override
  public TradeHistoryParams createTradeHistoryParams() {
    return new CoinjarTradeHistoryParams();
  }

  public static class CoinjarTradeHistoryParams implements TradeHistoryParams {}
}
