# Diagrammer application

This folder is the Maven/Hilla project root. Run every command below from here, not from the repository root.

Release v0.0.1 is story S1: log in, see your display name, log out, and land on an empty Diagram Orchestrator shell. The public `/diagrams/**` route is a placeholder.

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

That password is a local fixture, not a production secret. After login the header shows **Albert** and **Log out**, and the page is the empty Diagram Orchestrator shell.

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
