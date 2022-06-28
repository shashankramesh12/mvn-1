package com.tyss.optimize.nlp.util;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v96.network.Network;
import org.openqa.selenium.remote.Augmenter;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class WebDriverDevTools {

    public DevTools getDevTools(WebDriver driver){

        DevTools chromeDevTools = null;
        try{
            ChromeDriver chromeDriver = (ChromeDriver) new Augmenter().augment(driver);
            chromeDevTools = chromeDriver.getDevTools();
            chromeDevTools.createSession();
            chromeDevTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        } catch (Exception e){
            log.error("Exception in WebDriverDevTools : " + e.getMessage() + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
        }
        return chromeDevTools;
    }

}
