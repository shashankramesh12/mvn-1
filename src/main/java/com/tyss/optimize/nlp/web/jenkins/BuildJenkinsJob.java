package com.tyss.optimize.nlp.web.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
@Component(value = "BuildJenkinsJob")
public class BuildJenkinsJob implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        String consoleUrl = null;
        String buildStatus = CommonConstants.FAILURE;

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String hostUrl = (String) attributes.get("hostUrl");
            String hostName = (String) attributes.get("hostname");
            int portNumber = Integer.parseInt((String)attributes.get("port"));
            String username = (String) attributes.get("username");
            String password = (String) attributes.get("password");
            String jobName = (String) attributes.get("jobName");
            String token = (String) attributes.get("token");
            int sec = Integer.parseInt((String)attributes.get("seconds"));

            String failMessage = nlpRequestModel.getFailMessage();
            String passMessage = nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }

            String buildUrl = hostUrl+"/user/"+username.toLowerCase()+"/my-views/view/all/";
            JenkinsServer jenkinsBuild = new JenkinsServer(URI.create(buildUrl), username, password);
            JobWithDetails completedJobs = jenkinsBuild.getJob(jobName);
            int previousJobCount = completedJobs.getAllBuilds().size();

            String crumbUrl = hostUrl+"/crumbIssuer/api/json";
            String url = hostUrl+"/job/"+jobName+"/build?token="+token;
            String cookiePath = "/home/TYSS/Cookies/";
            long randomNumber = System.currentTimeMillis();
            String cookieFileName = "cookies_"+randomNumber+".txt";
            String COOKIE_JAR= cookiePath+cookieFileName;

            modifiedFailMessage = failMessage.replace("*hostUrl*", url);
            String modifiedPassMessage = passMessage.replace("*hostUrl*", url);

            File fileCookie = new File(COOKIE_JAR);
            if(fileCookie.exists()) {
                FileUtils.deleteQuietly(fileCookie);
            }

            StringBuilder sbData = new StringBuilder();
            sbData.append("host="+hostName+"\n");
            sbData.append("port="+portNumber+"\n");
            sbData.append("username="+username+"\n");
            sbData.append("password="+password+"\n");
            FileUtils.writeStringToFile(fileCookie,sbData.toString(), Charset.forName("US-ASCII"),false);

            String[] commandCrumb = { "curl","--cookie-jar",COOKIE_JAR, "-u", username + ":" + password, "-X", "GET", crumbUrl };

            ProcessBuilder processCrumb = new ProcessBuilder(commandCrumb);
            Process p1,p2;
            log.info("Building jenkins job " + url + " in Browser window");

            p1 = processCrumb.start();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            StringBuilder builder1 = new StringBuilder();
            String line1 = null;
            while ((line1 = reader1.readLine()) != null) {
                builder1.append(line1);
                builder1.append(System.getProperty("line.separator"));
            }
            String result1 = builder1.toString();
            String crumb = result1.split(",")[1].split(":")[1].toString();
            p1.destroyForcibly();

            String[] command = { "curl","--cookie",COOKIE_JAR, "-H", "Content-Type: text/plain", "-H","User-Agent: Chrome Mozilla Safari AppleWebKit", "-H", "Jenkins-Crumb:"+String.valueOf(crumb), "-u", username + ":" + password, "-X", "POST", url };

            ProcessBuilder process = new ProcessBuilder(command);
            p2 = process.start();
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
            StringBuilder builder2 = new StringBuilder();
            String line2 = null;
            while ((line2 = reader2.readLine()) != null) {
                builder2.append(line2);
                builder2.append(System.getProperty("line.separator"));
            }
            String result2 = builder2.toString();
            p2.destroyForcibly();

            log.info("Wait for "+sec+" seconds to start a build");
            Thread.sleep(sec*1000);
            log.info("Jenkins Build running...");

            JobWithDetails job = jenkinsBuild.getJob(jobName);
            Build lastBuild = job.getLastBuild();
            int currentJobCount = job.getAllBuilds().size();
            boolean isBuilding = lastBuild.details().isBuilding();
            while (isBuilding) {
                Thread.sleep(sec*10);
                isBuilding = lastBuild.details().isBuilding();
            }
            int buildNumber = lastBuild.getNumber();
            consoleUrl = hostUrl+"/job/"+jobName+"/"+buildNumber+"/console";
            buildStatus = lastBuild.details().getResult().toString();
            modifiedFailMessage = modifiedFailMessage.replace("*consoleUrl*", consoleUrl);
            modifiedPassMessage = modifiedPassMessage.replace("*consoleUrl*", consoleUrl);
            log.info("The Jenkins Job is completed.");
            if(currentJobCount == previousJobCount) {
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
            } else if(buildStatus.equalsIgnoreCase("success")){
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in NavigateToURL ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Map<String , Object> returnMap = new HashMap<>();
        returnMap.put("buildStatus", buildStatus);
        returnMap.put("consoleUrl", consoleUrl);
        nlpResponseModel.getAttributes().put("return", returnMap);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        return params;
    }

}

