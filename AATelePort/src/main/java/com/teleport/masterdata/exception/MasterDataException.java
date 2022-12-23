package com.teleport.masterdata.exception;

import com.teleport.masterdata.utils.ErrorResponse;

public class MasterDataException extends RuntimeException {
  ErrorResponse errorResponse;

  public MasterDataException(String message) {
    super(message);
  }

  public MasterDataException(String message, Throwable cause) {
    super(message);
  }

  public MasterDataException(ErrorResponse errorResponse) {
    this.errorResponse = errorResponse;
  }

  public ErrorResponse getErrorResponse() {
    return errorResponse;
  }
}
