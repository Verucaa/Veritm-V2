# VERITM "Frosted Glass" Redesign Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Give VERITM its own frosted-glass visual identity (togglable, on by default), with album-cover tinting, an equalizer loader, centered lyrics, and a fade+slide tab animation.

**Architecture:** A single `GlassTokens` value (CompositionLocal) drives a `glassSurface` modifier + `GlassCard` wrapper + `AppBackdrop`. When disabled, the modifier is a no-op and surfaces keep their previous Material3 look. The tint reuses the theme color already extracted from album art in `MainActivity`; no new color extractor.

**Tech Stack:** Kotlin, Jetpack Compose, Material3 (custom), DataStore preferences. Existing shapes in `ui/theme/Theme.kt`.

**Spec:** `docs/superpowers/specs/2026-10-11-veritm-glass-redesign-design.md`

## Global Constraints

- No new third-party dependency; no `Modifier.blur` added anywhere.
- All user-facing strings go only in `app/src/main/res/values/veritm_strings.xml` (English); never other locales.
- Do NOT bump the app version.
- Preserve existing option variants: `useNewPlayerDesign`, player background styles, mini-player styles.
- `GlassmorphismKey` default MUST be `true`.
- Every task must leave the tree compiling (`./gradlew :app:assembleFossDebug`).
- Add comments only for non-obvious logic (one line, `// glass: ...` where needed).

## Review Focus

These are the inputs/conditions most likely to surprise a user; each has a check in its owning task.

1. Fresh install with no stored preference → glass active (default `true`). Task 2.
2. No song playing / grayscale cover / extraction failure → tint falls back to green, text stays readable. Tasks 3, 6.
3. Glassmorphism toggled OFF → surfaces revert to the exact previous look (no residual translucency). Tasks 1, 3.
4. Light mode (`darkTheme == false`) → glass fill/border still legible. Task 1.
5. Tab switch while `navigationItems` excludes `ListenTogether` → slide direction remains correct (uses `routeIndexMap`). Task 5.

---

### Task 1: Glass foundation

**Files:**
- Create: `app/src/main/kotlin/com/veritm/music/ui/theme/Glass.kt`
- Test: `app/src/test/kotlin/com/veritm/music/ui/theme/GlassTokensTest.kt`
- Modify: none

