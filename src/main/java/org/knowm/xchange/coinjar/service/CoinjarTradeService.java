package org.knowm.xchange.coinjar.service;

import org.knowm.xchange.coinjar.CoinjarAdapters;
import org.knowm.xchange.coinjar.CoinjarErrorAdapter;
import org.knowm.xchange.coinjar.CoinjarException;
import org.knowm.xchange.coinjar.CoinjarExchange;
import org.knowm.xchange.coinjar.dto.CoinjarOrder;
import org.knowm.xchange.coinjar.dto.trading.CoinjarOrderRequest;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderByIdParams;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamPaging;
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
    try {
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
    } catch (CoinjarException e) {
      throw CoinjarErrorAdapter.adaptCoinjarException(e);
    }
  };

  @Override
  public Collection<Order> getOrder(String... orderIds) throws IOException {
    try {
      String orderId = orderIds[0]; // Lists.newArrayList(orderIds).get(0);
      CoinjarOrder coinjarOrder = getOrder(orderId);
      return Collections.singletonList(CoinjarAdapters.adaptOrderToLimitOrder(coinjarOrder));
    } catch (CoinjarException e) {
      throw CoinjarErrorAdapter.adaptCoinjarException(e);
    }
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {
    Integer page = 0;
    if (params instanceof CoinjarTradeHistoryParams) {
      page = ((CoinjarTradeHistoryParams) params).pageNumber;
    }
    try {
      List<UserTrade> trades =
          getAllOrders(page).stream()
              .map(CoinjarAdapters::adaptOrderToUserTrade)
              .collect(Collectors.toList());
      return new UserTrades(trades, UserTrades.TradeSortType.SortByID);
    } catch (CoinjarException e) {
      throw CoinjarErrorAdapter.adaptCoinjarException(e);
    }
  }

  @Override
  public TradeHistoryParams createTradeHistoryParams() {
    return new CoinjarTradeHistoryParams();
  }

  @Override
  public boolean cancelOrder(CancelOrderParams orderParams) throws IOException {
    try {
      if (orderParams instanceof CancelOrderByIdParams) {
        CoinjarOrder cancelledOrder =
            cancelOrderById(((CancelOrderByIdParams) orderParams).getOrderId());
        return cancelledOrder.status == "cancelled";
      } else {
        throw new IllegalArgumentException(
            "Unable to extract id from CancelOrderParams" + orderParams);
      }
    } catch (CoinjarException e) {
      throw CoinjarErrorAdapter.adaptCoinjarException(e);
    }
  }

  private static class CoinjarTradeHistoryParams
      implements TradeHistoryParams, TradeHistoryParamPaging {
    private Integer pageNumber;

    @Override
    public Integer getPageLength() {
      return null;
    }

    @Override
    public void setPageLength(Integer pageLength) {
      throw new NotAvailableFromExchangeException();
    }

    @Override
    public Integer getPageNumber() {
      return pageNumber;
    }

    @Override
    public void setPageNumber(Integer pageNumber) {
      this.pageNumber = pageNumber;
    }
  }
}
