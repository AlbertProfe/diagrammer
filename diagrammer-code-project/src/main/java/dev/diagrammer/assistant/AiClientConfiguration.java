package dev.diagrammer.assistant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Selects the chat client. With no Bedrock config ({@code diagrammer.bedrock.enabled}
 * unset or false) the scripted assistant is the only {@link AiClient} bean.
 * There is no Bedrock implementation in this release, so enabling that flag
 * leaves the application without a client instead of calling AWS.
 */
@Configuration
public class AiClientConfiguration {

    @Bean
    @ConditionalOnProperty(
            prefix = "diagrammer.bedrock",
            name = "enabled",
            havingValue = "false",
            matchIfMissing = true)
    AiClient aiClient(MessageSource messageSource) {
        return new ScriptedAiClient(messageSource);
    }
}
