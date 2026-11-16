package io.github.marcperez06.ai_api_wrapper.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.github.marcperez06.ai_api_wrapper.exception.AiConfigurationException;
import io.github.marcperez06.ai_api_wrapper.information.PropertiesKeys;
import io.github.marcperez06.java_utilities.file.FileUtils;
import io.github.marcperez06.java_utilities.json.GsonUtils;
import io.github.marcperez06.java_utilities.properties.EnvironmentProperties;
import io.github.marcperez06.java_utilities.validation.ValidationUtils;

public class AiConfigLoader {

    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private static EnvironmentProperties envProperties;
    
    public static AiConfig load(String filePath) {
    	AiConfig aiConfig = null;
    	if (filePath.endsWith(".json")) {
    		aiConfig = loadFromJson(filePath);
        } else if (filePath.endsWith(".yaml") || filePath.endsWith(".yml")) {
        	aiConfig = loadFromYaml(filePath);
        } else if (filePath.endsWith(".properties")) {
        	aiConfig = loadFromProperties(filePath);
        } else {
            throw new AiConfigurationException("Unsupported configuration file format: " + filePath);
        }
    	return aiConfig;
    }

    public static AiConfig loadFromJson(String filePath) {
    	AiConfig aiConfig = null;
    	String fileContent = FileUtils.readFile(filePath);
        if (ValidationUtils.isNotEmpty(fileContent)) {
        	aiConfig = GsonUtils.returnJsonObject(fileContent, AiConfig.class);	
        } else {
        	throw new AiConfigurationException("Failed to parse JSON configuration of file " + filePath);
        }
        return aiConfig;
    }

    public static AiConfig loadFromYaml(String filePath) {
    	AiConfig aiConfig = null;
    	File file = new File(filePath);
    	boolean existFile = ValidationUtils.isNotNull(file) && file.exists();
        if (existFile) {
        	try (InputStream inputStream = new FileInputStream(file)) {
            	aiConfig = yamlMapper.readValue(inputStream, AiConfig.class);
            } catch (IOException e) {
                throw new AiConfigurationException("Failed to parse YAML configuration", e);
            }
        } else {
        	throw new AiConfigurationException("Configuration file does not exist: " + file.getAbsolutePath());
        }
        
        return aiConfig;
    }

    public static AiConfig loadFromProperties(String filePath) {
    	envProperties = new EnvironmentProperties(filePath);
    	AiConfig config = new AiConfig();
    	
    	envProperties.loadPropertiesFromFile(filePath);
    	
    	config.setProvider(getStringPropertyOrDefault(PropertiesKeys.AI_PROVIDER, "ollama"));
    	config.setModel(getStringPropertyOrDefault(PropertiesKeys.AI_MODEL, "llama3"));
        config.setApiKey(getStringPropertyOrDefault(PropertiesKeys.AI_API_KEY, ""));
        config.setEndpoint(getStringPropertyOrDefault(PropertiesKeys.AI_API_URL, ""));
        config.setTemperature(getDoublePropertyOrDefault(PropertiesKeys.AI_TEMPERATURE, 0.2));
        config.setTimeout(getIntPropertyOrDefault(PropertiesKeys.AI_TIMEOUT, 30));
        config.setMaxContent(getIntPropertyOrDefault(PropertiesKeys.AI_MAX_CONTENT, 1024));
        config.setChatHistorySize(getIntPropertyOrDefault(PropertiesKeys.AI_CHAT_HISTORY_SIZE, 3));
        
        return config;
    }
    
    private static Integer getIntPropertyOrDefault(String propertyKey, int defaultValue) {
		Integer value = defaultValue;
		if (envProperties.existProperty(propertyKey)) {
			value = envProperties.getPropertyAsInteger(propertyKey);
		}
		return value;
	}

	private static Double getDoublePropertyOrDefault(String propertyKey, double defaultValue) {
		Double value = defaultValue;
		if (envProperties.existProperty(propertyKey)) {
			value = envProperties.getPropertyAsDouble(propertyKey);
		}
		return value;
	}

	private static String getStringPropertyOrDefault(String propertyKey, String defaultValue) {
		String value = defaultValue;
		if (envProperties.existProperty(propertyKey)) {
			value = envProperties.getProperty(propertyKey);
		}
		return value;
	}
}
