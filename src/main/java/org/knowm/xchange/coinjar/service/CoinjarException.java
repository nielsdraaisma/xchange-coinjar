package org.knowm.xchange.coinjar.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import si.mazi.rescu.HttpStatusExceptionSupport;

public class CoinjarException extends HttpStatusExceptionSupport {

  public CoinjarException(@JsonProperty("message") String reason) {
    super(reason);
  }
}
