package org.knowm.xchange.coinjar;

import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;

import static org.assertj.core.api.Assertions.assertThat;

public class CoinjarAdaptersTest {

  @Test
  public void orderTypeToBuySell() {
    assertThat(CoinjarAdapters.buySellToOrderType("buy")).isEqualTo(Order.OrderType.BID);
    assertThat(CoinjarAdapters.buySellToOrderType("sell")).isEqualTo(Order.OrderType.ASK);
  }

  @Test
  public void testProductToCurrencyPair() {
    assertThat(CoinjarAdapters.productToCurrencyPair("BTCAUD")).isEqualTo(CurrencyPair.BTC_AUD);
  }

  @Test
  public void testCurrencyPairToProduct() {
    assertThat(CoinjarAdapters.currencyPairToProduct(CurrencyPair.BTC_AUD)).isEqualTo("BTCAUD");
  }

  @Test
  public void testAdaptStatus() {
    assertThat(CoinjarAdapters.adaptStatus("filled")).isEqualTo(Order.OrderStatus.FILLED);
    assertThat(CoinjarAdapters.adaptStatus("booked")).isEqualTo(Order.OrderStatus.PENDING_NEW);
  }
}
