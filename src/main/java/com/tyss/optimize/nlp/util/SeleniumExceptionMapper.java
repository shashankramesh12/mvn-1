package com.tyss.optimize.nlp.util;

import com.tyss.optimize.data.models.dto.results.ErrorInfo;

import java.util.*;

public class SeleniumExceptionMapper {
    static Map  seleniumExceptionMap = null;
    public static ErrorInfo getErrorInfo(String exceptionSimpleName,StackTraceElement[] stackTrace){
        if(seleniumExceptionMap==null ||(seleniumExceptionMap!=null && seleniumExceptionMap.isEmpty()) ){
            init();
        }
        ErrorInfo errorInfo= (ErrorInfo) seleniumExceptionMap.get(exceptionSimpleName);
        if(errorInfo==null){
            errorInfo=new ErrorInfo();
            errorInfo.setName(exceptionSimpleName);
            errorInfo.setCause(Arrays.toString(stackTrace));
            List suggestions=new ArrayList<>();
            suggestions.add("No Suggestions Available");
            errorInfo.setSuggestions(suggestions);
        }
        return errorInfo;
    }
    public static void  init(){
        seleniumExceptionMap=new HashMap();
        setElementNotVisibleException();
        setElementNotSelectableException();
        setNoSuchElementException();
        setNoSuchFrameException();
        setNoSuchWindowException();
        setNoAlertPresentException();
        setStaleElementReferenceException();
        setSessionNotFoundException();
        setWebDriverException();
        setTimeoutException();
        setConnectionClosedException();
        setElementClickInterceptedException();
        setElementNotInteractableException();
        setErrorInResponseException();
        setUnknownServerException();
        setImeActivationFailedException();
        setImeNotAvailableException();
        setInsecureCertificateException();
        setInvalidArgumentException();
        setInvalidCookieDomainException();
        setInvalidCoordinatesException();
        setInvalidElementStateException();
        setInvalidSessionIdException();
        setInvalidSwitchToTargetException();
        setJavascriptException();
        setJsonException();
        setNoSuchAttributeException();
        setMoveTargetOutOfBoundsException();
        setNoSuchContextException();
        setNoSuchCookieException();
        setNotFoundException();
        setRemoteDriverServerException();
        setScreenshotException();
        setSessionNotCreatedException();
        setUnableToSetCookieException();
        setUnexpectedTagNameException();
        setUnhandledAlertException();
        setUnexpectedAlertPresentException();
        setUnknownMethodException();
        setUnreachableBrowserException();
        setUnsupportedCommandException();
    }


