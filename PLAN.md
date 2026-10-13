# Plan

## Done

**Toolchain**
- KMP project on Gradle 9.8.1 / AGP 9.4.1 / Kotlin 2.4.21 / CMP 1.12.1
- `:androidApp` + `:composeApp` split required by AGP 9
- JDK pinned away from the broken `JAVA_HOME`; `compileSdk` 37
- Emulator AVD renamed `pulse` → `pixel10`

**Design system**
- Golden sand on warm dark, full palette + light scheme
- Type scale, shapes, theme
- Hand-drawn Canvas glyphs for the highlight strip

**Data**
- Product / Finish / Highlight / Hero / CartLine models, cents-based prices
- 24-product seeded catalogue across watches, boxes, accessories
- `ProductRepository` interface + seeded implementation

**Screens** — all built and verified on device
- Home: hero carousel, search, popular rail, featured list
- Collection: segmented tabs (Watches / Boxes / Accessories), 2-up grid, search
- Detail: hero image, swatches, highlight strip, “Complete the set”, add to cart
- Bag: line items, steppers, subtotal / shipping / total
- Saved: favourites list
- Profile: placeholder + prototype notice

**Interaction**
- Back stack, system back, custom bottom bar with badge
- Bag and favourites working end to end; cart totals verified

**Bugs found and fixed during the build**
- Hero image collapsed to zero height (`Canvas` with no intrinsic height)
- Detail controls painted *under* the image (`Box` declaration order)
- Cart total rendered `$0.00` (re-parsed a formatted string back to cents)
- `PillShape` clip on a text column clipped “BOXES” → “JXES”
- Hero pagination dots collided with the CTA
- Product descriptions read as sentence fragments

**Imagery (Phase B)**
- `tools/gen-mock-images.ts` generates one WebP per product via `@napi-rs/canvas`
  — no `sharp` needed, it installs and runs cleanly on Windows
- Manifest is cross-checked against `SeedCatalog.kt`; drift fails the run
- Watches: brushed-metal case, stitched strap, recessed dial, 10:10 hands, sheen
- Boxes: lid, seam, brass clasp with keyhole; Accessories: band, lugs, clasp
- `ProductImage` renders artwork when present, falls back to the drawn silhouette
- Artwork is committed (~0.3 MB) so a fresh clone compiles
- Fixed: dropped `androidResources { enable = true }` during the AGP 9 migration,
  so drawables were prepared but never packaged (`MissingResourceException`)

**Persistence**
- Bag and favourites survive process death via androidx DataStore (1.2.1, KMP)
- `UserStateStore` interface in `commonMain`; only the factory is
  `expect`/`actual`
- DataStore is the single source of truth — `StoreViewModel` exposes its flows
  via `stateIn` instead of mirroring them into in-memory state, so the UI and
  disk cannot disagree after a failed write
- Verified by force-stopping the app and relaunching

**Docs**
- `AGENTS.md` — commands plus the environment gotchas found the hard way
- `DESIGN.md`, `ARCHITECTURE.md`, `PLAN.md`

## Next

### Also worth doing

- Real navigation library once `navigation3-ui` resolves to a non-stub artifact
- Restore branded fonts (Playfair + Inter) if CMP gains `Font(FontResource)`
- Wire Ktor behind `ProductRepository` when a backend exists
- `@Preview` coverage for both colour schemes
- Tighten screens to accept UI state instead of the view model
- Real 3D: Compose Scene / Filament-KMP both work across Android and iOS from
  Kotlin, but need actual `.glb` models. The generated imagery is the current
  ceiling until those exist.

## Not doing

- Checkout / payments — explicitly out of scope for the prototype
- Auth — Profile is a placeholder
- iOS build — needs a Mac; structure is ready