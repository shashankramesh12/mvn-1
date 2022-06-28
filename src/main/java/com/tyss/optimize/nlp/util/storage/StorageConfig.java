package com.tyss.optimize.nlp.util.storage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tyss.optimize.data.models.dto.StorageInput;
import com.tyss.optimize.data.models.dto.StorageOutput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "StorageConfig")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class StorageConfig {

    @Transient
    public static final String SEQUENCE_NAME = "STORAGECONFIG";

    @Id
    public String id;
    public String licenseId;
    public String username;
    public String password;
    public String domain;
    public String sharedFolder;
    public String accessKey;
    public String secretKey;
    public String ipAddress;
    public String type;
    private StorageInput inputs;
    private StorageOutput outputs;

}
