# Watch Store — agent guide

Kotlin Multiplatform (Compose Multiplatform) commerce app for watches and watch
cases. Android builds and runs today; iOS is a configuration step away.

## Commands

Always use the Gradle wrapper — there is no system Gradle on this machine.

```bash
./gradlew :androidApp:assembleDebug      # compile only
./gradlew :androidApp:installDebug       # build + install to running emulator
./gradlew :composeApp:compileAndroidMain # shared-code typecheck, fastest signal
```

Verify on device:

```bash
adb shell am start -n dev.watchstore/.MainActivity
adb shell screencap -p /sdcard/s.png && adb pull /sdcard/s.png out.png
```

Emulator AVD is `pixel10` (android-37.1):
`emulator -avd pixel10 -no-snapshot-load -gpu swiftshader_indirect`

## Environment gotchas — read before touching the build

These cost real time to rediscover. They are all deliberate.

1. **`JAVA_HOME` is unusable.** It points into an Eclipse `.p2` pool at a **JRE**
   with no `jlink.exe`. AGP 9 needs jlink to build `androidJdkImage` and fails
   with a misleading `NoClassDefFoundError`. The build pins a real JDK via
   `org.gradle.java.home` in `gradle.properties`. Do not "fix" this by trusting
   `JAVA_HOME`.

2. **Gradle must be 9.8.1+.** AGP 9.4.1 references
   `org.gradle.features.binding.ProjectTypeBinding`, an internal API absent from
   9.4.1. The failure surfaces as an unrelated-looking `NoClassDefFoundError`
   during `gradle wrapper`. Bump the wrapper rather than the plugin.

3. **`compileSdk` is 37.** Compose Multiplatform 1.12.x ships androidx compose
   1.12.x, which refuses to build against API 36. AGP auto-downloads the
   platform; no manual SDK install is needed.

4. **AGP 9 forbids KMP + `com.android.application` in one module.** That is why
   the app entry point is `:androidApp` and shared code is `:composeApp`. Do not
   merge them.

5. **`compose.resources` accessors are `internal` by default** and the package
   is derived from the Gradle project name. `composeApp/build.gradle.kts` sets
   `publicResClass` and `packageOfResClass`; changing the project name or
   forgetting these breaks every resource import.

6. **`Font(FontResource)` does not exist** in CMP 1.12.1 on Android — only
   `Font(path, assetManager, …)` and friends. Bundled fonts via
   `composeResources/font` therefore do not compile. Type uses platform families
   (`FontFamily.Serif` / `SansSerif`). See DESIGN.md.

7. **`navigation3-ui` resolves to an empty stub artifact** (283-byte aar). It
   cannot be imported. Navigation is a plain back stack in `WatchStoreApp.kt`.

8. **Compose Multiplatform deprecates the `compose.*` DSL accessors**
   (`compose.runtime`, …). Declare dependencies explicitly in
   `gradle/libs.versions.toml`. Note Material3 is versioned separately from the
   rest of Compose.

9. **`androidResources { enable = true }` is mandatory.** Without it,
   `copyNonXmlValueResourcesForAndroidMain` reports `NO-SOURCE`: resource
   *accessors* still generate and the code compiles, but the asset files never
   reach the APK and the app dies at runtime with `MissingResourceException`.
   This cost a debugging round — treat a silent `NO-SOURCE` as a red flag.

10. **Several Compose Multiplatform Android variants resolve to empty stubs.**
    `navigation3-ui` ships a 283-byte `classes.jar`, `ui-tooling-preview` 291
    bytes — both aars are valid but contain nothing. So **Nav3** and the common
    **`@Preview`** annotation cannot be imported; every unresolved reference to
    them is this, not a bad import.

    Before assuming a CMP dependency is usable, check its real size:

    ```powershell
    $aar = Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\<group>\<artifact>" -Recurse -Filter *.aar | Select -First 1
    "size: $($aar.Length)"
    ```

    Anything in the low kilobytes is a stub. Note `ui-text` looks the same but
    still works — so prefer a compile probe over reading sizes alone.

11. **Bundled fonts need `expect`/`actual`.** There is no `Font(FontResource)`
    overload, so `BrandFonts.android.kt` loads the TTFs through `AssetManager`
    with `FontVariation`. The asset path there must stay in sync with
    `packageOfResClass` in `composeApp/build.gradle.kts`.

## Tools

Product artwork is generated, not hand-drawn:

```bash
bun install --cwd tools
bun run tools/gen-mock-images.ts
```

Writes one WebP per finish into
`composeApp/src/commonMain/composeResources/drawable/`. The script parses
`SeedCatalog.kt` and **fails the run** if its manifest has drifted from it, so
adding a product without artwork is caught immediately. It also emits
`ProductImages.kt` itself, so the compile-time resource mapping can never drift
from the files on disk.

Catalogue API (development):

```bash
bun run server            # http://127.0.0.1:8787, emulator sees 10.0.2.2:8787
```

Also derived from `SeedCatalog.kt`, with the same drift guard. The app falls back
to the compiled-in seed if this is not running — Profile shows which source
answered. Contract: `docs/api.md`.

**The emulator maps `10.0.2.2` to the host loopback.** On a physical device
change `DEV_API_BASE_URL` in `HttpClientFactory.android.kt` to the host's LAN IP.

## Conventions

- All UI lives in `composeApp/src/commonMain`. Do not put Android APIs in
  shared code; if something is genuinely platform-only, use `expect`/`actual`.
- Screens are dumb: they render state and emit callbacks. Business logic belongs
  in `StoreViewModel` or the repository.
- Never touch `local.properties` (gitignored). It points at the SDK.
- Never commit build output.
- **Generated artwork under `composeResources/drawable/` IS committed.** Compose
  Resources accessors are compile-time symbols, so a clone without the files
  would fail to compile. They total ~0.3 MB. Regenerate rather than edit them by
  hand.

## Verification

Before committing, the build must be green:

```bash
./gradlew :androidApp:assembleDebug
```

For visual changes, install and screenshot rather than assuming — three real
bugs in the first build (a zero-height hero image, a paint-order bug hiding the
detail controls, and a total that rendered `$0.00`) were only visible on screen.

## Docs

- `DESIGN.md` — palette, type, component rules
- `ARCHITECTURE.md` — module layout and the reasoning behind it
- `PLAN.md` — what is done and what is next

When you need current library docs, use context7 tools.