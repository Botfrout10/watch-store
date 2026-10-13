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

## Next

### Phase B — real product imagery (the big one)

Placeholders are the weakest part of the app today. Replace
`ProductImage`'s drawing with generated assets — **same slot, no reflow**.

- `tools/gen-mock-images.ts` (Bun) emits unique WebP per product into
  `composeApp/src/commonMain/composeResources/drawable/products/`
- Watches as 3/4 discs with bezel gradient, specular sheen, contact shadow
- Boxes as angled forms with cast shadow and a brass clasp
- Each on its own warm radial backdrop, matching the current seed logic
- Catalog points at the files; emitted binaries stay gitignored
- ⚠️ Rasterising SVG likely needs `sharp` — verify it installs cleanly under Bun
  on Windows, else render PNG directly

Once real assets exist, the long-term path is Compose Scene / Filament-KMP for
actual 3D. Both now work across Android and iOS from Kotlin, but they need real
`.glb` models that do not exist yet.

### Persistence

Bag and favourites reset on process death.

- DataStore (KMP) for both — small, and the obvious fix
- Room only if the catalogue ever needs to work fully offline

### Also worth doing

- Real navigation library once `navigation3-ui` resolves to a non-stub artifact
- Restore branded fonts (Playfair + Inter) if CMP gains `Font(FontResource)`
- Wire Ktor behind `ProductRepository` when a backend exists
- `@Preview` coverage for both colour schemes
- Tighten screens to accept UI state instead of the view model

## Not doing

- Checkout / payments — explicitly out of scope for the prototype
- Auth — Profile is a placeholder
- iOS build — needs a Mac; structure is ready