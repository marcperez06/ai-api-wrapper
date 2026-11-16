# ai-api-wrapper

[![Java 8](https://img.shields.io/badge/Java-8-blue.svg)](https://www.oracle.com/java/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

`ai-api-wrapper` is a lightweight, flexible Java library designed to seamlessly interact with multiple Artificial Intelligence (AI) providers and models, both from external cloud APIs and local machines.

## Key Features

- **Multi-Provider Support**: Easily switch between or integrate with:
  - **OpenAI** (and OpenAI-compatible endpoints / LM Studio / Custom)
  - **Anthropic Claude**
  - **Google Gemini**
  - **Ollama** (Local AI models)
- **Flexible Configuration**: Load configuration settings via `.properties`, `.json`, or `.yml` files using `AiConfigLoader`.
- **Agent Configuration**: Define complex agent behaviors, system prompts, guardrails, memory, and tool configurations using YAML (`AgentLoader`).
- **Java 8 Compatibility**: Built with Java 8, with minimal dependencies (`jackson-databind`, `jackson-dataformat-yaml`, and `java-utilities`).

---

## Requirements

- **Java**: JDK 1.8 or higher
- **Build Tool**: Apache Maven 3.x

---

## Installation / Maven Dependency

Add the dependency to your `pom.xml` or build and package locally:

```xml
<dependency>
    <groupId>io.github.marcperez06</groupId>
    <artifactId>ai-api-wrapper</artifactId>
    <version>0.0.1</version>
</dependency>
```

---

## Configuration

You can configure the AI client using properties, JSON, or YAML.

### Properties Example (`ai-api-wrapper.properties`)
```properties
ai.provider=openai
ai.model=gpt-4
ai.api.url=https://api.openai.com/v1
ai.api.key=your-api-key-here
ai.temperature=0.5
ai.timeout=20
ai.max.content=512
ai.chat.history.size=3
```

### JSON Example (`ai-api-wrapper.json`)
```json
{
  "provider": "ollama",
  "apiKey": "",
  "endpoint": "http://localhost:11434",
  "model": "llama3",
  "temperature": 0.7,
  "timeout": 30,
  "maxContent": 1024
}
```

### YAML Example (`ai-api-wrapper.yml`)
```yaml
ai:
  provider: claude
  apiKey: test-claude-key
  endpoint: https://api.anthropic.com/v1
  model: claude-3-opus-20240229
  temperature: 0.2
  timeout: 15
  maxContent: 2048
```

---

## Agent Configuration

You can define advanced agent behaviors, system instructions, tools, and guardrails in YAML (e.g., `agent-config-example.yml`):

```yaml
agent:
  name: "support-assistant-v2"
  version: "2.1.0"
  description: "A helpful, empathetic customer support agent."

  model:
    provider: "google"
    name: "gemini-3.1-flash-lite"
    temperature: 0.3
    maxTokens: 1500

  systemPrompt: |
    You are an expert customer support specialist for ShopFast.
    
    Guidelines:
    1. Always verify the customer's order ID before providing shipping updates.
    2. Keep your tone professional, warm, and concise.

  guardrails:
    contentFiltering: true
    maxRetryOnError: 3
```

---

## Usage Example

```java
import io.github.marcperez06.ai_api_wrapper.client.AiClientFactory;
import io.github.marcperez06.ai_api_wrapper.client.IAiApiClient;
import io.github.marcperez06.ai_api_wrapper.config.AiConfig;
import io.github.marcperez06.ai_api_wrapper.config.AiConfigLoader;
import io.github.marcperez06.ai_api_wrapper.information.Paths;
import io.github.marcperez06.ai_api_wrapper.model.AiResponse;

public class Main {
    public static void main(String[] args) {
        // Load configuration
        AiConfig aiConfig = AiConfigLoader.load(Paths.PROPERTIES_PATH);

        // Create client via factory
        IAiApiClient aiClient = AiClientFactory.createClient(aiConfig);

        // Generate response
        AiResponse aiResponse = aiClient.generate("Where is my package?");
        System.out.println("AI Response: " + aiResponse.getContent());
    }
}
```

---

## Building and Testing

To compile and run tests:

```bash
mvn clean test
```

To build executable shaded JARs:

```bash
mvn clean package
```

---

## License

This project is licensed under the [MIT License](http://www.opensource.org/licenses/mit-license.php).

## Author

- **Marc Perez Rodriguez** ([marcperez06](https://github.com/marcperez06))
