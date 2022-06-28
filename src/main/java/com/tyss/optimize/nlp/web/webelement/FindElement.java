package com.tyss.optimize.nlp.web.webelement;

import com.tyss.optimize.nlp.web.exception.NoLocatorForWebElementException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Slf4j
public class FindElement {


	public Object[] findElement(WebDriver driver, String elementName, String elementType,
			List<Map> locatorList) throws NoSuchElementException {
		int numberOfLocators = locatorList.size();
		int iterationCount = 0;
		WebElement element = null;
		Object[] returnObj=new Object[3];
		log.info(
				"Number of locators for " + elementName + " " + elementType + " is:" + numberOfLocators);
		if (numberOfLocators == 0) {
			throw new NoLocatorForWebElementException(elementName);
		}
		for(Map locator : locatorList) {
			iterationCount++;
			String locatorName = locator.get("LocatorName").toString();
			String locatorValue = locator.get("LocatorValue").toString();
			try {
				By b = (By) By.class.getDeclaredMethod(locatorName, String.class).invoke(null, locatorValue);
				try {
					element = driver.findElement(b);
					log.info(
							elementName + " " + elementType + " element found; stoping the findELement iteration");
					returnObj[0]=locatorName;
					break;
				} catch (Exception e) {
					if (iterationCount < numberOfLocators) {
						log.info(
								elementName + " " + elementType + " element not found trying next locator");
					} else {
						log.error( elementName + " " + elementType + " element not found");
						//throw new NoSuchElementException("Searched with all specified locators;still Element Not found");
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		if(element == null) {
			throw new NoSuchElementException("Searched with all specified locators;still Element Not found");
		}
		returnObj[1]=iterationCount;
		returnObj[2]=element;

		return returnObj;
	}

}
