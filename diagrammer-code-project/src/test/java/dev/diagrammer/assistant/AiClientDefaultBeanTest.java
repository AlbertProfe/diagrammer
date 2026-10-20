package dev.diagrammer.assistant;

import static org.assertj.core.api.Assertions.assertThat;

import dev.diagrammer.profile.Domain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

/** The application starts, and the chat bean is the scripted client when Bedrock is not configured. */
@SpringBootTest
@ActiveProfiles("test")
class AiClientDefaultBeanTest {

    @Autowired
    private AiClient aiClient;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Environment environment;

    @Test
    void applicationStartsWithTheScriptedAssistantWhenBedrockIsUnset() {
        assertThat(environment.getProperty("diagrammer.bedrock.enabled")).isNull();
        assertThat(aiClient).isInstanceOf(ScriptedAiClient.class);
        assertThat(aiClient.offline()).isTrue();
        assertThat(context.getBeansOfType(AiClient.class)).hasSize(1);
        assertThat(aiClient.systemPrompt()).containsIgnoringCase("do not output diagrams");
        assertThat(aiClient.greeting(Domain.JAVA)).contains("How adopt-dog travels from CLI to Repository");
    }
}
