# CI / build workflows

Two manual (`workflow_dispatch`) workflows. Run them from the **Actions** tab →
pick the workflow → **Run workflow**.

## `build-all.yml` — Build All (server + clients)

Builds the whole stack on demand and uploads each output as an artifact. Every
job always uploads whatever it produced, so a failure in one target still
surfaces its build log without blocking the rest.

| Job | Output artifact | Notes |
|-----|-----------------|-------|
| `server`  | `server-jar`      | Spring Boot `bootJar`, run with `java -jar` |
| `android` | `android-apk`     | debug APK |
| `desktop` | `desktop-jar`     | runnable uber-jar for the runner OS (Linux) |
| `web`     | `web-dist`        | Compose/JS static site, zipped |
| `ios`     | `ios-framework`   | shared framework (device + simulator) + best-effort unsigned simulator app |

**Inputs**

- `base_url` — API base URL baked into the **client** builds (android/desktop/web/ios)
  so the app talks to that backend with no source edits. Empty keeps each
  platform's default. Example: a `serve-live` tunnel URL, or your Liara server.
- `server_ref` — server repo branch/ref to build (default `develop`).
- `server` / `android` / `desktop` / `web` / `ios` — toggle each target.

The server job runs first; the client jobs wait for it (so its errors show up
first) but still build regardless of the server result.

## `serve-live.yml` — Serve Live (server + web)

Spins up a full temporary live environment:

1. builds & runs the Spring Boot server + PostgreSQL,
2. exposes the API on a Cloudflare tunnel (public URL),
3. builds Compose-Web **with that API URL baked in**,
4. serves the web on a second tunnel,
5. prints one **FINAL LINK** to open the app against the live API.

Everything is temporary and the URLs change each run. They are also written to
the **`claude/live-url`** branch (`live-url.txt`) as the run progresses, so you
can grab the API/Swagger link the moment the tunnels are up — before the web
build finishes.

**Inputs**: `minutes` (uptime, max 350), `server_ref`, `seed_enabled`.

## Optional secret: `CROSS_REPO_TOKEN`

Both workflows check out the server repo (`puriakazemieh/Rasteh_Kotlin_Spring_Boot`).
If that repo is **private**, add a Personal Access Token with repo read access as
a secret named `CROSS_REPO_TOKEN`. If it is public (or the run token already has
access) nothing is needed — the run token is used as a fallback.

## How the API URL is injected

`.github/scripts/bake-base-url.sh <sourceSet> <url>` rewrites the single active
`actual val baseUrl = "…"` line in the matching `PlatformConfig.<platform>.kt`
before the build. Commented alternatives are left untouched.
