package org.skrymer.qrbuilder.exception;

public class CouldNotCreateQRCodeException extends RuntimeException {

  public CouldNotCreateQRCodeException(String message, Throwable cause){
    super(message, cause);
  }
}
