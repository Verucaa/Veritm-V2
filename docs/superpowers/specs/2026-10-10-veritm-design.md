# VERITM — Design Spec

## Goal
Fork Metrolist into a new app **VERITM** at `/root/VERITM-new`: renamed package/identity,
Discord RPC removed, dark Spotify-ish look with color theme choices, easily replaceable
icon, complete README, and CI that builds APK on GitHub Actions without errors.

## Source & constraints
- Base: `/root/Metrolist` (Kotlin/Compose Android, AGP 9.4.1, JDK 21, compileSdk 37, minSdk 26).
- New root package: `com.veritm.music` (app), `com.veritm.innertube` (innertube module).
- applicationId base: `com.veritm.music`. App name string: `VERITM`.
- Version NOT bumped (AGENTS rule).

## Must NOT rename (published third-party deps / attribution)
- `com.metrolist.innertubex`, `.shazamkit`, `.kugou`, `.lastfm`, `.paxsenix`, `.lrclib`
- `com.github.MetrolistGroup.*` (JitPack group), `MetrolistGroup` GitHub org
- License text and "based on Metrolist" attribution

## Rename rules (word-boundary)
- `com.veritm.music` -> `com.veritm.music`
- `com.veritm.innertube` (NOT followed by `x`) -> `com.veritm.innertube`
- Dir moves: `.../com/metrolist/{music,innertube}/*` -> `.../com/veritm/*` (main, test, flavor source sets).
- Identifiers: `VeritmTheme`->`VeritmTheme`, `VeritmWidgetManager`->`VeritmWidgetManager`,
  widget style names `Metrolist*`->`Veritm*`, `Theme.Veritm`->`Theme.Veritm`.
- Resource strings/tokens: `metrolist_strings.xml` -> `veritm_strings.xml` (all locales).
- Gradle: `rootProject.name = "VERITM"`, app config env prefix `METROLIST_` -> `VERITM_`.
- Deep-link schemes containing `metrolist` updated.

## Remove Discord RPC (full)
- Delete `app/src/main/kotlin/com/veritm/music/discord/**` and `app/src/test/.../discord/**`.
- Delete `.../ui/screens/settings/integrations/DiscordSettings.kt`; remove Discord card from
  `IntegrationScreen.kt` (keep LastFM).
- Remove route/import in `NavigationBuilder.kt`.
- Strip all Discord wiring from `MusicService.kt` (fields, init, collectors, `syncDiscordState`,
  `updateDiscordRPC`, `fetchArtistThumbnail`, `onDestroy` calls).
- Remove Discord `PreferenceKeys`.
- Remove `DiscordOAuthActivity` + `metrolistdiscord` scheme from AndroidManifest.
- Remove `DISCORD_APP_ID` buildConfig.
- Remove Discord drawable + strings. Remove Discord community link from AboutScreen.

## Theme / Spotify look (subtle, global)
- Default mode dark; default accent Spotify green `#1DB954`; system-dynamic off by default.
- Keep existing palette picker in `ThemeScreen.kt`.
- Override Material3 `Shapes` in `Theme.kt` to rounder/pill -> whole-app effect.

## Icon (easily replaceable)
- Adaptive icon: simple `Veritm` mark foreground vector + green background color.
- Document replacement steps in README.

## README
Full rewrite: icon, description, structure, install, environment setup, build APK via GitHub
workflow, libraries used, based-on-Metrolist, credits `VeruProject`. Repo URL placeholders.

## CI
- Runner `blacksmith-*-ubuntu-2404` -> `ubuntu-latest`.
- env `METROLIST_*` -> `VERITM_*`.
- Keep flavors foss/gms/izzy; keep gms build in build.yml.

## Verification
- Static: no `com.veritm.music` / `com.veritm.innertube` leftover; no `discord` refs;
  no `Metrolist` identifiers except allowed attribution/external.
- Full APK build runs on GitHub Actions (no local Android SDK).
