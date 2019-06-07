package org.knowm.xchange.coinjar;

import com.fasterxml.jackson.annotation.JsonProperty;
import si.mazi.rescu.HttpStatusExceptionSupport;

import java.util.List;

public class CoinjarException extends HttpStatusExceptionSupport {

  public final String errorType;
  public final List<String> errorMessages;

  public CoinjarException(
      @JsonProperty("error_type") String errorType,
      @JsonProperty("error_messages") List<String> errorMessages) {
    super(errorType + " : " + errorMessages.stream().findFirst().orElse(""));
    this.errorType = errorType;
    this.errorMessages = errorMessages;
  }

  public String getFirstMessage() {
    return this.errorMessages.stream().findFirst().orElse(null);
  }
}
