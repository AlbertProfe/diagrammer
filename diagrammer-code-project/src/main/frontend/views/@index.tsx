import { Button } from '@vaadin/react-components/Button.js';
import { Details } from '@vaadin/react-components/Details.js';
import { TextField } from '@vaadin/react-components/TextField.js';
import { useSignal } from '@vaadin/hilla-react-signals';
import type { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { AssistantEndpoint, MessagesEndpoint, ProfileEndpoint } from 'Frontend/generated/endpoints';
import type AssistantReply from 'Frontend/generated/dev/diagrammer/assistant/AssistantReply';
import type AudienceOption from 'Frontend/generated/dev/diagrammer/profile/AudienceOption';
import type DepthOption from 'Frontend/generated/dev/diagrammer/profile/DepthOption';
import type DiagramTypeOption from 'Frontend/generated/dev/diagrammer/profile/DiagramTypeOption';
import type DomainOption from 'Frontend/generated/dev/diagrammer/profile/DomainOption';
import type GoalOption from 'Frontend/generated/dev/diagrammer/profile/GoalOption';
import type InteractivityOption from 'Frontend/generated/dev/diagrammer/profile/InteractivityOption';
import type PreProfile from 'Frontend/generated/dev/diagrammer/profile/PreProfile';
import type ThemeOption from 'Frontend/generated/dev/diagrammer/profile/ThemeOption';
import type ProfileCatalog from 'Frontend/generated/dev/diagrammer/profile/ProfileCatalog';
import type UiCopy from 'Frontend/generated/dev/diagrammer/endpoint/UiCopy';
import { useEffect } from 'react';
import { AssistantPanel } from 'Frontend/profile/AssistantPanel';
import { applyCatalog } from 'Frontend/profile/applyCatalog';
import { ChipGroup, type ChipOption } from 'Frontend/profile/ChipGroup';
import { acceptedTitle, titleError } from 'Frontend/profile/titleText';

export const config: ViewConfig = {
  loginRequired: true,
  title: 'Diagram Orchestrator',
  menu: { title: 'Diagram Orchestrator' },
};

type Choice = {
  id?: string | null;
  label?: string | null;
  value?: string | null;
};

const FALLBACK_ERROR = 'Profile options could not be loaded.';

export default function DiagramOrchestratorView() {
  const catalog = useSignal<ProfileCatalog | undefined>(undefined);
  const copy = useSignal<UiCopy | undefined>(undefined);
  const profile = useSignal<PreProfile | undefined>(undefined);
  const offline = useSignal(false);
  const loadError = useSignal('');
  const titleValue = useSignal('');
  const titleTyped = useSignal(false);
  const titleBlurred = useSignal(false);
  const generateNote = useSignal('');

  useEffect(() => {
    let active = true;
    Promise.all([ProfileEndpoint.catalog(), MessagesEndpoint.ui(), AssistantEndpoint.offline()])
      .then(([loaded, ui, isOffline]) => {
        if (!active) {
          return;
        }
        if (!loaded?.defaults || !ui) {
          loadError.value = FALLBACK_ERROR;
          return;
        }
        catalog.value = loaded;
        copy.value = ui;
        offline.value = Boolean(isOffline);
        profile.value = applyCatalog(loaded.defaults, loaded.rules).profile;
      })
      .catch(() => {
        if (active) {
          loadError.value = FALLBACK_ERROR;
        }
      });
    return () => {
      active = false;
    };
  }, [catalog, copy, loadError, offline, profile]);

  const loaded = catalog.value;
  const ui = copy.value;
  const current = profile.value;
  if (!loaded || !current || !ui) {
    return (
      <main className="orchestrator">
        <h1>Diagram Orchestrator</h1>
        <p>{loadError.value || 'Loading profile options…'}</p>
      </main>
    );
  }

  const applied = applyCatalog(current, loaded.rules);
  const title = acceptedTitle(titleValue.value);
  const shown: PreProfile = { ...applied.profile, title };
  const error = titleError(titleValue.value, titleTyped.value, titleBlurred.value, ui.titleError, ui.titleEmpty);
  const topicSet = Boolean(shown.topic && shown.topic.trim());
  const canGenerate = Boolean(title) && topicSet;

  function select(
    group: 'domain' | 'goal' | 'interactivity' | 'type' | 'depth' | 'theme' | 'audience',
    wire: string,
  ) {
    // Read the signal, not the render snapshot. Forcing Type (Compare concepts
    // stays on Auto) emits a second value-changed while this render is stale.
    const latest = profile.value;
    if (!latest) {
      return;
    }
    const next: PreProfile = { ...latest };
    if (group === 'domain') {
      const option = find(loaded!.domains, wire);
      if (!option?.id) {
        return;
      }
      next.domain = option.id;
    } else if (group === 'goal') {
      const option = find(loaded!.goals, wire);
      if (!option?.id) {
        return;
      }
      next.goal = option.id;
    } else if (group === 'interactivity') {
      const option = find(loaded!.interactivity, wire);
      if (!option?.id) {
        return;
      }
      next.interactivity = option.id;
    } else if (group === 'type') {
      const option = find(loaded!.types, wire);
      if (!option?.id) {
        return;
      }
      next.type = option.id;
    } else if (group === 'depth') {
      const option = find(loaded!.depths, wire);
      if (!option?.id) {
        return;
      }
      next.depth = option.id;
    } else if (group === 'theme') {
      const option = find(loaded!.themes, wire);
      if (!option?.id) {
        return;
      }
      next.theme = option.id;
    } else {
      const option = find(loaded!.audiences, wire);
      if (!option?.id) {
        return;
      }
      next.audience = option.id;
    }
    profile.value = applyCatalog(next, loaded!.rules).profile;
  }

  function onReply(reply: AssistantReply) {
    const latest = profile.value;
    if (!latest) {
      return;
    }
    const next: PreProfile = { ...latest };
    if (reply.topic) {
      next.topic = reply.topic;
    }
    if (reply.refinements) {
      next.refinements = reply.refinements;
    }
    if (reply.confirmedGoal) {
      next.goal = reply.confirmedGoal;
    }
    profile.value = applyCatalog(next, loaded!.rules).profile;
  }

  return (
    <main className="orchestrator">
      <h1>Diagram Orchestrator</h1>
      <div className="orchestrator-columns">
        <section className="intake-panel" aria-label="Profile options">
          <TextField
            className="diagram-title"
            label={ui.titleLabel}
            placeholder={ui.titlePlaceholder}
            value={titleValue.value}
            aria-invalid={error ? true : undefined}
            aria-describedby={error ? 'title-error' : undefined}
            onValueChanged={(event) => {
              const next = event.detail.value;
              titleValue.value = next;
              if (next.length > 0) {
                titleTyped.value = true;
              }
            }}
            onBlur={() => {
              titleBlurred.value = true;
            }}
          />
          {error ? (
            <p id="title-error" className="title-error" role="alert">
              {error}
            </p>
          ) : null}
          <ChipGroup
            group="domain"
            label="Domain"
            value={wireOf(loaded.domains, current.domain)}
            options={chips(loaded.domains)}
            onChange={(value) => select('domain', value)}
          />
          <ChipGroup
            group="goal"
            label="Goal"
            value={wireOf(loaded.goals, current.goal)}
            options={chips(loaded.goals)}
            onChange={(value) => select('goal', value)}
          />
          <ChipGroup
            group="interactivity"
            label="Interactivity"
            value={wireOf(loaded.interactivity, current.interactivity)}
            options={chips(loaded.interactivity, applied.disabledInteractivity)}
            onChange={(value) => select('interactivity', value)}
          />
          <ChipGroup
            group="type"
            label="Type"
            value={wireOf(loaded.types, current.type)}
            options={chips(loaded.types, applied.disabledTypes)}
            onChange={(value) => select('type', value)}
          />
          <ChipGroup
            group="depth"
            label="Depth"
            value={wireOf(loaded.depths, current.depth)}
            options={chips(loaded.depths)}
            onChange={(value) => select('depth', value)}
          />
          <ChipGroup
            group="theme"
            label="Theme"
            value={wireOf(loaded.themes, current.theme)}
            options={chips(loaded.themes)}
            onChange={(value) => select('theme', value)}
          />
          <Details className="advanced" summary="Advanced" opened={true}>
            <ChipGroup
              group="audience"
              label="Audience"
              value={wireOf(loaded.audiences, current.audience)}
              options={chips(loaded.audiences)}
              onChange={(value) => select('audience', value)}
            />
          </Details>
          <PreProfileCard
            domains={loaded.domains}
            goals={loaded.goals}
            interactivity={loaded.interactivity}
            types={loaded.types}
            depths={loaded.depths}
            themes={loaded.themes}
            audiences={loaded.audiences}
            profile={shown}
            notSet={ui.notSet}
            refinementsLabel={ui.refinementsLabel}
            titleLabel={ui.titleLabel}
          />
          <div className="generate">
            <Button
              id="confirm-generate"
              disabled={!canGenerate}
              aria-describedby={canGenerate ? undefined : 'generate-hint'}
              onClick={() => {
                if (!canGenerate) {
                  return;
                }
                generateNote.value = ui.generateLater;
              }}
            >
              {ui.generateButton}
            </Button>
            {!canGenerate && (
              <p id="generate-hint" className="generate-hint">
                {ui.generateHint}
              </p>
            )}
            {generateNote.value && (
              <p id="generate-note" className="generate-note" role="status">
                {generateNote.value}
              </p>
            )}
          </div>
        </section>
        <AssistantPanel
          copy={ui}
          offline={offline.value}
          domain={shown.domain}
          profile={shown}
          onReply={onReply}
        />
      </div>
    </main>
  );
}

function PreProfileCard({
  domains,
  goals,
  interactivity,
  types,
  depths,
  themes,
  audiences,
  profile,
  notSet,
  refinementsLabel,
  titleLabel,
}: {
  domains: DomainOption[] | undefined;
  goals: GoalOption[] | undefined;
  interactivity: InteractivityOption[] | undefined;
  types: DiagramTypeOption[] | undefined;
  depths: DepthOption[] | undefined;
  themes: ThemeOption[] | undefined;
  audiences: AudienceOption[] | undefined;
  profile: PreProfile;
  notSet: string;
  refinementsLabel: string;
  titleLabel: string;
}) {
  return (
    <section className="pre-profile" aria-label="Pre-Profile">
      <h2>Pre-Profile</h2>
      <dl>
        <Row field="title" label={titleLabel} text={shownText(profile.title, notSet)} />
        <Row field="domain" label="Domain" text={labelOf(domains, profile.domain)} wire={wireOf(domains, profile.domain)} />
        <Row field="goal" label="Goal" text={labelOf(goals, profile.goal)} wire={wireOf(goals, profile.goal)} />
        <Row
          field="interactivity"
          label="Interactivity"
          text={labelOf(interactivity, profile.interactivity)}
          wire={wireOf(interactivity, profile.interactivity)}
        />
        <Row field="type" label="Type" text={labelOf(types, profile.type)} wire={wireOf(types, profile.type)} />
        <Row field="depth" label="Depth" text={labelOf(depths, profile.depth)} wire={wireOf(depths, profile.depth)} />
        <Row field="theme" label="Theme" text={labelOf(themes, profile.theme)} wire={wireOf(themes, profile.theme)} />
        <Row
          field="audience"
          label="Audience"
          text={labelOf(audiences, profile.audience)}
          wire={wireOf(audiences, profile.audience)}
        />
        <Row field="topic" label="Topic" text={shownText(profile.topic, notSet)} />
        <Row field="refinements" label={refinementsLabel} text={shownText(profile.refinements, notSet)} />
      </dl>
      <ThemePreview wire={wireOf(themes, profile.theme)} />
    </section>
  );
}

function shownText(value: string | null | undefined, notSet: string): string {
  const trimmed = value?.trim() ?? '';
  return trimmed.length === 0 ? notSet : trimmed;
}

function ThemePreview({ wire }: { wire: string }) {
  const theme = wire === 'dark' ? 'dark' : 'light';
  return (
    <section className={`theme-preview diagram-theme-${theme}`} data-theme={theme} aria-label="Theme preview">
      <p className="theme-preview-kicker">Theme preview</p>
      <div className="theme-preview-card">
        <h3 className="theme-preview-title">Adopt a dog</h3>
        <span className="theme-preview-step">Step 1</span>
        <code className="theme-preview-code">repository.save(dog)</code>
        <span className="theme-preview-action">Next</span>
      </div>
    </section>
  );
}

function Row({ field, label, text, wire }: { field: string; label: string; text: string; wire?: string }) {
  const unset = text === 'not set yet';
  return (
    <div className="pre-profile-row" data-field={field}>
      <dt>{label}</dt>
      <dd data-value={wire ?? ''} className={unset ? 'unset' : undefined}>
        {text}
      </dd>
    </div>
  );
}

function chips(options: readonly Choice[] | undefined, disabled?: ReadonlyMap<string, string>): ChipOption[] {
  return (options ?? []).flatMap((option) => {
    if (!option.value || !option.label || !option.id) {
      return [];
    }
    const tooltip = disabled?.get(option.id);
    return [
      {
        value: option.value,
        label: option.label,
        disabled: Boolean(tooltip),
        tooltip,
      },
    ];
  });
}

function find<T extends Choice>(
  options: readonly (T | null | undefined)[] | undefined,
  wire: string,
): T | undefined {
  return options?.find((option): option is T => option != null && option.value === wire);
}

function labelOf(options: readonly Choice[] | undefined, id: string | null | undefined): string {
  return options?.find((option) => option.id === id)?.label ?? '';
}

function wireOf(options: readonly Choice[] | undefined, id: string | null | undefined): string {
  return options?.find((option) => option.id === id)?.value ?? '';
}
