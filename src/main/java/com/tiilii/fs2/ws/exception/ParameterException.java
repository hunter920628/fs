package com.tiilii.fs2.ws.exception;

public class ParameterException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6627941407544324122L;
	private String errorCode;
	
	public ParameterException(){}

	public ParameterException(String message) {
		super(message);
	}
	

	public ParameterException(Throwable throwable) {
		super(throwable);
	}

	public ParameterException(String errorCode, String message) {
		super(message);
		this.errorCode=errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
