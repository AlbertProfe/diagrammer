package dev.diagrammer.assistant;

import static org.assertj.core.api.Assertions.assertThat;

import dev.diagrammer.endpoint.UiCopy;
import dev.diagrammer.profile.DiagramType;
import dev.diagrammer.profile.Domain;
import dev.diagrammer.profile.Goal;
import dev.diagrammer.profile.PreProfile;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

class ScriptedAiClientTest {

    private static final String CLEAR = "How adopt-dog travels from CLI to Repository";

    private static final String HTML = "<script>window.__xss=1</script><img src=x onerror=\"window.__xss=1\"><b>injected</b>";

    private ScriptedAiClient client;

    private String clarify;

    private String goalAsk;

    @BeforeEach
    void client() {
        MessageSource messages = bundle();
        client = new ScriptedAiClient(messages);
        clarify = messages.getMessage("assistant.clarify", null, java.util.Locale.ENGLISH);
        goalAsk = messages.getMessage("assistant.goal.ask", null, java.util.Locale.ENGLISH);
    }

    @Test
    void offlineClientUsesTheSystemPromptFromTheBundle() {
        assertThat(client.offline()).isTrue();
        assertThat(client.systemPrompt().toLowerCase(java.util.Locale.ROOT))
                .contains("do not output diagrams")
                .contains("specs")
                .contains("html");

        UiCopy ui = UiCopy.english(bundle());
        assertThat(ui.titleLabel()).isEqualTo("Title");
        assertThat(ui.titlePlaceholder()).isEqualTo("Name this diagram");
        assertThat(ui.titleError()).isEqualTo("Use 3 to 80 characters.");
        assertThat(ui.titleEmpty()).isEqualTo("Enter a title.");
        assertThat(ui.generateButton()).isEqualTo("Confirm & Generate");
        assertThat(ui.generateHint()).isEqualTo("Title and Topic are needed.");
        assertThat(ui.generateLater()).isEqualTo("Generation arrives in a later release");
        assertThat(ui.assistantOffline()).contains("Offline assistant");
        assertThat(ui.notSet()).isEqualTo("not set yet");
        assertThat(ui.topicJava()).isEqualTo(CLEAR);
        assertThat(ui.topicMl()).isEqualTo("What happens in a train/test split");
        assertThat(ui.topicReact()).isEqualTo("Lifecycle of a component with useEffect");
        assertThat(ui.topicNeutral()).isEqualTo("Describe your topic in one sentence");
    }

    @Test
    void greetingNamesTheChipsTheTitleAndTheDomainExample() {
        assertThat(client.greeting(Domain.JAVA)).contains("Title").contains("chips").contains(CLEAR);
        assertThat(client.greeting(Domain.ML)).contains("What happens in a train/test split");
        assertThat(client.greeting(Domain.REACT)).contains("Lifecycle of a component with useEffect");
        assertThat(client.greeting(Domain.SPRING_BOOT)).contains("Describe your topic in one sentence");
        assertThat(client.greeting(Domain.PYTHON)).contains("Describe your topic in one sentence");
        assertThat(client.greeting(Domain.JAVA)).doesNotContain("Describe your topic in one sentence");
    }

    @Test
    void clearTopicSetsTheTopicAndSummarizesEveryField() {
        PreProfile profile = PreProfile.defaults().withTitle("Adopt a dog");

        AssistantReply reply = client.chat(request(profile, List.of(), CLEAR));

        assertThat(reply.topic()).isEqualTo(CLEAR);
        assertThat(reply.refinements()).isNull();
        assertThat(reply.confirmedGoal()).isNull();
        assertThat(reply.text())
                .contains("Profile summary")
                .contains("Title: Adopt a dog")
                .contains("Domain: Java")
                .contains("Goal: Explain flow")
                .contains("Interactivity: Step-by-step")
                .contains("Type: Auto")
                .contains("Depth: With code")
                .contains("Theme: Light")
                .contains("Audience: Beginner")
                .contains("Topic: " + CLEAR)
                .contains("Refinements: not set yet")
                .doesNotContain("likely")
                .doesNotContain("<html")
                .doesNotContain("<svg")
                .doesNotContain("DiagramSpec");
    }

