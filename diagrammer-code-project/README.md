# Diagrammer application

This folder is the Maven/Hilla project root. Run every command below from here, not from the repository root.

Release v0.0.1 is story S1: log in, see your display name, and log out. Release v0.0.2 is story S2: the Diagram Orchestrator intake, with profile chips and a live Pre-Profile card. Release v0.0.3 is story S15: a Light/Dark theme chip, stored on the Pre-Profile, with a preview of the diagram palette. Release v0.0.4 is stories S3 and S4: a Title field, the Confirm & Generate gate, and a profile-assistant chat. The chat is a scripted assistant. It is the default when `diagrammer.bedrock.enabled` is unset, and it does not call AWS. Confirm & Generate does not generate a diagram yet. The public `/diagrams/**` route is still a placeholder. The app shell stays light.

## What works without AWS

The fixture user store is opt-in. It loads only when you choose the `local` profile (dev) or the `test` profile (smoke tests). It reads `src/main/resources/fixture/users.json` and never calls S3.

These work with no bucket and no AWS credentials:

- `./mvnw spring-boot:run -Pdev` (turns on the `local` profile)
- `SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run`
- `./mvnw test` and `./mvnw verify` (the `test` profile uses the same fixture)

A run with no profile does not load the fixture. `java -jar` with no profile, and `./mvnw spring-boot:run` without `-Pdev` or `SPRING_PROFILES_ACTIVE`, fail at startup and tell you to choose `local` or `s3`. If `DIAGRAMMER_S3_BUCKET` and `AWS_REGION` are both set and no profile is selected, the app uses the S3 user store instead of failing.

The Maven `dev` profile only affects `spring-boot:run`. It is not baked into a packaged jar.

## Prerequisites

- Java 21 (Temurin)
- Node.js 24 LTS, used by the Vaadin frontend build. If it is not on `PATH`, the Vaadin Maven plugin downloads a compatible Node.js the first time it builds the frontend.
- A browser

Maven itself is not required. `./mvnw` is the Maven Wrapper (Maven 3.9.11) checked into this folder.

## Run locally (no AWS)

```bash
cd diagrammer-code-project
./mvnw spring-boot:run -Pdev
```

`-Pdev` sets the Spring profile `local` for this run. The same opt-in without the Maven profile is `SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run`.

Wait until the log says the application started. Then open http://localhost:8080.

The app redirects to the login view. Sign in with the fixture user:

| Field | Value |
| --- | --- |
| Username | `albert` |
| Password | `diagrammer` |
| Display name | Albert |

That password is a local fixture, not a production secret. After login the header shows **Albert** and **Log out**. The Diagram Orchestrator shows a Title field, the profile chips (Domain, Goal, Interactivity, Type, Depth, Theme, and Audience under Advanced), a live Pre-Profile card, a small theme preview in that card, Confirm & Generate, and the profile assistant. The assistant says it is offline and replies from a script. Theme defaults to Light. Dark changes the preview only. Confirm & Generate stays disabled until the title is 3–80 characters and the topic is set. Clicking it then shows that generation arrives in a later release.

`./mvnw -Pdev` is the same one-liner. `spring-boot:run` is the default Maven goal, and `-Pdev` is what turns the fixture on. Without `-Pdev`, that default goal fails closed.

## Smoke tests

```bash
cd diagrammer-code-project
./mvnw test
./mvnw verify
```

Tests use the `test` profile and the classpath fixture. They do not need `DIAGRAMMER_S3_BUCKET`, `AWS_REGION`, or AWS credentials. They check:

