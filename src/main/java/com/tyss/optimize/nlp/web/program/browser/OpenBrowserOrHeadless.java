package com.tyss.optimize.nlp.web.program.browser;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.util.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "OpenBrowserOrHeadless")
public class OpenBrowserOrHeadless implements Nlp {
    private static final String IO_EXCEPTION = "IOException = ";
    private static final String DETAILS_COULD_NOT_GET_IP_ADDRESSS = "Details = Could not get IP Address:";

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String hubURL = (String) nlpRequestModel.getUrl();
            DesiredCapabilities capabilities = (DesiredCapabilities) nlpRequestModel.getDesiredCapabilities();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            WebDriver driver = null;
            log.info("HUB URL is: " + hubURL);
            URL remoteAddress = new URL(hubURL);
            if (capabilities == null) {
                log.info("Please ensure that capabilities are set before this step ");
                nlpResponseModel.setMessage(failMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            } else {
                if (checkIfDNSReachable(hubURL)) {
                    driver = new RemoteWebDriver(remoteAddress, capabilities);
                }
                log.info("Executing in Headless mode");
                String browse_name = capabilities.getBrowserName();
                log.info("Browser is:" + browse_name);
                if ("firefox".equalsIgnoreCase(browse_name)) {
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                } else if ("IE".equalsIgnoreCase(browse_name)) {
                    WebDriverManager.iedriver().setup();
                    driver = new InternetExplorerDriver();
                } else {
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--disable-notifications");
                    options.addArguments("--disable-infobars");
                    options.addArguments("--headless");
                    driver = new ChromeDriver(options);
                }
                nlpResponseModel.setMessage(passMessage);
                IDriver iDriver = new com.tyss.optimize.data.models.dto.drivers.WebDriver(driver);
                nlpResponseModel.setDriver(iDriver);
                nlpResponseModel.setStatus(CommonConstants.pass);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in OpenBrowserOrHeadless ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }

        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public boolean checkIfDNSReachable(String url) {
        try {
            if (InetAddress.getByName(new URL(url).getHost()).isReachable(200)) {
                return checkIfIPReachable(url);
            } else {
                return checkIfIPReachable(url);
            }
        } catch (MalformedURLException e) {
            log.error(("MalformedURLException = " + DETAILS_COULD_NOT_GET_IP_ADDRESSS
                    + e.getMessage() + System.lineSeparator() + Arrays.toString(e.getStackTrace())));
        } catch (IOException e) {
            log.error(IO_EXCEPTION + DETAILS_COULD_NOT_GET_IP_ADDRESSS + e.getMessage()
                    + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
        }
        return false;
    }

    private boolean checkIfIPReachable(String serverUrl) {
        Socket socket = null;
        try {
            URL url = new URL(serverUrl);
            if (url.getPort() == -1) {
                return true;
            }
            socket = new Socket(url.getHost(), url.getPort());
        } catch (IOException e) {
            log.error(IO_EXCEPTION + DETAILS_COULD_NOT_GET_IP_ADDRESSS + e.getMessage()
                    + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error(IO_EXCEPTION + DETAILS_COULD_NOT_GET_IP_ADDRESSS + e.getMessage()
                            + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
                }
            }
        }
        return true;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("boolean isDNSReachable = true;\n");
        sb.append("URL remoteAddress = new URL(hubURL);\n");
        sb.append("if (capabilities == null) {\n");
        sb.append("	System.out.println(\"Please ensure that capabilities are set before this step\");\n");
        sb.append("} else {\n");
        sb.append("	Socket socket = null;\n");
        sb.append("	try {\n");
        sb.append("		boolean isReachable = InetAddress.getByName(new URL(hubURL).getHost()).isReachable(200);\n");
        sb.append("		URL url = new URL(hubURL);\n");
        sb.append("		if (url.getPort() == -1) {\n");
        sb.append("			isDNSReachable = true;\n");
        sb.append("		}\n");
        sb.append("		socket = new Socket(url.getHost(), url.getPort());\n");
        sb.append("	} catch (MalformedURLException e) {\n");
        sb.append("		isDNSReachable = false;\n");
        sb.append("	} catch (IOException e) {\n");
        sb.append("		isDNSReachable = false;\n");
        sb.append("	}finally {\n");
        sb.append("		if (socket != null) {\n");
        sb.append("			try {\n");
        sb.append("				socket.close();\n");
        sb.append("			} catch (IOException e) {\n");
        sb.append("				isDNSReachable = false;\n");
        sb.append("			}\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	if (isDNSReachable) {\n");
        sb.append("		driver = new RemoteWebDriver(remoteAddress, capabilities);\n");
        sb.append("	}\n");
        sb.append("	String browse_name = capabilities.getBrowserName();\n");
        sb.append("	if (\"firefox\".equalsIgnoreCase(browse_name)) {\n");
        sb.append("		WebDriverManager.firefoxdriver().setup();\n");
        sb.append("		driver = new FirefoxDriver();\n");
        sb.append("	} else if (\"IE\".equalsIgnoreCase(browse_name)) {\n");
        sb.append("		WebDriverManager.iedriver().setup();\n");
        sb.append("		driver = new InternetExplorerDriver();\n");
        sb.append("	} else {\n");
        sb.append("		WebDriverManager.chromedriver().setup();\n");
        sb.append("		ChromeOptions options = new ChromeOptions();\n");
        sb.append("		options.addArguments(\"--disable-notifications\");\n");
        sb.append("		options.addArguments(\"--disable-infobars\");\n");
        sb.append("		options.addArguments(\"--headless\");\n");
        sb.append("		driver = new ChromeDriver(options);\n");
        sb.append("	}\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("hubURL::\"https://www.google.com/\"");
        params.add("capabilities::\"randomObject\"");

        return params;
    }
}