    @Test
    void shortTopicAndTheFixedListAreVagueAndTheQuestionIsAskedOnce() {
        AssistantReply first = client.chat(request(PreProfile.defaults(), List.of(), "dogs"));
        assertThat(first.text()).isEqualTo(clarify);
        assertThat(first.topic()).isEqualTo("dogs");
        assertThat(first.refinements()).isNull();

        AssistantReply listed = client.chat(
                request(PreProfile.defaults(), List.of(), "please make me a diagram about this"));
        assertThat(listed.text()).isEqualTo(clarify);
        assertThat(listed.topic()).isEqualTo("please make me a diagram about this");

        PreProfile withTopic = PreProfile.defaults().withTopic("dogs");
        String answer = "the path from the CLI into the repository";
        AssistantReply second = client.chat(request(
                withTopic,
                List.of(ChatTurn.user("dogs"), ChatTurn.assistant(first.text())),
                answer));
        assertThat(second.refinements()).isEqualTo(answer);
        assertThat(second.topic()).isEqualTo("dogs");
        assertThat(second.text()).contains("Profile summary").contains("Refinements: " + answer).doesNotContain(clarify);

        PreProfile done = withTopic.withRefinements(answer);
        AssistantReply third = client.chat(request(
                done,
                List.of(
                        ChatTurn.user("dogs"),
                        ChatTurn.assistant(first.text()),
                        ChatTurn.user(answer),
                        ChatTurn.assistant(second.text())),
                "more"));
        assertThat(third.text()).doesNotContain(clarify);
        assertThat(third.confirmedGoal()).isNull();
        assertThat(third.topic()).isEqualTo("dogs");
    }

    @Test
    void fiveWordsIsAClearTopic() {
        String topic = "Spring Boot handles HTTP requests";

        AssistantReply reply = client.chat(request(PreProfile.defaults(), List.of(), topic));

        assertThat(ScriptedAiClient.vague(topic)).isFalse();
        assertThat(reply.topic()).isEqualTo(topic);
        assertThat(reply.text()).startsWith("Profile summary");
    }

    @Test
    void goalOtherMapsAPhraseAndChangesTheGoalOnlyAfterConfirm() {
        PreProfile other = PreProfile.defaults().withGoal(Goal.OTHER).withTitle("Layers");
        AssistantReply ask = client.chat(request(other, List.of(), CLEAR));
        assertThat(ask.text()).isEqualTo(goalAsk);
        assertThat(ask.topic()).isEqualTo(CLEAR);
        assertThat(ask.confirmedGoal()).isNull();

        PreProfile withTopic = other.withTopic(CLEAR);
        AssistantReply mapped = client.chat(request(
                withTopic, List.of(ChatTurn.user(CLEAR), ChatTurn.assistant(ask.text())), "show the layers of the app"));
        assertThat(mapped.text()).contains("That maps to Show layers");
        assertThat(mapped.confirmedGoal()).isNull();
        assertThat(mapped.topic()).isEqualTo(CLEAR);

        AssistantReply confirmed = client.chat(request(
                withTopic,
                List.of(
                        ChatTurn.user(CLEAR),
                        ChatTurn.assistant(ask.text()),
                        ChatTurn.user("show the layers of the app"),
                        ChatTurn.assistant(mapped.text())),
                "yes"));
        assertThat(confirmed.confirmedGoal()).isEqualTo(Goal.LAYERS);
        assertThat(confirmed.text())
                .contains("Goal: Show layers")
                .contains("Domain: Java")
                .contains("Type: Auto")
                .contains("Title: Layers")
                .contains("Theme: Light")
                .contains("Audience: Beginner");
        assertThat(withTopic.goal()).isEqualTo(Goal.OTHER);
        assertThat(withTopic.type()).isEqualTo(DiagramType.AUTO);
        assertThat(withTopic.domain()).isEqualTo(Domain.JAVA);
    }

    @Test
    void goalPhrasesMapOntoTheCanonicalGoals() {
        PreProfile other = PreProfile.defaults().withGoal(Goal.OTHER).withTopic(CLEAR);
        List<ChatTurn> history = List.of(ChatTurn.user(CLEAR), ChatTurn.assistant(goalAsk));

        assertThat(mapped(other, history, "compare the two libraries")).contains("Compare concepts");
        assertThat(mapped(other, history, "walk through a use case")).contains("Walk through use case");
        assertThat(mapped(other, history, "make it memorable for newcomers")).contains("Explain flow");
    }

    @Test
    void htmlFromTheClientIsReturnedAsTextAndDoesNotChangeTheProfile() {
        PreProfile profile = PreProfile.defaults().withTitle("Safe");

        AssistantReply reply = client.chat(request(profile, List.of(), HTML));

        assertThat(reply.text()).isEqualTo(HTML);
        assertThat(reply.text()).contains("<script>");
        assertThat(reply.topic()).isNull();
        assertThat(reply.refinements()).isNull();
        assertThat(reply.confirmedGoal()).isNull();
    }

    private String mapped(PreProfile profile, List<ChatTurn> history, String phrase) {
        return client.chat(request(profile, history, phrase)).text();
    }

    private static AssistantRequest request(PreProfile profile, List<ChatTurn> history, String message) {
        return new AssistantRequest(profile, history, message);
    }

    private static MessageSource bundle() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:messages");
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setFallbackToSystemLocale(false);
        return source;
    }
}
