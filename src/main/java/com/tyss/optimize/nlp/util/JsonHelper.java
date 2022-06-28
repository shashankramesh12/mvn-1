package com.tyss.optimize.nlp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class JsonHelper {

    public boolean isJSONValid(String jsonInString) {

        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            log.error("Failed to validate JSON Object");
            return false;
        }
    }

    public Object getJsonPathData(String jsonPath, Object jsonObject) {
        Object jsonData = JsonPath.read(jsonObject.toString(), jsonPath);
        if(Objects.isNull(jsonData)){
            DocumentContext context = JsonPath.parse(jsonObject.toString());
            jsonData = context.read(jsonPath);
        }
        return jsonData;
    }
}
