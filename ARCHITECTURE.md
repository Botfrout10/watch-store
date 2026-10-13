# Architecture

## Stack

| | |
|---|---|
| Language | Kotlin 2.4.21 |
| UI | Compose Multiplatform 1.12.1 (Material 3) |
| Gradle | 9.8.1 · AGP 9.4.1 |
| Android | `compileSdk` 37, `minSdk` 26, JVM target 17 |
| State | `org.jetbrains.androidx.lifecycle` ViewModel + StateFlow |
| Data | Network via Ktor 3.6, seeded catalogue behind a repository interface |
| Persist | DataStore (KMP) for bag and favourites |

## The central decision: KMP, Android-first

The app targets Android and iOS from one `commonMain` codebase, but the iOS
targets are **declared commented-out** and Android is the only compiled one.

**Why KMP anyway, with no Mac available:**

- Writing Compose in `commonMain` is byte-identical to writing it for Android
  natively. The real deltas are mechanical: `R.drawable` → `composeResources`,
  Nav Compose → Nav3, Coil → Coil 3.
- Starting native and converting later is a genuine migration — restructure every
  source set, rewrite every resource reference, swap navigation and image
  loading. Starting KMP now makes adding iOS a config change.
- So KMP dominates even if iOS never happens. The bet is on *not paying a
  migration*, not on iOS shipping.

**Why iOS is not compiled yet:** Kotlin/Native can cross-compile Apple *klibs*
from Windows, but final binaries, CocoaPods and simulator runs all need macOS.
Enabling it on Windows would pull a ~1 GB toolchain for something unverifiable.
The commented targets in `composeApp/build.gradle.kts` mark exactly what to
uncomment on a Mac; nothing in `commonMain` changes.

## Modules

Two, because AGP 9 forbids `org.jetbrains.kotlin.multiplatform` and
`com.android.application` in the same module.

```
:composeApp   KMP module — all shared code (commonMain / androidMain / iosMain)
:androidApp   com.android.application — MainActivity, manifest, theme, packaging
```

Kept as a single shared module rather than split into `core` / `feature` /
`data` Gradle subprojects: at this size that is a lot of build ceremony for no
enforcement benefit. Packages inside `commonMain` carry the structure.

```
composeApp/src/commonMain/kotlin/dev/watchstore/
  app/            WatchStoreApp (nav + wiring), BottomNav, Destination
  core/designsystem/  Color, Type, Shape, Theme
  core/ui/        ProductImage, ProductCard, Buttons, Common, HighlightGlyph, ...
  data/model/     Product, Finish, Highlight, Hero, CartLine, formatPrice
  data/catalog/   SeedCatalog
  data/repository/ ProductRepository (interface) + SeedProductRepository
  feature/store/  StoreViewModel
  feature/{home,collection,detail,cart,favorites,profile}/
```

## Data flow

```
server/ (dev fixture) ──HTTP──┐
                              ├─> ApiProductRepository ─┐
SeedCatalog ─────────────────┘                          ├─> FallbackProductRepository
                                                         └─> SeedProductRepository
                                                                      │
                                                            CatalogSnapshot
                                                                      │
                                                            StoreViewModel
                                                                      │
                                                                 screens
```

`ProductRepository` is the only seam between UI and data source. It has one
method, `loadCatalog()`, returning a `CatalogSnapshot` that records **which
source answered**.

**Why a single call, not a dozen query methods:** a remote source cannot answer
`byId` synchronously, and pretending otherwise is what pushes `suspend` into
every screen. The store loads once, then derives — `byId`, `byKind`, `search`,
`featured`, `cartLines` are all pure functions on the snapshot.

**Fallback is deliberate.** `FallbackProductRepository` tries the network, then
the compiled-in seed. A retail app that shows an empty catalogue because the
network blipped is worse than one showing slightly stale stock. Whether the
fallback fired is surfaced on the Profile screen rather than swallowed.

`StoreViewModel` exposes `CatalogState` (`Loading` → `Ready`). The seed guarantees
`Ready` always arrives, so the loading state is brief and never an error state.

Wire format is documented in `docs/api.md`, and the client is written to survive
a server ahead of or behind it: unknown keys ignored, unknown enum values degrade
to a default, empty or non-200 treated as failure rather than a half-render.

## State

`StoreViewModel` is app-scoped and owns the bag and favourites — the two pieces of
state that must outlive navigation and process death. It is exposed via
`viewModel { }` from `lifecycle-viewmodel-compose`.

**Persistence:** `UserStateStore` (`commonMain`) wraps androidx DataStore.
Preferences files are flat maps, so the bag is stored one entry per product id
under a `cart.` prefix and favourites under `fav.`; reads filter the key set by
prefix. Only the *factory* is `expect`/`actual` — the store itself, including all
mapping logic, is shared.

The store is the **single source of truth**: `StoreViewModel` exposes DataStore's
flows via `stateIn` rather than mirroring them into in-memory `MutableStateFlow`s.
That removes a class of bug where the UI and disk disagree after a failed write,
at the cost of one write round-trip per mutation.

## Navigation

A plain back stack (`mutableStateListOf<Destination>`) plus `BackHandler`.

Nav3 was the plan, but `navigation3-ui` resolves to an empty stub artifact
(283-byte aar) on this toolchain, so there is nothing to import. The behaviour
needed — tab roots, push/pop detail, system back — is fully covered. Swapping in
Nav3 later is confined to `WatchStoreApp.kt`.

## Deliberate deviations

- **No Coil.** Bundled `composeResources` drawables load via `painterResource`;
  Coil is only needed for remote images.
- **No Room.** Nothing requires local persistence beyond bag and favourites.
- **No separate design-system module.** Premature for one app.
- **Screens receive the view model directly** rather than a UI-state class. Worth
  tightening once screens grow more state.
- **Artwork is not served by the API.** Generated locally and keyed by product
  id, so the catalogue can move to a real backend without shipping image URLs.

## iOS bring-up (on a Mac)

1. Uncomment `iosArm64()` / `iosSimulatorArm64()` in `composeApp/build.gradle.kts`.
2. Add an `iosApp/` Xcode host project that depends on the `composeApp` framework
   and calls `WatchStoreApp()` from SwiftUI.
3. `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64`.

No `commonMain` changes are required at any step.