package org.knowm.xchange.coinjar;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.knowm.xchange.coinjar.dto.CoinjarOrder;
import org.knowm.xchange.coinjar.dto.data.CoinjarOrderBook;
import org.knowm.xchange.coinjar.dto.data.CoinjarTicker;
import org.knowm.xchange.coinjar.service.CoinjarException;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CoinjarAdapters {

  private static final Logger logger = LoggerFactory.getLogger(CoinjarAdapters.class);

  private CoinjarAdapters() {}

  public static String currencyPairToProduct(CurrencyPair pair) {
    return pair.base.getCurrencyCode() + pair.counter.getCurrencyCode();
  }

  public static CurrencyPair productToCurrencyPair(String product) {
    return new CurrencyPair(
        Currency.getInstance(product.substring(0, 3)),
        Currency.getInstance(product.substring(3, 6)));
  }

  public static String orderTypeToBuySell(Order.OrderType orderType) {
    if (orderType == Order.OrderType.BID) {
      return "buy";
    } else if (orderType == Order.OrderType.ASK) {
      return "sell";
    } else
      throw new IllegalArgumentException(
          "Unable to convert orderType " + orderType + " to buy/sell");
  }

  public static Order.OrderType buySellToOrderType(String buySell) {
    if (buySell.equals("buy")) {
      return Order.OrderType.BID;
    } else if (buySell.equals("sell")) {
      return Order.OrderType.ASK;
    } else
      throw new IllegalArgumentException(
          "Unable to convert orderType " + buySell + " to Order.OrderType");
  }

  public static Order.OrderStatus adaptStatus(String status){
    if ( status.equals("booked")){
      return Order.OrderStatus.PENDING_NEW;
    } else if ( status.equals("filled")){
      return Order.OrderStatus.FILLED;
    } else return Order.OrderStatus.UNKNOWN;
  }
  public static Ticker adaptTicker(CoinjarTicker ticker, CurrencyPair currencyPair) {
    try {
      return new Ticker.Builder()
          .currencyPair(currencyPair)
          .last(new BigDecimal(ticker.last))
          .bid(new BigDecimal(ticker.bid))
          .ask(new BigDecimal(ticker.ask))
          .timestamp(DateUtils.fromISODateString(ticker.currentTime))
          .volume(new BigDecimal(ticker.volume))
          .build();
    } catch (InvalidFormatException e) {
      throw new CoinjarException("adaptTicker cannot parse date " + ticker.currentTime);
    }
  }

  public static LimitOrder adaptOrder(CoinjarOrder coinjarOrder) {
    BigDecimal originalAmount = new BigDecimal(coinjarOrder.size);
    BigDecimal filled = new BigDecimal(coinjarOrder.filled);
    BigDecimal remainingAmount = originalAmount.subtract(filled);
    return new LimitOrder.Builder(
            buySellToOrderType(coinjarOrder.orderSide),
            productToCurrencyPair(coinjarOrder.productId))
            .id(coinjarOrder.oid.toString())
            .limitPrice(new BigDecimal(coinjarOrder.price))
            .originalAmount(originalAmount)
            .remainingAmount(remainingAmount)
            .cumulativeAmount(filled)
            .averagePrice(new BigDecimal(coinjarOrder.price))
            .timestamp(Date.from(ZonedDateTime.parse(coinjarOrder.timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()))
            .orderStatus(adaptStatus(coinjarOrder.status))
            .build();
  }

  private static List<LimitOrder> adaptOrderList(
          List<List<String>> orderList, Order.OrderType orderType, CurrencyPair pair) {
    return orderList.stream()
        .map(
            l ->
                new LimitOrder(
                    orderType,
                    new BigDecimal(l.get(1)),
                    pair,
                    null,
                    null,
                    new BigDecimal(l.get(0))))
        .collect(Collectors.toList());
  }

  public static OrderBook adaptOrderbook(CoinjarOrderBook orderBook, CurrencyPair currencyPair) {

    return new OrderBook(
        null,
        adaptOrderList(orderBook.asks, Order.OrderType.ASK, currencyPair),
        adaptOrderList(orderBook.bids, Order.OrderType.BID, currencyPair));
  }

  //  public static List<FundingRecord> adaptFundingRecords(TransferStatus ts) {
  //    List<FundingRecord> result = new ArrayList<>();
  //    FundingRecord fundingRecord =
  //        new FundingRecord(
  //            ts.getDest().replaceAll("bitcoin:", ""),
  //            Date.from(Instant.ofEpochSecond(ts.getCreatedAt())),
  //            Currency.getInstance(ts.getSourceCurrency()),
  //            ts.getSourceAmount(),
  //            ts.getId(),
  //            null,
  //            FundingRecord.Type.WITHDRAWAL,
  //            adaptStatusToFundingRecordStatus(ts.getStatus()),
  //            null,
  //            ts.getFeeEquivalencies().get(ts.getSourceCurrency()),
  //            ts.getDesc());
  //    if (fundingRecord.getStatus() == null) {
  //      logger.warn("Unable to get FundingRecord status for Wyre transfer status {}",
  // ts.getStatus());
  //    }
  //    result.add(fundingRecord);
  //    return result;
  //  }
  //
  //  public static List<FundingRecord> adaptFundingRecords(TransferHistoryResponse r) {
  //    List<FundingRecord> result = new ArrayList<>();
  //    for (TransferStatus ts : r.getData()) {
  //      result.addAll(adaptFundingRecords(ts));
  //    }
  //    return result;
  //  }
  //
  //  public static Order adaptTransferStatus(TransferStatus transferStatus) {
  //    CurrencyPair pair =
  //        new CurrencyPair(transferStatus.getSourceCurrency(), transferStatus.getDestCurrency());
  //    return new LimitOrder(
  //        null,
  //        transferStatus.getSourceAmount(),
  //        pair,
  //        transferStatus.getId(),
  //        new Date(transferStatus.getCreatedAt()),
  //        transferStatus.getExchangeRate(),
  //        transferStatus.getExchangeRate(),
  //        transferStatus.getDestAmount(),
  //        transferStatus.getTotalFees(),
  //        adaptOrderStatus(adaptStatusToFundingRecordStatus(transferStatus.getStatus())));
  //  }
}
