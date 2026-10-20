import { Button } from '@vaadin/react-components/Button.js';
import { TextField } from '@vaadin/react-components/TextField.js';
import { useSignal } from '@vaadin/hilla-react-signals';
import { AssistantEndpoint } from 'Frontend/generated/endpoints';
import type AssistantReply from 'Frontend/generated/dev/diagrammer/assistant/AssistantReply';
import type PreProfile from 'Frontend/generated/dev/diagrammer/profile/PreProfile';
import type UiCopy from 'Frontend/generated/dev/diagrammer/endpoint/UiCopy';

type Turn = { role: 'user' | 'assistant'; text: string };

/**
 * Profile-assistant transcript. Assistant lines are React text, so a reply that
 * contains tags is shown as text and is not parsed as HTML.
 */
export function AssistantPanel({
  copy,
  offline,
  domain,
  profile,
  onReply,
}: {
  copy: UiCopy;
  offline: boolean;
  domain: string | null | undefined;
  profile: PreProfile;
  onReply: (reply: AssistantReply) => void;
}) {
  const turns = useSignal<Turn[]>([]);
  const draft = useSignal('');
  const sending = useSignal(false);

  async function send() {
    const text = draft.value.trim();
    if (!text || sending.value) {
      return;
    }
    sending.value = true;
    const history = turns.value.map((turn) => ({ role: turn.role, text: turn.text }));
    draft.value = '';
    try {
      const reply = await AssistantEndpoint.chat({ profile, history, userMessage: text });
      const answer = reply?.text ?? copy.assistantError;
      turns.value = [...turns.value, { role: 'user', text }, { role: 'assistant', text: answer }];
      if (reply?.text) {
        onReply(reply);
      }
    } catch {
      turns.value = [...turns.value, { role: 'user', text }, { role: 'assistant', text: copy.assistantError }];
    } finally {
      sending.value = false;
    }
  }

  const greeting = `${copy.greetingLead} ${topicExample(copy, domain)}`;

  return (
    <aside className="assistant-panel" aria-label={copy.assistantHeading}>
      <h2>{copy.assistantHeading}</h2>
      {offline && <p className="assistant-offline">{copy.assistantOffline}</p>}
      <div className="assistant-log">
        <p className="assistant-message" data-role="assistant" data-kind="greeting">
          {greeting}
        </p>
        {turns.value.map((turn, index) => (
          <p key={`${turn.role}-${index}`} className="assistant-message" data-role={turn.role}>
            {turn.text}
          </p>
        ))}
      </div>
      <div className="assistant-compose">
        <TextField
          className="assistant-input"
          label={copy.assistantInput}
          placeholder={topicExample(copy, domain)}
          value={draft.value}
          onValueChanged={(event) => {
            draft.value = event.detail.value;
          }}
          onKeyDown={(event) => {
            if (event.key === 'Enter') {
              event.preventDefault();
              void send();
            }
          }}
        />
        <Button
          className="assistant-send"
          theme="primary"
          disabled={sending.value}
          onClick={() => {
            void send();
          }}
        >
          {copy.assistantSend}
        </Button>
      </div>
    </aside>
  );
}

export function topicExample(copy: UiCopy, domain: string | null | undefined): string {
  switch (domain) {
    case 'ML':
      return copy.topicMl;
    case 'REACT':
      return copy.topicReact;
    case 'SPRING_BOOT':
    case 'PYTHON':
      return copy.topicNeutral;
    default:
      return copy.topicJava;
  }
}
