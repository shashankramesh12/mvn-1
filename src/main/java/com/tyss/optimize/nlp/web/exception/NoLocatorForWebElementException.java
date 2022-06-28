package com.tyss.optimize.nlp.web.exception;

public class NoLocatorForWebElementException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoLocatorForWebElementException(String elementName) {
		super("There is no locator specified for the element " + elementName);
	}


}