- unauthenticated visitors are sent to `/login`
- `albert` / `diagrammer` signs in and the shell shows Albert
- logout returns to the login view
- `CurrentUserEndpoint` returns 401 when nobody is logged in
- `GET /diagrams/example` returns 404 with the text `Diagram not found` and does not require login
- a start with no profile and no S3 environment variables fails before the fixture store is loaded
- the orchestrator opens on the default chips, and the Pre-Profile card matches them, including Theme Light
- changing a chip updates the card, Type offers only the v1 layouts, and Pyramid cannot be combined with Multi-scenario
- picking Dark updates the Pre-Profile card and the theme preview; the app shell stays light
- the Pre-Profile serializes `theme`, and the light and dark palettes define the same CSS custom properties
- the title field gates Confirm & Generate together with the topic, and the click does not generate
- the profile assistant greets, asks once when a topic is vague, maps Goal Other after confirmation, and shows model-shaped HTML as text
- with no Bedrock setting, the only chat client is the scripted assistant

## S3 user store

Profile `s3` reads `auth/users.json` from the bucket. The file is a JSON array of users:

```json
[{"userId":"u1","username":"albert","password":"{noop}...","displayName":"Albert"}]
```

Passwords are plaintext with the `{noop}` prefix. The AWS SDK for Java v2 default credential provider chain resolves the keys. This project does not hardcode keys and does not define custom key properties.

Set these environment variables (names only; do not commit values) and start the `s3` profile:

- `DIAGRAMMER_S3_BUCKET` — bucket name
- `AWS_REGION`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

```bash
cd diagrammer-code-project
SPRING_PROFILES_ACTIVE=s3 ./mvnw spring-boot:run
```

Without those variables, or without `auth/users.json` in the bucket, the `s3` profile cannot log anyone in. For a run with no AWS credentials, use `-Pdev` or `SPRING_PROFILES_ACTIVE=local`.

If no Spring profile is set and both `DIAGRAMMER_S3_BUCKET` and `AWS_REGION` are present, startup activates `s3` on its own. The access key and secret still come from the default credential provider chain.

## Deploy on Render

The Blueprint at the repository root (`render.yaml`) defines one web service. Render builds it with the Docker runtime from `diagrammer-code-project/Dockerfile`, using that directory as the build context, from branch `main`. Auto-deploy is on (`autoDeployTrigger: commit`). The service is in region `frankfurt` on the `free` plan.

`./mvnw package` is the production build. Vaadin 25 does not use a separate Maven `production` profile: that command writes the optimized frontend bundle and leaves the optional `vaadin-dev` tools out of the jar. The runtime image is a Java 21 JRE. It runs as a non-root user, listens on `0.0.0.0:$PORT` (8080 when `PORT` is unset), and caps the heap with `MaxRAMPercentage` so the process can fit a 512 MB instance.

The image sets `SPRING_PROFILES_ACTIVE=s3`. That is the production user store. The Maven `dev` profile only affects `spring-boot:run`, so it is not in the jar. The container entrypoint also refuses the `local` and `test` profiles, so the fixture user (`albert` / `diagrammer`) cannot be turned on in this deploy.

Render checks `GET /actuator/health`. The probe is unauthenticated and returns `{"status":"UP"}` with no component details. Login still protects the rest of the app. `GET /diagrams/**` stays public and still answers 404 until diagrams exist.

Set these in the Render dashboard. The Blueprint declares them with `sync: false`. Do not commit the values.

| Variable | Role |
| --- | --- |
| `DIAGRAMMER_S3_BUCKET` | Private bucket name |
| `AWS_REGION` | `eu-central-1` |
| `AWS_ACCESS_KEY_ID` | IAM user `diagrammer-app` access key |
| `AWS_SECRET_ACCESS_KEY` | IAM user secret |

The Blueprint also sets `SPRING_PROFILES_ACTIVE=s3`. That value is not a secret.

Without `DIAGRAMMER_S3_BUCKET` and `AWS_REGION`, the process exits at startup. It does not load the fixture, and the health endpoint does not come up. With those variables set, the app starts without reading the bucket. S3 is contacted on login, when it reads `auth/users.json`. A dummy key is enough for the process and `/actuator/health` to come up. Login will fail until the bucket holds a real credentials file.
