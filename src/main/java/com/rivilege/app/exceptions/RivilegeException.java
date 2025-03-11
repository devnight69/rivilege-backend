package com.rivilege.app.exceptions;

/**
 * Customize exception class for Rivilege.
 *
 * @author Kousik manik
 */
public class RivilegeException extends Exception {
  private String message;

  public RivilegeException() {
    super();
  }

  public RivilegeException(String message) {
    super(message);
  }

  @Override
  public String getMessage() {
    return super.getMessage();
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