    private static void setElementNotVisibleException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("ElementNotVisibleException");
        errorInfo.setCause("This element is hidden in page");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Wait until the element is visible");
        suggestions.add("Perform an action to make the element visible");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("ElementNotVisibleException",errorInfo);
    }
    private static void setElementNotSelectableException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("ElementNotSelectableException");
        errorInfo.setCause("This element is present in web page, but not able to select");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Wait until the element is visible or enable");
        suggestions.add("Perform an action to make the element visible or enable");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("ElementNotSelectableException",errorInfo);
    }
    private static void setNoSuchElementException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("NoSuchElementException");
        errorInfo.setCause("Searched with all given locators, but still element is not found");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Element Locator value might be incorrect");
        suggestions.add("Element you are searching might not exist on page");
        suggestions.add("Wait until the element is present on page");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("NoSuchElementException",errorInfo);
    }
    private static void setNoSuchFrameException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("NoSuchFrameException");
        errorInfo.setCause("This frame is not present in page");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("The Name or ID or Locator value of the frame might be incorrect");
        suggestions.add("The frame you are searching might not exist on page");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("NoSuchFrameException",errorInfo);
    }
    private static void setNoAlertPresentException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("NoAlertPresentException");
        errorInfo.setCause("There was no alert box present on page");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Wait until alert is present on page");
        suggestions.add("Perform an action to make the alert visible");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("NoAlertPresentException",errorInfo);
    }
    private static void setNoSuchWindowException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("NoSuchWindowException");
        errorInfo.setCause("There was no such window present");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Wait until window is present");
        suggestions.add("Perform an action to make the new window visible");
        suggestions.add("The window you are searching / switching might not exist");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("NoSuchWindowException",errorInfo);
    }
    private static void setStaleElementReferenceException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("StaleElementReferenceException");
        errorInfo.setCause("The reference of the element is changed");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("The element might have refreshed");
        suggestions.add("The reference of the element is changed due to page refresh before performing an action");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("StaleElementReferenceException",errorInfo);
    }
    private static void setSessionNotFoundException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("SessionNotFoundException");
        errorInfo.setCause("The browser session is not found");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("The browser might have closed");
        suggestions.add("You might be performing an action after the browser is closed");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("SessionNotFoundException",errorInfo);
    }
    private static void setTimeoutException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("TimeoutException");
        errorInfo.setCause("It is taking too long to perfoam an action");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Increase the wait time");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("TimeoutException",errorInfo);
    }
    private static void setWebDriverException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("WebDriverException");
        errorInfo.setCause("The Browser or BrowserDriverServer.exe is not compatible with current web driver client library");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Use latest versions of browser or BrowserDriverServer.exe");
        suggestions.add("Use the latest web driver with latest browser and server");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("WebDriverException",errorInfo);
    }
    private static void setConnectionClosedException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("ConnectionClosedException");
        errorInfo.setCause("The connection to the SafariDriver is lost");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("The browser might have closed");
        suggestions.add("You might be performing an action after the browser is closed");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("ConnectionClosedException",errorInfo);
    }
    private static void setElementClickInterceptedException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("ElementClickInterceptedException");
        errorInfo.setCause("Element is not accepting click event since element is not loaded completely on web page");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Try using custom wait NLP that is \"Wait for element to be present in webpage\"");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("ElementClickInterceptedException",errorInfo);
    }
    private static void setElementNotInteractableException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("ElementNotInteractableException");
        errorInfo.setCause("Element is not accepting any events since element is not loaded completely or the element is disabled or element is hidden on web page");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Perform an action to enable or to make element visible");
        suggestions.add("Wait until element is enable or visible");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("ElementNotInteractableException",errorInfo);
    }
    private static void setErrorInResponseException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("ErrorInResponseException");
        errorInfo.setCause("This happens while interacting with the Firefox extension or the remote driver server.");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add(" Make sure the BrowserServerDriver.exe is running properly");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("ErrorInResponseException",errorInfo);
    }
    private static void setUnknownServerException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("UnknownServerException");
        errorInfo.setCause("Exception is used as a placeholder in case if the server returns an error without a stack trace.");
        /*List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("UnknownServerException",errorInfo);
    }
    private static void setImeActivationFailedException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("ImeActivationFailedException");
        errorInfo.setCause("This expectation will occur when IME engine activation has failed.");
        /*List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("ImeActivationFailedException",errorInfo);
    }
    private static void setImeNotAvailableException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("ImeNotAvailableException");
        errorInfo.setCause("It takes place when IME support is unavailable.");
       /* List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("ImeNotAvailableException",errorInfo);
    }
    private static void setInsecureCertificateException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("InsecureCertificateException");
        errorInfo.setCause("Navigation made the user agent to hit a certificate warning. This can cause by an invalid or expired TLS certificate.");
       /* List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("InsecureCertificateException",errorInfo);
    }
    private static void setInvalidArgumentException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("InvalidArgumentException");
        errorInfo.setCause("An argument does not belong to the expected type");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Make sure that the argument type of method is valid");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("InvalidArgumentException",errorInfo);
    }
    private static void setInvalidCookieDomainException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("InvalidCookieDomainException");
        errorInfo.setCause("Trying to add a cookie under a different domain instead of current URL");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Trying to add a cookie under same domain as in current URL");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("InvalidCookieDomainException",errorInfo);
    }
    private static void setInvalidCoordinatesException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("InvalidCoordinatesException");
        errorInfo.setCause("The specified coordinates are accedding the maximum browser window coordinates");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Specify the coordinates within the maximum browser window coordinates");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("InvalidCoordinatesException",errorInfo);
    }
    private static void setInvalidElementStateException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("InvalidElementStateException");
        errorInfo.setCause("Element is in a state that means actions cannot be performed with it");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("An element is being obscured by another when clicking");
        suggestions.add("Element is not being visible on the web page");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("InvalidElementStateException",errorInfo);
    }
    private static void setInvalidSessionIdException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("InvalidSessionIdException");
        errorInfo.setCause("The browser window session ID is not included in the list of active sessions");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Check the given session ID is included in the list of active sessions. ");
        suggestions.add(" It means the session does not exist or is inactive either.");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("InvalidSessionIdException",errorInfo);
    }
    private static void setInvalidSwitchToTargetException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("InvalidSwitchToTargetException");
        errorInfo.setCause("The frame or window target to be switched does not exist.");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Check the frame or window is exists");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("InvalidSwitchToTargetException",errorInfo);
    }
    private static void setJavascriptException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("JavascriptException");
        errorInfo.setCause("The specified javascript is invalid");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Make sure the javascript is valid");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("JavascriptException",errorInfo);
    }
    private static void setJsonException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("JsonException");
        errorInfo.setCause("JSON Exception");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Trying to get the session when the session is not created.");
        suggestions.add("JSON wire protocol not able to create a JSON request for a specified action");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("JsonException",errorInfo);
    }
    private static void setNoSuchAttributeException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("NoSuchAttributeException");
        errorInfo.setCause("The attribute of an element could not be found");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("The specified attribute of an element is incorrect or may not present");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("NoSuchAttributeException",errorInfo);
    }
    private static void setMoveTargetOutOfBoundsException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("MoveTargetOutOfBoundsException");
        errorInfo.setCause("The target provided to the actions move() method is invalid");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Check the target provided to the Actions move() method is outside of the size of the window");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("MoveTargetOutOfBoundsException",errorInfo);
    }
    private static void setNoSuchContextException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("NoSuchContextException");
        errorInfo.setCause("Appium driver not able to switch from native app to mobile view");
        /*List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("NoSuchContextException",errorInfo);
    }
    private static void setNoSuchCookieException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("NoSuchCookieException");
        errorInfo.setCause("The specified cookie is not matching with the cookie in current browser session");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Check the specified cookie is valid");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("NoSuchCookieException",errorInfo);
    }
    private static void setNotFoundException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("NotFoundException");
        errorInfo.setCause("Searched with all the locators, but still element is not found");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Element Locator might be incorrect");
        suggestions.add("Element you are searching might not exist on web page");
        suggestions.add("Wait until the element is present on web page");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("NotFoundException",errorInfo);
    }
    private static void setRemoteDriverServerException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("RemoteDriverServerException");
        errorInfo.setCause("The server is not responding because of the problem that the capabilities described are not proper.");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add(" Check the desired capabilities of the device are correct ");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("RemoteDriverServerException",errorInfo);
    }
    private static void setScreenshotException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("ScreenshotException");
        errorInfo.setCause("It is not possible to capture a screen.");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("Make sure the driver session ID is exists");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("ScreenshotException",errorInfo);
    }
    private static void setSessionNotCreatedException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("SessionNotCreatedException");
        errorInfo.setCause("A new session could not be successfully created");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("May be the browser is not compatible with the current version of web driver");
        suggestions.add("The browser and BrowserServerDriver.exe are not compatible");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("SessionNotCreatedException",errorInfo);
    }
    private static void setUnableToSetCookieException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("UnableToSetCookieException");
        errorInfo.setCause("The driver is unable to set a cookie");
       /* List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("UnableToSetCookieException",errorInfo);
    }
    private static void setUnexpectedTagNameException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("UnexpectedTagNameException");
        errorInfo.setCause("Happens if a support class did not get a web element as expected.");
        /*List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("UnexpectedTagNameException",errorInfo);
    }
    private static void setUnhandledAlertException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("UnhandledAlertException");
        errorInfo.setCause("Not able to perform an action on Alert box");
        List<String> suggestions = new ArrayList<String>();
        suggestions.add("This expectation occurs when there is an alert, but WebDriver is not able to perform Alert operation.");
        errorInfo.setSuggestions(suggestions);
        seleniumExceptionMap.put("UnhandledAlertException",errorInfo);
    }
    private static void setUnexpectedAlertPresentException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("UnexpectedAlertPresentException");
        errorInfo.setCause("It occurs when there is the appearance of an unexpected alert.");
       /* List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("UnexpectedAlertPresentException",errorInfo);
    }
    private static void setUnknownMethodException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("UnknownMethodException");
        errorInfo.setCause("This Exception happens when the requested command matches with a known URL but and not matching with a methodology for a specific URL.");
        /*List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("UnknownMethodException",errorInfo);
    }
    private static void setUnreachableBrowserException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("UnreachableBrowserException");
        errorInfo.setCause("This Exception occurs only when the browser is not able to be opened or crashed because of some reason.");
        /*List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("UnreachableBrowserException",errorInfo);
    }
    private static void setUnsupportedCommandException(){
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setName("UnsupportedCommandException");
        errorInfo.setCause("This occurs when remote WebDriver does n't send valid commands as expected.");
        /*List<String> suggestions = new ArrayList<String>();
        suggestions.add("");
        errorInfo.setSuggestions(suggestions);*/
        seleniumExceptionMap.put("UnsupportedCommandException",errorInfo);
    }

}
