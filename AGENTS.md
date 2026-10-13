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

## Conventions

- All UI lives in `composeApp/src/commonMain`. Do not put Android APIs in
  shared code; if something is genuinely platform-only, use `expect`/`actual`.
- Screens are dumb: they render state and emit callbacks. Business logic belongs
  in `StoreViewModel` or the repository.
- Never touch `local.properties` (gitignored). It points at the SDK.
- Never commit build output. Generated product imagery under
  `composeResources/drawable/products/` is gitignored on purpose — regenerate
  with `bun run tools/gen-mock-images.ts`.

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