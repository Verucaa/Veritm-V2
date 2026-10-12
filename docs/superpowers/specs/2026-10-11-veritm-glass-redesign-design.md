# VERITM "Frosted Glass" — Redesign Design Spec

## Goal
Give VERITM its own visual identity so it no longer looks like Metrolist. Introduce a
lightweight "frosted glass" style (faux glass — semi-transparent fill + top sheen + hairline
border, **no real blur**), dynamic color taken from the currently playing album cover, a
custom equalizer loader, lyrics whose active line sits exactly in the vertical center, and a
smooth fade+slide animation when switching bottom-bar tabs.

Add a theme choice in Settings: **Glassmorphism** (new) and **Default** (the previous
non-glass look). On a fresh install, **Glassmorphism is active by default**.

## Scope
Screens touched: Home, Library, Player, bottom-bar navigation, bottom-sheet menus, loader,
lyrics. "Other pages" inherit the glass surfaces through shared components.

## Non-goals
- No real backdrop blur (no `Modifier.blur`, no Haze dependency).
- No change to navigation destinations, database schema, or playback logic.
- No version bump.
- No new third-party dependency.

## Constraints
- Kotlin / Jetpack Compose, `minSdk 26`, `compileSdk 37`, AGP 9.4.1, JDK 21.
- Follow Compose best practices; keep 60fps, avoid per-frame allocations in scroll paths.
- All new strings go in `app/src/main/res/values/veritm_strings.xml` (English only).
- Keep existing user options (`useNewPlayerDesign`, player background styles, palette picker).

## Decisions (from brainstorming)
| Topic | Decision |
|---|---|
| Visual | Frosted Glass |
| Glass strength | Faux glass, no blur — applied to card containers, bottom bar, sheet menus, loader, active lyric pill |
| Identity color | Dynamic, extracted from current album cover; fallback green `#1DB954` when nothing is playing |
| Tab animation | Fade + slight slide (in from destination side) |
| Loader | Equalizer bars (music-themed) |
| Active lyric | Exactly vertical center (anchor 0.5), manual drag kept |
| Theme choice | `Glassmorphism` (default) vs `Default` (old flat look) |

## Design system

### New file: `ui/theme/Glass.kt`
Tokens (`GlassTokens` data class, provided via `CompositionLocalProvider`):
- `glassFill` — translucent surface fill (dark: white ~0.10–0.14 alpha; light: white ~0.55).
- `glassFillStrong` — slightly more opaque fill for the bottom bar / sheet.
- `glassBorder` — hairline stroke, white ~0.12–0.20 alpha, 1dp.
- `glassSheen` — top-edge vertical gradient white `0.22f -> 0.0f` (the "glass sheen").
- `tint` — current dynamic accent color (from album cover or fallback green).

Primitives:
- `Modifier.glassSurface(shape: Shape, strong: Boolean = false)` — draws fill, top sheen,
  and 1dp border within `shape`. Pure Canvas/gradient, no blur.
- `GlassCard(...)` — thin wrapper over `Card`/`Surface` applying `glassSurface` + existing
  corner radii. Used by card containers, never by list/grid thumbnails.
- `AppBackdrop(colors)` — full-screen vertical gradient built from the two dynamic colors;
  fallback = green→dark. Drawn behind screen content (in `MainActivity` scaffold).

### Dynamic color (reuse existing plumbing)
- MainActivity already extracts the album-cover color into `themeColor` and feeds it to
  `VeritmTheme` (`MainActivity.kt` L624-680, gated by `DynamicThemeKey`). No new extractor.
- Glass tint = `MaterialTheme.colorScheme.primary` (green by default; album color when dynamic
  is on). `AppBackdrop` derives its gradient from `colorScheme.primary`/`tertiary`.
- To make a fresh install show cover colors while glass is on, derive
  `effectiveDynamicTheme = GlassmorphismKey.value || DynamicThemeKey.value` and use it in place
  of `enableDynamicTheme` for the existing extraction `LaunchedEffect`.
- Fallback is automatic: when no song playing, `themeColor` = `selectedThemeColor` (green).

### Preference: `constants/PreferenceKeys.kt`
- `GlassmorphismKey: Boolean = true` (default `true` → fresh install shows glass).
- Toggle in `ui/screens/settings/AppearanceSettings.kt`, presented as a two-way choice
  (segmented choice): **Glassmorphism** / **Default**, with a short description.
- When `false`: all glass primitives render the previous flat surfaces; `AppBackdrop` is not
  drawn; dynamic cover color is ignored and the existing theme palette is used.

## Per-screen changes

### Bottom bar (`ui/component/AppNavigation.kt`, `MainActivity.kt`)
- `AppNavigationBar` / `AppNavigationRail` use a glass container when glass is on
  (translucent `glassFillStrong` + hairline top border + sheen), else existing `surfaceContainer`.
- Active item gets a subtle scale animation (spring) on selection.

### Tab transition (`MainActivity.kt` NavHost ~L1351-1390)
- Replace the current `slideInHorizontally { it/8 } + fadeIn` with a tuned fade + slide from
  the destination side (direction by tab index). Duration ~220–260ms `FastOutSlowInEasing`.