**Interfaces:**
- Produces:
  - `data class GlassTokens(enabled: Boolean, fill: Color, fillStrong: Color, border: Color, sheen: Color)` with `GlassTokens.Companion.Disabled`.
  - `val LocalGlassTokens: ProvidableCompositionLocal<GlassTokens>` (default `GlassTokens.Disabled`).
  - `fun glassTokens(dark: Boolean, accent: Color): GlassTokens` (pure).
  - `fun Modifier.glassSurface(shape: Shape, strong: Boolean = false): Modifier`.
  - `@Composable fun GlassCard(modifier: Modifier = Modifier, shape: Shape = MaterialTheme.shapes.large, strong: Boolean = false, content: @Composable () -> Unit)`.
  - `@Composable fun AppBackdrop(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit)`.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.veritm.music.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GlassTokensTest {
    @Test fun dark_tokens_are_translucent_whites() {
        val t = glassTokens(dark = true, accent = Color(0xFF1DB954))
        assertTrue(t.enabled)
        assertTrue(t.fill.alpha < 0.2f)
        assertTrue(t.border.alpha in 0.05f..0.4f)
    }
    @Test fun disabled_is_fully_transparent() {
        assertEquals(0f, GlassTokens.Disabled.fill.alpha)
        assertEquals(false, GlassTokens.Disabled.enabled)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testFossDebugUnitTest --tests '*GlassTokensTest'`
Expected: FAIL — `glassTokens` / `GlassTokens` unresolved.

- [ ] **Step 3: Implement `Glass.kt`**

Values: dark → fill `Color.White.copy(alpha=0.08f)`, fillStrong `0.12f`, border `White 0.16f`, sheen `White 0.22f`. Light → fill `White 0.45f`, fillStrong `0.60f`, border `Black 0.08f`, sheen `White 0.60f`. `enabled = true`.
`glassSurface`: if `!LocalGlassTokens.current.enabled` return `this` unchanged; else `.background(fill, shape).background(Brush.verticalGradient(listOf(sheen, Color.Transparent)), shape).border(1.dp, border, shape)` using `strong` to pick fillStrong.
`GlassCard`: `Surface(color = Color.Transparent, shape = shape, modifier = modifier.glassSurface(shape, strong)) { content() }`, but when tokens disabled fall back to `Surface(shape = shape)`.
`AppBackdrop`: `Box(modifier.fillMaxSize().background(Brush.verticalGradient(listOf(colorScheme.primary.copy(alpha), colorScheme.surface, colorScheme.tertiary.copy(alpha))))) { content() }`; when tokens disabled draw `colorScheme.surface` only.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testFossDebugUnitTest --tests '*GlassTokensTest'`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/ui/theme/Glass.kt app/src/test/kotlin/com/veritm/music/ui/theme/GlassTokensTest.kt
git commit -m "feat(ui): add glass design foundation (tokens, surface, card, backdrop)"
```

---

### Task 2: Glassmorphism preference + settings toggle

**Files:**
- Modify: `app/src/main/kotlin/com/veritm/music/constants/PreferenceKeys.kt` (add key near `DynamicThemeKey`, L20)
- Modify: `app/src/main/res/values/veritm_strings.xml` (add `enable_glassmorphism`, `enable_glassmorphism_desc`)
- Modify: `app/src/main/kotlin/com/veritm/music/ui/screens/settings/AppearanceSettings.kt` (add item in the `theme` group, near L987-1010)

**Interfaces:**
- Consumes: nothing from Task 1.
- Produces: `val GlassmorphismKey = booleanPreferencesKey("glassmorphism")`, read via `rememberPreference(GlassmorphismKey, defaultValue = true)`.

- [ ] **Step 1: Add the key**

`val GlassmorphismKey = booleanPreferencesKey("glassmorphism")`.

- [ ] **Step 2: Add strings (English only)**

`<string name="enable_glassmorphism">Glassmorphism</string>` and `<string name="enable_glassmorphism_desc">Frosted glass surfaces. Turn off for the flat look.</string>`.

- [ ] **Step 3: Add the settings item**

In `AppearanceSettings.kt`, read `val (glassmorphism, onGlassmorphismChange) = rememberPreference(GlassmorphismKey, defaultValue = true)` and add a `Material3SettingsItem` (icon `R.drawable.palette`, title `enable_glassmorphism`, description `enable_glassmorphism_desc`, trailing `Switch`) mirroring the `enable_dynamic_icon` item (L987-1010).

- [ ] **Step 4: Verify it compiles**

Run: `./gradlew :app:assembleFossDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Review-focus check (fresh install)**

Confirm `defaultValue = true` in both the read site and the item, so a fresh install reports glass ON. No stored value → default used.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/constants/PreferenceKeys.kt app/src/main/res/values/veritm_strings.xml app/src/main/kotlin/com/veritm/music/ui/screens/settings/AppearanceSettings.kt
git commit -m "feat(settings): add Glassmorphism toggle (default on)"
```

---

### Task 3: Wire glass into MainActivity (provider, backdrop, effective dynamic)

**Files:**
- Modify: `app/src/main/kotlin/com/veritm/music/MainActivity.kt` (theme read L600-640; `VeritmTheme` call L682; content root L712-718)

**Interfaces:**
- Consumes: `GlassmorphismKey` (Task 2), `LocalGlassTokens`, `glassTokens`, `AppBackdrop` (Task 1).
- Produces: `LocalGlassTokens` provided around the whole app content; backdrop behind content.

- [ ] **Step 1: Read the preference and derive effective dynamic**

`val (glassmorphism, _) = rememberPreference(GlassmorphismKey, defaultValue = true)`. Replace uses of `enableDynamicTheme` in the two `LaunchedEffect`s (L630-640 and L636) with a derived `val effectiveDynamicTheme = glassmorphism || enableDynamicTheme`.

- [ ] **Step 2: Provide tokens**

Inside `VeritmTheme { ... }`, wrap content:
`CompositionLocalProvider(LocalGlassTokens provides glassTokens(dark = useDarkTheme, accent = MaterialTheme.colorScheme.primary)) { ... }` (merge with the existing `CompositionLocalProvider(LocalDensity ...)` at L712 if convenient).

- [ ] **Step 3: Draw the backdrop**

Replace the root `BoxWithConstraints` background (L713-718) so that when `glassmorphism` is on, `AppBackdrop { ... }` draws the gradient; when off, keep `.background(surface)`.

- [ ] **Step 4: Verify**

Run: `./gradlew :app:assembleFossDebug` → BUILD SUCCESSFUL.
Review-focus: with glass off, background must be plain `surface` (no gradient).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/MainActivity.kt
git commit -m "feat(ui): provide glass tokens, backdrop, and dynamic tint in MainActivity"
```

---

### Task 4: Glass bottom bar + active-icon scale

**Files:**
- Modify: `app/src/main/kotlin/com/veritm/music/ui/component/AppNavigation.kt` (rail L60, bar L142-151, both item blocks)

**Interfaces:**
- Consumes: `Modifier.glassSurface`, `LocalGlassTokens` (Task 1).
- Produces: no new public API.

- [ ] **Step 1: Glass container**

In `AppNavigationBar`, when tokens enabled use `containerColor = Color.Transparent` and add `.glassSurface(shape = RectangleShape, strong = true)` + a top hairline border to the `NavigationBar` modifier; when disabled keep `surfaceContainer`/`pureBlack` behavior. Do the same for `AppNavigationRail`.

- [ ] **Step 2: Animated active icon**

Wrap the item `Icon` in `Modifier.animateContentSize()` and scale the selected icon via `val scale by animateFloatAsState(if (isSelected) 1.15f else 1f)` + `Modifier.graphicsLayer { scaleX = scale; scaleY = scale }` (both bar and rail).

- [ ] **Step 3: Verify**

Run: `./gradlew :app:assembleFossDebug` → BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/ui/component/AppNavigation.kt
git commit -m "feat(ui): glass bottom bar with animated active icon"
```

---

### Task 5: Fade + slide tab transition

**Files:**
- Modify: `app/src/main/kotlin/com/veritm/music/MainActivity.kt` (NavHost transitions L1343-1400; uses `routeIndexMap` L750)

**Interfaces:**
- Consumes: `routeIndexMap` (existing).
- Produces: no new API.

- [ ] **Step 1: Compute direction from index**

Derive `val from = routeIndexMap[targetState.route] ?: 0` and `val to = routeIndexMap[initialState.route] ?: 0` (guard `null` → treat as 0) and `val forward = to >= from`.

- [ ] **Step 2: Set enter/exit**

`enterTransition = { slideInHorizontally { w -> if (forward) w / 6 else -w / 6 } + fadeIn(tween(240, easing = FastOutSlowInEasing)) }`, `exitTransition = { slideOutHorizontally { w -> if (forward) -w / 6 else w / 6 } + fadeOut(tween(180)) }`, plus matching `popEnterTransition`/`popExitTransition` with reversed sign.

- [ ] **Step 3: Verify**

Run: `./gradlew :app:assembleFossDebug` → BUILD SUCCESSFUL.
Review-focus: temporarily hide ListenTogether in the top bar (setting) and confirm tab order/slide direction still correct (list is dynamic). If a route is missing from the map, direction defaults to forward (no crash).

- [ ] **Step 4: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/MainActivity.kt
git commit -m "feat(ui): fade+slide transition when switching bottom-bar tabs"
```

---

### Task 6: Home glass cards

**Files:**
- Modify: `app/src/main/kotlin/com/veritm/music/ui/screens/HomeScreen.kt` (`CommunityPlaylistCard` ~L207, `DailyDiscoverCard` ~L506, Wrapped ~L1376; optional `SpeedDialGridItem` ~L1586)

**Interfaces:**
- Consumes: `GlassCard`, `glassSurface` (Task 1).

- [ ] **Step 1: Convert feature cards**

Replace the Material3 `Card`/`Surface` containers of `CommunityPlaylistCard`, `DailyDiscoverCard`, and the Wrapped card with `GlassCard(shape = RoundedCornerShape(28.dp), strong = true)` keeping their existing padding/content. Keep thumbnail/image content unchanged.

- [ ] **Step 2: Contrast-safe headers**

For section titles drawn over the backdrop, ensure color is `MaterialTheme.colorScheme.onSurface` (already default) and avoid hard-coded low-contrast colors. No change if already correct.

- [ ] **Step 3: Verify**

Run: `./gradlew :app:assembleFossDebug` → BUILD SUCCESSFUL.
Review-focus: with no song playing the backdrop is green-tinted; card text must remain readable.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/ui/screens/HomeScreen.kt
git commit -m "feat(ui): glass cards on home"
```

---

### Task 7: Library glass (chips, cards, headers)

**Files:**
- Modify: `app/src/main/kotlin/com/veritm/music/ui/component/ChipsRow.kt` (`ChipsRow` L59, `ChoiceChipsRow` L94)
- Modify: `app/src/main/kotlin/com/veritm/music/ui/component/Library.kt` (card containers L67, L136, L239)
- Modify: `app/src/main/kotlin/com/veritm/music/ui/component/LibrarySearchHeader.kt`, `SortHeader.kt`

**Interfaces:**
- Consumes: `glassSurface`, `GlassCard` (Task 1).

- [ ] **Step 1: Glass chips**

In `ChipsRow`/`ChoiceChipsRow`, wrap chip content in `Modifier.glassSurface(shape = if (selected) MaterialTheme.shapes.small else CircleShape)`, keeping selected = tinted (`primary`/`secondaryContainer`) and unselected = translucent.

- [ ] **Step 2: Glass library cards**

In `Library.kt` grid items that use a container, apply `GlassCard`/`glassSurface` (rounded) while leaving thumbnails as-is.

- [ ] **Step 3: Glass headers**

Wrap `LibrarySearchHeader` and `SortHeader` rows in `glassSurface` (pill/medium shape).

- [ ] **Step 4: Verify**

Run: `./gradlew :app:assembleFossDebug` → BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/ui/component/ChipsRow.kt app/src/main/kotlin/com/veritm/music/ui/component/Library.kt app/src/main/kotlin/com/veritm/music/ui/component/LibrarySearchHeader.kt app/src/main/kotlin/com/veritm/music/ui/component/SortHeader.kt
git commit -m "feat(ui): glass library chips, cards, and headers"
```

---

### Task 8: Player glass controls

**Files:**
- Modify: `app/src/main/kotlin/com/veritm/music/ui/player/Player.kt` (buttons L1128-1144, controls L1526+; both `useNewPlayerDesign` variants)
- Modify: `app/src/main/kotlin/com/veritm/music/ui/theme/PlayerSliderColors.kt` (L21-40)

**Interfaces:**
- Consumes: `glassSurface`, `LocalGlassTokens` (Task 1).

- [ ] **Step 1: Glass control buttons**

Apply `glassSurface(shape = CircleShape)` (or the existing `shareShape`/`favShape`) to the share/fav/fullscreen buttons and the play/pause button in both the new and legacy layout branches. Do not change their click handlers.

- [ ] **Step 2: Slider tint**

In `PlayerSliderColors.getSliderColors`, when glass is enabled, keep the active fill `primary` (cover tinted) and the inactive track translucent; do not alter the wavy/slim call sites' signatures.

- [ ] **Step 3: Verify**

Run: `./gradlew :app:assembleFossDebug` → BUILD SUCCESSFUL.
Review-focus: toggle `useNewPlayerDesign` off and confirm both layouts render glass without overlap.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/ui/player/Player.kt app/src/main/kotlin/com/veritm/music/ui/theme/PlayerSliderColors.kt
git commit -m "feat(ui): glass player controls and slider tint"
```

---

### Task 9: Glass menus (sheet, rows, action grid)

**Files:**
- Modify: `app/src/main/kotlin/com/veritm/music/ui/component/BottomSheetMenu.kt` (sheet L121, drag handle L138-146)
- Modify: `app/src/main/kotlin/com/veritm/music/ui/component/Menu.kt` (`Material3MenuGroup` L30, item rows L62)
- Modify: `app/src/main/kotlin/com/veritm/music/ui/component/NewMenuComponents.kt` (`NewActionButton` L40, `NewActionGrid` L113)

**Interfaces:**
- Consumes: `glassSurface` (Task 1).

- [ ] **Step 1: Glass sheet**

Set the `ModalBottomSheet` container color to translucent when glass enabled and add a subtle top sheen to the content column; keep `shape`/drag handle.

- [ ] **Step 2: Glass rows and tiles**

Apply `glassSurface` to `Material3MenuItemRow` backgrounds and to `NewActionButton` tiles (`RoundedCornerShape(16.dp)`), keeping selected/active states tinted.

- [ ] **Step 3: Verify**

Run: `./gradlew :app:assembleFossDebug` → BUILD SUCCESSFUL.
Review-focus: open a song menu and a player menu from a dark and a light theme.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/ui/component/BottomSheetMenu.kt app/src/main/kotlin/com/veritm/music/ui/component/Menu.kt app/src/main/kotlin/com/veritm/music/ui/component/NewMenuComponents.kt
git commit -m "feat(ui): glass bottom-sheet menus and action tiles"
```

---

### Task 10: Equalizer loader

**Files:**
- Create: `app/src/main/kotlin/com/veritm/music/ui/component/EqualizerLoader.kt`
- Test: `app/src/test/kotlin/com/veritm/music/ui/component/EqualizerLoaderTest.kt`
- Modify: `app/src/main/kotlin/com/veritm/music/ui/menu/LoadingScreen.kt` (L92), and the primary `ContainedLoadingIndicator` call sites in `HomeScreen.kt` (~L1430), `Player.kt` (~L2107).

**Interfaces:**
- Produces:
  - `fun barFraction(index: Int, barCount: Int, phase: Float): Float` (pure, returns `0.2f..1.0f`).
  - `@Composable fun EqualizerLoader(modifier: Modifier = Modifier, barCount: Int = 4, color: Color = MaterialTheme.colorScheme.primary)`.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.veritm.music.ui.component

import kotlin.test.Test
import kotlin.test.assertTrue

class EqualizerLoaderTest {
    @Test fun fraction_stays_in_range_for_all_phases() {
        for (i in 0 until 4) {
            var p = 0f
            while (p < 1f) {
                val f = barFraction(i, 4, p)
                assertTrue(f in 0.2f..1.0f, "fraction $f out of range at $p")
                p += 0.05f
            }
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testFossDebugUnitTest --tests '*EqualizerLoaderTest'`
Expected: FAIL — `barFraction` unresolved.

- [ ] **Step 3: Implement `EqualizerLoader.kt`**

`barFraction(i, n, phase) = 0.2f + 0.8f * ((sin(2π * (phase - i / n.toFloat())) + 1f) / 2f)`. `EqualizerLoader` uses `rememberInfiniteTransition` animating `phase` 0..1 over `1200ms` linear and draws `barCount` rounded bars (width `size/ (barCount*2 -1)`), height `size * barFraction(...)`, centered vertically.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testFossDebugUnitTest --tests '*EqualizerLoaderTest'`
Expected: PASS.

- [ ] **Step 5: Replace primary loaders**

Swap `ContainedLoadingIndicator` at the HomeScreen/Player call sites and the `LoadingScreen.kt` indicator with `EqualizerLoader` sized to fit the same slot (no layout change beyond the indicator).

- [ ] **Step 6: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/ui/component/EqualizerLoader.kt app/src/test/kotlin/com/veritm/music/ui/component/EqualizerLoaderTest.kt app/src/main/kotlin/com/veritm/music/ui/menu/LoadingScreen.kt app/src/main/kotlin/com/veritm/music/ui/screens/HomeScreen.kt app/src/main/kotlin/com/veritm/music/ui/player/Player.kt
git commit -m "feat(ui): custom equalizer loader"
```

---

### Task 11: Center active lyric + glass active pill

**Files:**
- Modify: `app/src/main/kotlin/com/veritm/music/ui/component/ExperimentalLyrics.kt` (`LYRICS_ANCHOR_RATIO` L133, offset math L722-743)
- Modify: `app/src/main/kotlin/com/veritm/music/ui/component/LyricsLine.kt` (`LyricsLine` L122)

**Interfaces:**
- Consumes: `glassSurface` (Task 1).

- [ ] **Step 1: Center the anchor**

Change `LYRICS_ANCHOR_RATIO` from `0.35f` to `0.5f`. Verify top/bottom insets still let the first and last line reach center (adjust the container's top/bottom content padding proportionally if not). Preserve `userManualOffset`, clamping, and auto-scroll reset logic untouched.

- [ ] **Step 2: Glass active pill**

In `LyricsLine`, when the line is active and glass is enabled, draw a `glassSurface` rounded background behind the text using the tint; when disabled, keep existing behavior.

- [ ] **Step 3: Verify**

Run: `./gradlew :app:assembleFossDebug` → BUILD SUCCESSFUL.
Review-focus: drag the lyrics, release, and confirm auto-scroll returns the active line to center; confirm the last line can still scroll to center.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/kotlin/com/veritm/music/ui/component/ExperimentalLyrics.kt app/src/main/kotlin/com/veritm/music/ui/component/LyricsLine.kt
git commit -m "feat(lyrics): center active line and add glass pill"
```

---

## Self-Review Notes

- **Spec coverage:** glass system (T1), toggle + fresh-install default (T2), dynamic tint/backdrop (T3), bottom bar (T4), tab animation (T5), home (T6), library (T7), player (T8), menus (T9), loader (T10), lyrics (T11). "Other pages" inherit via shared components (T4/T9).
- **Type consistency:** `glassSurface(shape, strong)`, `GlassCard(modifier, shape, strong, content)`, `AppBackdrop(modifier, content)`, `barFraction(index, barCount, phase)` are used identically across tasks.
- **Note:** This environment has no Android SDK, so `assembleFossDebug` / unit tests run in CI or on a machine with the SDK; task steps still record the exact commands.
