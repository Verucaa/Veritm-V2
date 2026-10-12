<div align="center">

<img src="assets/logo-veritm.png" alt="VERITM app icon" width="200" />

# VERITM

### YouTube Music client for Android

A dark, Spotify-styled fork of [Metrolist](https://github.com/Verucaa/Veritm). Dark by default, a green accent theme, and no Discord integration.

<br/>

[![License](https://img.shields.io/github/license/VeruProject/VERITM?style=for-the-badge&labelColor=0d1117)](https://github.com/Verucaa/Veritm/blob/main/LICENSE)
[![Latest release](https://img.shields.io/github/v/release/VeruProject/VERITM?style=for-the-badge&labelColor=0d1117)](https://github.com/Verucaa/Veritm/releases)

<br/>

[**Download**](#download-now) · [**Features**](#features) · [**Build**](#build-from-source) · [**Customize**](#customization) · [**Credits**](#credits)

</div>

> [!NOTE]
> VERITM is a fork of Metrolist, rewritten and rebranded for the `VeruProject` organization. All credit for the original work goes to the Metrolist team and its contributors — see [Credits](#credits).

> [!WARNING]
> **Regional Restriction** — If YouTube Music is unavailable in your region, this app will not work without a **VPN or proxy** connecting to a supported region.

---

<div align="center">

<h1><a id="features"></a>Features</h1>

<table>
  <tr>
    <td width="50%" valign="top">

#### Playback
- Stream any song or video from YouTube Music
- Background playback
- Download & cache for offline use
- Skip silence
- Sleep timer

</td>
    <td width="50%" valign="top">

#### Audio
- Audio normalization
- Tempo & pitch control
- Equalizer
- Crossfade

</td>
  </tr>
  <tr>
    <td width="50%" valign="top">

#### Lyrics & Discovery
- Live synced lyrics
- AI-powered lyrics translation
- Personalized quick picks
- Search songs, albums, artists, videos, and playlists

</td>
    <td width="50%" valign="top">

#### Library & Account
- Full library management
- Local playlists
- Import playlists
- Reorder songs in playlist or queue
- YouTube Music account login
- Sync songs, artists, albums, and playlists

</td>
  </tr>
  <tr>
    <td width="50%" valign="top">

#### Social
- Listen together with friends in real-time
- Last.fm integration for scrobbling

</td>
    <td width="50%" valign="top">

#### Interface
- Spotify-styled green accent, dark by default
- Light / Dark / Black theme modes
- Album-art dynamic color + 19 preset palettes
- Home screen widget
- Built with Material 3

</td>
  </tr>
</table>

</div>

---

<div align="center">

<h1><a id="download-now"></a>Download Now</h1>

<p>Download the latest APK from the releases page.</p>

<a href="https://github.com/VeruProject/VERITM/releases/latest">
  <img src="https://img.shields.io/badge/GitHub-Latest%20Release-0d1117?style=for-the-badge&logo=github" alt="Download the latest VERITM release" />
</a>

<p>Available builds: <code>foss</code> (updater, no Cast), <code>gms</code> (updater + Google Cast), <code>izzy</code> (no updater, no Cast — F-Droid compliant).</p>

</div>

---

<div align="center">

<h1><a id="build-from-source"></a>Build From Source</h1>

</div>

VERITM builds on GitHub Actions automatically — every push to `main` produces a nightly APK and every version bump produces a release. To build locally you need **JDK 21** and the **Android SDK** (`compileSdk 37`, build-tools `36.0.0`).

```bash
# Debug build (fastest)
./gradlew :app:assembleFossDebug
```

The debug APK lands at `app/build/outputs/apk/foss/debug/`.

Release flavors:

```bash
./gradlew :app:assembleFossRelease   # updater, no Cast
./gradlew :app:assembleGmsRelease    # updater + Google Cast
./gradlew :app:assembleIzzyRelease   # F-Droid compliant
```

### Build configuration

Configuration is read from `local.properties` or environment variables:

| Variable | Purpose | Default |
|----------|---------|---------|
| `VERITM_APPLICATION_ID` | Override application ID | `com.veritm.music` |
| `VERITM_APP_NAME` | Override the app name | `VERITM` |
| `VERITM_BUILD_COMMIT` | Commit hash appended to the version name | — |
| `LASTFM_API_KEY` / `LASTFM_SECRET` | Enable Last.fm scrobbling | empty |

Release builds are signed with `app/keystore/release.keystore` using `STORE_PASSWORD`, `KEY_ALIAS`, and `KEY_PASSWORD`.

### CI

Workflows live in `.github/workflows/` and run on `ubuntu-latest`. They build, sign, and (for releases) publish APKs. Set repository secrets for signing ( `KEYSTORE`, `KEY_ALIAS`, `KEYSTORE_PASSWORD`, `KEY_PASSWORD` ) and Last.fm keys if you want scrobbling in CI builds.

---

<div align="center">

<h1><a id="customization"></a>Customization</h1>

</div>

### Replace the app icon

The launcher icon is defined entirely by two resources, so you can swap the branding without touching any code:

| File | What it controls |
|------|------------------|
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | The logo (vector, drawn on top) |
| `app/src/main/res/values/ic_launcher_background.xml` | The background color (`ic_launcher_background`) |

To use your own icon:

1. Edit `ic_launcher_foreground.xml` (any 108×108 vector) or drop in a new vector.
2. Set the background color in `values/ic_launcher_background.xml`.
3. For Android 12+ themed icons, optionally update `app/src/main/res/drawable/ic_launcher_monochrome.xml`.
4. To use a raster logo instead, replace `fastlane/metadata/android/en-US/images/icon.png` and the `mipmap-*/ic_launcher.webp` files.

The adaptive icon definitions that reference these live in `app/src/main/res/mipmap-anydpi-v26/` and `mipmap-anydpi-v31/`.

### Theme & colors

- The default accent is Spotify green (`DefaultThemeColor` in `app/src/main/kotlin/com/veritm/music/ui/theme/Theme.kt`).
- The app ships dark by default (`DarkModeKey`) and pure black is available in settings.
- Users can pick from the preset palettes or enable album-art dynamic color in **Settings → Appearance → Theme**.

---

<div align="center">

<h1><a id="credits"></a>Credits</h1>

<h3>VERITM is a rebrand of Metrolist. It stands on the shoulders of incredible open-source work.</h3>

<h3>Original project</h3>

<table>
  <thead>
    <tr>
      <th align="center">Project</th>
      <th align="center">Authors</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center"><strong>Metrolist</strong></td>
      <td align="center"><a href="https://github.com/MetrolistGroup/Metrolist">Metrolist Group &amp; contributors</a></td>
    </tr>
  </tbody>
</table>

<h3>Main inspirations</h3>

<table>
  <thead>
    <tr>
      <th align="center">Project</th>
      <th align="center">Authors</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center"><strong>InnerTune</strong></td>
      <td align="center"><a href="https://github.com/z-huang">Zion Huang</a> · <a href="https://github.com/Malopieds">Malopieds</a></td>
    </tr>
    <tr>
      <td align="center"><strong>OuterTune</strong></td>
      <td align="center"><a href="https://github.com/DD3Boh">Davide Garberi</a> · <a href="https://github.com/mikooomich">Michael Zh</a></td>
    </tr>
  </tbody>
</table>

<h3>Libraries & integrations</h3>

<table>
  <thead>
    <tr>
      <th align="center">Project</th>
      <th align="center">Contribution</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center"><a href="https://better-lyrics.boidu.dev"><strong>Better Lyrics</strong></a></td>
      <td>Time-synced lyrics with word-by-word highlighting &amp; YouTube Music integration</td>
    </tr>
    <tr>
      <td align="center"><a href="https://github.com/MetrolistGroup/metroserver"><strong>metroserver</strong></a></td>
      <td>Listen-together real-time backend</td>
    </tr>
    <tr>
      <td align="center"><a href="https://github.com/aleksey-saenko/MusicRecognizer"><strong>MusicRecognizer</strong></a></td>
      <td>Music recognition feature &amp; Shazam API integration</td>
    </tr>
    <tr>
      <td align="center"><a href="https://github.com/ZemerTeam/zemer-cipher"><strong>zemer-cipher</strong></a></td>
      <td>YouTube cipher deobfuscation and PoToken generation</td>
    </tr>
  </tbody>
</table>

<h3>We also thank the entire open-source community — for every library, tool, and API that powers this project.</h3>

</div>

---

<div align="center">

<h1>Disclaimer</h1>

This project is **not affiliated with, funded, authorized, endorsed by, or in any way associated** with YouTube, Google LLC, Metrolist Group LLC, or any of their affiliates and subsidiaries.

All trademarks, service marks, and intellectual property rights referenced in this project belong to their respective owners.

</div>

---

<div align="center">

<h1>License</h1>

Licensed under the **GNU General Public License v3.0 (GPL-3.0)**. See [LICENSE](LICENSE) for details.

<br/>

**VERITM is a project by [VeruProject](https://github.com/VeruProject)**

</div>