- Keep index-based `routeIndexMap` logic intact.

### Home (`ui/screens/HomeScreen.kt`)
- `AppBackdrop` behind content.
- Card containers become `GlassCard`: `CommunityPlaylistCard` (~L207), `DailyDiscoverCard`
  (~L506), Wrapped card, `SpeedDialGridItem`, and section `LazyRow` item wrappers where a
  container card exists. Thumbnails inside stay normal rounded images.
- Section title chrome restyled for glass backdrop (contrast-safe text).
- Horizontal content padding left as-is per row.

### Library (`ui/screens/library/*`, `ui/component/Library.kt`, `ChipsRow.kt`)
- `ChipsRow`/`ChoiceChipsRow` become glass pills (translucent + border), selected = tinted.
- `LibraryAlbum*Item`, `LibraryArtist*Item`, `LibraryPlaylist*Item` containers use `GlassCard`.
- `LibrarySearchHeader` / `SortHeader` rendered as glass surfaces.

### Player (`ui/player/Player.kt`, `ui/theme/PlayerSliderColors.kt`)
- Control buttons (share/fav/fullscreen, play/pause) become glass circles.
- Slider track/fill uses tint from dynamic color; wavy/slim variants keep working.
- Thumbnail gets a glass frame (border + sheen), both old and `useNewPlayerDesign` layouts.
- Background styles (DEFAULT/GRADIENT/BLUR) untouched.

### Menus (`ui/component/Menu.kt`, `NewMenuComponents.kt`, `BottomSheetMenu.kt`)
- `BottomSheetMenu` container = glass surface; drag handle restyled.
- `Material3MenuItemRow` / `Material3MenuGroup` rows = glass fill + border.
- `NewActionGrid` / `NewActionButton` tiles = glass.

### Loader — new `ui/component/EqualizerLoader.kt`
- `EqualizerLoader(size, color)`: animated bars using `rememberInfiniteTransition` + per-bar
  phase (staggered), color = tint. Replaces `CircularProgressIndicator` at the prominent
  loading sites: app/playlist loaders (`ui/menu/LoadingScreen.kt`), `ContainedLoadingIndicator`
  call sites (Home, Player inline lyrics, Album, Online Playlist/Podcast), changelog.
- Falls back to a still compact size that fits existing 24–48dp slots.

### Lyrics (`ui/component/ExperimentalLyrics.kt`, `LyricsLine.kt`)
- Change `LYRICS_ANCHOR_RATIO` 0.35f -> 0.5f so the active line sits in the vertical center.
- Preserve `userManualOffset` drag, clamping, and auto-scroll reset logic.
- Active line gets a glass highlight pill (`glassSurface`, tinted) behind the text.
- `Inset`/padding around the lyric area may need adjustment so first/last lines still scroll
  into center (top/bottom content padding).

## Phasing (each phase must compile)
1. **Foundation**: `Glass.kt`, `GlassColors.kt`, `GlassmorphismKey` + `AppearanceSettings`
   toggle + strings, `AppBackdrop`, apply to bottom bar + Home + cards, tab animation.
2. **Library + Player + Menus**: chips, library cards, player controls/slider/thumbnail, sheet
   menu + action grid.
3. **Loader + Lyrics**: `EqualizerLoader` + replacements, lyrics anchor/center + active pill.

## Files (primary)
- New: `ui/theme/Glass.kt`, `ui/component/EqualizerLoader.kt`.
- Edit: `ui/theme/Theme.kt`, `constants/PreferenceKeys.kt`,
  `ui/screens/settings/AppearanceSettings.kt`, `MainActivity.kt`, `ui/component/AppNavigation.kt`,
  `ui/screens/HomeScreen.kt`, `ui/component/Items.kt`, `ui/screens/library/*`,
  `ui/component/Library.kt`, `ui/component/ChipsRow.kt`, `ui/player/Player.kt`,
  `ui/theme/PlayerSliderColors.kt`, `ui/menu/*`, `ui/component/Menu.kt`,
  `ui/component/NewMenuComponents.kt`, `ui/component/BottomSheetMenu.kt`,
  `ui/menu/LoadingScreen.kt`, `ui/component/ExperimentalLyrics.kt`, `ui/component/LyricsLine.kt`,
  `res/values/veritm_strings.xml`.

## Risks
- Over-glassing looks busy → restrict to container surfaces, never thumbnails.
- Dynamic color must always have the green fallback (no song, extraction failure, grayscale art).
- Tab animation is index-based (`routeIndexMap`) — direction mapping must stay correct.
- Lyrics centering must not break drag/clamp/auto-scroll reset; top/bottom insets must allow
  first/last lines to reach center.
- Two player layouts (`useNewPlayerDesign` on/off) must both be handled.
- `Default` theme must be pixel-close to the previous look.

## Verification
- Static: new strings only in `veritm_strings.xml`; no new dependency; no `Modifier.blur` added.
- Compile: `./gradlew :app:assembleFossDebug` must succeed (CI is the full check; local has no SDK).
- Manual (human): toggle Glassmorphism/Default; fresh-install default = glass; tab animation;
  loader visible; lyrics active line centered; dynamic color follows album cover then falls
  back to green.
