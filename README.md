# sample-app — Guess the Number

A small browser game served by a plain Java HTTP server, with Maven build, JUnit tests, Docker packaging, and a GitHub Actions pipeline that builds, tests, and pushes to Docker Hub.

The server picks a secret number between 1 and 100; the UI gives higher/lower hints, narrows the remaining range, tracks attempts, and remembers your best score.

## Endpoints

| Path | Response |
| --- | --- |
| `/` | Game UI (HTML) |
| `POST /api/new` | `{"id":"<uuid>","max":100}` |
| `/api/guess?id=&value=` | `{"result":"TOO_LOW｜TOO_HIGH｜CORRECT","attempts":n,"solved":bool}` |
| `/health` | `{"status":"UP"}` |

## Build and test locally

```bash
mvn clean verify
java -jar target/sample-app.jar
```

## Run with Docker

```bash
docker build -t sample-app:local .
docker run --rm -p 8080:8080 sample-app:local
```

Then open http://localhost:8080 and play.

Or with Compose:

```bash
docker compose up --build
```

## Git + GitHub

```bash
git init -b main
git add .
git commit -m "Initial commit: Java sample app with Docker and CI"
git remote add origin https://github.com/<user>/sample-app.git
git push -u origin main
```

## Docker Hub push from CI

Add these repository secrets in GitHub (Settings → Secrets and variables → Actions):

- `DOCKERHUB_USERNAME` — your Docker Hub username
- `DOCKERHUB_TOKEN` — a Docker Hub access token (Account Settings → Security)

Pushes to `main` publish `<user>/sample-app:latest` and `<user>/sample-app:<sha>`.

## Run the published image

```bash
docker run --rm -p 8080:8080 <user>/sample-app:latest
```
