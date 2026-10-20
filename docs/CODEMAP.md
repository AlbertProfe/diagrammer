# Code map

Last updated: 2026-10-20 (v0.0.3)

Skimmable map of `diagrammer-code-project/` on main. One line per item. Intended design that is not implemented is listed under [Not in the code yet](#not-in-the-code-yet).

## Java packages

| Package | What it does |
| --- | --- |
| `dev.diagrammer` | Spring Boot entry point and Vaadin app shell configurator. |
| `dev.diagrammer.auth` | Login, the user store, the S3 client, and the startup profile guard. |
| `dev.diagrammer.endpoint` | Hilla `@BrowserCallable` endpoints (`@NonNullApi`). |
| `dev.diagrammer.profile` | Pre-Profile chip model and combination rules (`@NonNullApi`). |
| `dev.diagrammer.theme` | The shared diagram palette. It reads `diagram-theme.css` and does not copy the colors. |
| `dev.diagrammer.web` | Placeholder HTTP filter for public `/diagrams/**`. |

There is no Bedrock package, no planner package, and no renderer package.

## Main Java classes

### Application and config

| Class | What it does |
| --- | --- |
| `DiagrammerApplication` | Starts Spring Boot and loads Lumo, `styles.css`, and `diagram-theme.css`. |
| `UserStoreProfileGuard` | Fails startup with no profile, or activates `s3` when `DIAGRAMMER_S3_BUCKET` and `AWS_REGION` are both set. Registered from `META-INF/spring.factories`. |
| `S3ClientConfig` | On the `s3` profile, builds an `S3Client` from `AWS_REGION` and the SDK default credential chain, and exposes `S3UserStore`. |
| `PublicDiagramFilter` | After security, answers `/diagrams` and `/diagrams/**` with 404 and the text `Diagram not found`. |

### Security and auth

| Class | What it does |
| --- | --- |
| `SecurityConfig` | Permits `/diagrams/**` and `/actuator/health`; everything else needs login at `/login`. Password encoder accepts the `{noop}` prefix. |
| `UserStore` | `findAll()` and `findByUsername(String)`. |
| `FixtureUserStore` | `local` and `test` profiles. Loads classpath `fixture/users.json`. |
| `S3UserStore` | `s3` profile. Reads object key `auth/users.json` from the bucket. |
| `UsersJson` | Parses the credentials JSON array and requires the `{noop}` password prefix. |
| `StoredUser` | One credentials row: `userId`, `username`, `password`, `displayName`. |
| `DiagrammerUser` | Spring Security principal. Adds `ROLE_USER` and keeps `userId` and `displayName`. |
| `DiagrammerUserDetailsService` | Loads a `DiagrammerUser` by username from the active `UserStore`. |
| `UserStoragePrefix` | Builds `users/{userId}/` and reads the logged-in `DiagrammerUser`. Rejects a user id that could escape the folder. |

### Profile model

| Class | What it does |
| --- | --- |
| `PreProfile` | Intake shape: nullable title and topic, plus domain, goal, interactivity, type, depth, theme, and audience. `defaults()` is the chip default, including theme `LIGHT`. |
| `ProfileChoice` | Chip contract: `wireValue()` and `label()`. |
| `Domain` | `java`, `spring-boot`, `python`, `ml`, `react`. Default `java`. |
| `Goal` | `explain-flow`, `use-case`, `compare`, `layers`, `other`. Default `explain-flow`. |
| `Interactivity` | `static`, `step-by-step`, `multi-scenario`. Default `step-by-step`. |
| `DiagramType` | `auto`, `horizontal-steps`, `u-shape`, `pyramid`. Default `auto`. No comparison value. |
| `Depth` | `overview`, `with-code`, `with-code-parallels`. Default `with-code`. |
| `Theme` | `light`, `dark`. Default `light`. Valid with every type and every interactivity. |
| `Audience` | `beginner`, `intermediate`. Default `beginner`. |
| `DomainOption`, `GoalOption`, `InteractivityOption`, `DiagramTypeOption`, `DepthOption`, `ThemeOption`, `AudienceOption` | One chip: enum id, label, wire value, and whether it is the default. |
| `CombinationRule` | One catalog row: match fields, chips to disable, optional forced type, tooltip. |
| `ProfileCatalog` | Defaults, every chip group, and the rules, shared with the browser. |
| `ResolvedProfile` | Profile after the rules, plus disabled-chip tooltips. |
| `ProfileCombinationCatalog` | The rule list. `catalog()` publishes it. `resolve(PreProfile)` forces a type, then collects disables. Theme has no row. |

### Theme tokens

| Class | What it does |
| --- | --- |
| `dev.diagrammer.theme.DiagramThemeTokens` | Reads `src/main/resources/META-INF/resources/diagram-theme.css`. `stylesheet()` is that text for the renderer to inline. `cssClass(Theme)` is `diagram-theme-light` or `diagram-theme-dark`. `tokens(Theme)` is the custom properties for one palette. |

## Hilla endpoints

Both types use `@BrowserCallable`. Methods are `@PermitAll` (any logged-in user). Neither is `@AnonymousAllowed`, so an anonymous call is rejected. There is no `@Endpoint` class.

| Endpoint | Method | What it does |
| --- | --- | --- |
| `CurrentUserEndpoint` | `getCurrentUser()` | Returns `CurrentUser` (`userId`, `username`, `displayName`, `roles`) for the logged-in user. |
| `ProfileEndpoint` | `catalog()` | Returns `ProfileCombinationCatalog.catalog()`. Does not validate, store, or generate. |

`CurrentUser` is the record those fields travel in. It is not an endpoint.

## React views and components

File routes from `src/main/frontend/views/`. `routes.tsx` sends anonymous users to `/login`.

| Route | File | What it does |
| --- | --- | --- |
| `/login` | `views/login.tsx` | Login overlay. Skips the app layout. Calls `useAuth().login`. |
| `/` | `views/@index.tsx` | Diagram Orchestrator. Login required. Chip intake, including the Theme chip after Depth and outside Advanced, a live Pre-Profile card, a theme preview card, disabled Confirm & Generate, and a profile-assistant placeholder. |
| layout | `views/@layout.tsx` | App shell: brand, display name, Log out, and the view outlet. |

| File | What it does |
| --- | --- |
| `index.tsx` | Mounts `AuthProvider` and the router on `#outlet`. |
| `index.html` | Shell document. Sets `theme="light"` on `<html>`. |
| `auth.ts` | Hilla auth bound to `CurrentUserEndpoint.getCurrentUser`. Exports `useAuth` and `AuthProvider`. |
| `routes.tsx` | Builds the file router and protects routes with `/login`. |
| `profile/ChipGroup.tsx` | Single-select radio chips. A disabled chip keeps a tooltip. |
| `profile/applyCatalog.ts` | Browser copy of `ProfileCombinationCatalog.resolve`. |
| `META-INF/resources/styles.css` | Layout for the app shell, chips, Pre-Profile card, theme preview, and assistant panel. |
| `src/main/resources/META-INF/resources/diagram-theme.css` | The only copy of the light and dark diagram palettes. Custom properties on `.diagram-theme-light` and `.diagram-theme-dark`. |

`PreProfileCard` and `ThemePreview` are functions inside `views/@index.tsx`, not their own files. Title and topic render as `not set yet`. `ThemePreview` shows a title, a step chip, a code line, and an accent button in the selected palette.

## Config and profiles

No Spring profile is the default.

| File | What it does |
| --- | --- |
| `src/main/resources/application.properties` | Port `${PORT:8080}`. Excludes `/diagrams/**` and `/actuator/health` from Vaadin. Exposes only health, with no details. |
| `src/main/resources/application-local.properties` | Marker for the `local` profile. The fixture path is in `FixtureUserStore`, not in this file. |
| `src/main/resources/application-s3.properties` | Comments only. Names the environment variables. No credential properties. |
| `src/main/resources/application-test.properties` | Turns off the Vaadin banner and browser launch for the `test` profile. |
| `src/main/resources/fixture/users.json` | Fixture credentials for `local` and `test`. |
| `src/main/resources/META-INF/spring.factories` | Registers `UserStoreProfileGuard`. |
| `pom.xml` | Maven profile `dev` sets `spring-boot.run.profiles=local` for `spring-boot:run` only. |
| `Dockerfile` | Multi-stage image. Runtime default `SPRING_PROFILES_ACTIVE=s3`, listens on `0.0.0.0`, Java 21 JRE, non-root user. |
| `docker/entrypoint.sh` | Refuses Spring profiles `local` and `test`, then runs the jar. |
| `vite.config.ts` | Custom Vite config is empty. It calls `overrideVaadinConfig` from `vite.generated`, which the Vaadin build writes and which is not in git. |
| [`render.yaml`](../render.yaml) | Render web service `diagrammer`: Docker, `main`, Frankfurt, free plan, `healthCheckPath=/actuator/health`, `SPRING_PROFILES_ACTIVE=s3`. |

`render.yaml` `buildFilter` matches [0032](decisions/devops/0032-render-deploys-only-for-app-changes.md) and [0037](decisions/devops/0037-render-deploys-via-the-github-app.md): paths `diagrammer-code-project/**`, ignoredPaths `diagrammer-code-project/**/*.md`. The file also sets `autoDeploy: true` and `autoDeployTrigger: commit`. That push trigger is expected and not yet proven ([0037](decisions/devops/0037-render-deploys-via-the-github-app.md)).

Environment variable names only (values are not in git):

| Name | Where it is read |
| --- | --- |
| `PORT` | `server.port` in `application.properties`. |
| `SPRING_PROFILES_ACTIVE` | Selects `local`, `test`, or `s3`. The image and `render.yaml` set `s3`. |
| `DIAGRAMMER_S3_BUCKET` | Bucket for the `s3` profile. With `AWS_REGION` and no profile, startup activates `s3`. |
| `AWS_REGION` | Region for the S3 client. |
| `AWS_ACCESS_KEY_ID` | SDK default credential chain. Not read by application code. |
| `AWS_SECRET_ACCESS_KEY` | SDK default credential chain. Not read by application code. |

## Tests

Backend tests are JUnit under `src/test/java/`. There is no frontend test runner and no `*.test.ts` / `*.spec.tsx`. The browser smoke test is Playwright started from Java.

| Test | What it covers |
| --- | --- |
| `LoginSmokeTest` | S1 HTTP smoke on the `test` profile. Anonymous `/` redirects to `/login`. Fixture login returns the current user. A wrong password stays logged out (401). Logout returns to `/login`, and the endpoint then returns 401. `GET /diagrams/anything` is 404 `Diagram not found` without login. |
| `HealthEndpointTest` | `GET /actuator/health` is public and hides details. Other actuator paths are not exposed. `/` still requires login. |
| `SignedInBrowser` | Test helper in `src/test/java/dev/diagrammer/SignedInBrowser.java`. Posts the fixture login and copies the session cookie into a Playwright context, so a feature test opens already signed in. |
| `OrchestratorSmokeTest` | S2 and S15 browser smoke. Starts already signed in through `SignedInBrowser`, then checks default chips including Theme Light, the Pre-Profile card, chip updates, Dark updating the card and the preview, v1 types only, pyramid versus multi-scenario, and compare staying on auto. Anonymous `catalog()` is rejected. The app shell stays light. |
| `ProfileCombinationCatalogTest` | Defaults, chip order and wire values, no comparison type, unique rule ids, static and step-by-step with every type, pyramid/multi-scenario, compare forces auto, other goals do not, and theme stays for every type and interactivity. |
| `PreProfileTest` | `PreProfile` serializes `theme` as `LIGHT` or `DARK`. |
| `DiagramThemeTokensTest` | Light and dark define the same custom-property keys, and the colors match `diagram-theme.css`. |
| `S3UserStoreTest` | Reads `auth/users.json`. Rejects a non-array file, a password without `{noop}`, and a user missing `displayName`. |
| `UserStoragePrefixTest` | `users/{userId}/`, the logged-in user, and a user id that could escape the folder. |
| `UserStoreProfileGuardTest` | No profile and no S3 env fails closed. Bucket without region does not activate `s3`. Both env vars activate `s3`. An explicit `local` profile stays on the fixture. |

## Not in the code yet

Decided in [ARCHITECTURE.md](ARCHITECTURE.md), and absent from this tree:

- Backend Pre-Profile validation is planned, not yet implemented (no story assigned yet). It is not part of v0.0.3, which is only the Theme chip, light/dark tokens and the preview card.
- The auto-layout suggestion (explain-flow → horizontal steps, use-case → U-shape, and layers → pyramid) is planned, not yet implemented (no story assigned yet). It is not part of v0.0.3, which is only the Theme chip, light/dark tokens and the preview card.
- Profile-assistant chat. The orchestrator shows the text "Profile assistant coming soon".
- Confirm & Generate. The button is disabled.
- Planner, DiagramSpec, template renderer, and opt-in LLM HTML.
- Bedrock client and `AiClient` (chat, JSON planner, diagrammer/renderer).
- Storing diagram HTML, Pre-Profile JSON, or DiagramSpec JSON in S3. S3 is used for `auth/users.json` only. `UserStoragePrefix` builds `users/{userId}/` and nothing under it is written.
- Public diagram pages. `/diagrams/**` is a 404 placeholder.
- Content-Security-Policy, HTML sanitizing, and a sandboxed iframe preview.
- Generated pages that apply the selected palette. The Theme chip and `ThemePreview` are in `views/@index.tsx`. The shell document stays `theme="light"` ([0035](decisions/front/0035-themes-only-on-generated-diagrams.md)).
