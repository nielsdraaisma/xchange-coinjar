package org.knowm.xchange.coinjar.service;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.coinjar.CoinjarData;
import org.knowm.xchange.coinjar.CoinjarTrading;
import org.knowm.xchange.service.BaseExchangeService;
import org.knowm.xchange.service.BaseService;
import si.mazi.rescu.RestProxyFactory;

import java.util.Optional;

public class CoinjarBaseService extends BaseExchangeService implements BaseService {

  public static final String LIVE_URL = "https://exchange.coinjar.com/";
  public static final String SANDBOX_URL = "https://exchange.coinjar-sandbox.com/";

  protected final CoinjarData coinjarData;
  protected final CoinjarTrading coinjarTrading;

  protected final String authorizationHeader;

  public CoinjarBaseService(Exchange exchange) {
    super(exchange);
    String domain;
    // Take url from either sslUri or host, fall back to live if not set
    String url =
        Optional.ofNullable(exchange.getExchangeSpecification().getSslUri())
            .or(() -> Optional.ofNullable(exchange.getExchangeSpecification().getHost()))
            .orElse(LIVE_URL);

    this.authorizationHeader =
        "Token token=\"" + exchange.getExchangeSpecification().getApiKey() + "\"";

    if (!url.equals(LIVE_URL) && !url.equals(SANDBOX_URL)) {
      throw new IllegalArgumentException(
          "Coinbase configuration url should be either "
              + LIVE_URL
              + " or "
              + SANDBOX_URL
              + " - got "
              + url);
    } else if (url.equals(SANDBOX_URL)) {
      domain = "coinjar-sandbox";
    } else {
      domain = "coinjar";
    }
    this.coinjarData =
        RestProxyFactory.createProxy(
            CoinjarData.class, "https://data.exchange." + domain + ".com/", getClientConfig());

    this.coinjarTrading =
        RestProxyFactory.createProxy(
            CoinjarTrading.class, "https://api.exchange." + domain + ".com/", getClientConfig());
  }

  //  protected ExchangeException handleError(CoinjarException exception) {
  //
  //    if (exception.getMessage().contains("Insufficient")) {
  //      return new FundsExceededException(exception);
  //    } else if (exception.getMessage().contains("Rate limit exceeded")) {
  //      return new RateLimitExceededException(exception);
  //    } else if (exception.getMessage().contains("Internal server error")) {
  //      return new InternalServerException(exception);
  //    } else {
  //      return new ExchangeException(exception);
  //    }
  //  }
}
