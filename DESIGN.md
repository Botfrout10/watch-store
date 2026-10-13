# Design — “golden sand on warm dark”

## Intent

A retail app for watches and watch cases that feels like a jeweller rather than a
marketplace. Warm, quiet, expensive. Nothing is bright except the gold.

Inspired by the layout rhythm of a Dribbble luxury-watch-box concept, but with its
own palette, own type pairing and its own screen structure.

## Palette

Warm darks with a brown undertone — never neutral grey, never green. One accent.

| Token | Hex | Use |
|---|---|---|
| `base` | `#14110D` | app background |
| `surface` | `#1E1A14` | cards, sheets, bottom bar |
| `surfaceHigh` | `#272117` | elevated cards, inputs, panels |
| `gold` | `#D8B26A` | **the only accent** — CTAs, prices, active states |
| `goldSoft` | `#EBD3A3` | hover, sheen highlights, hero spotlight |
| `onGold` | `#2A1F0E` | text on gold |
| `textPrimary` | `#F2EAD9` | warm cream |
| `textSecondary` | `#A99C86` | captions, labels |
| `outline` | `#3A3227` | hairlines, ghost button borders |
| `error` | `#C4695E` | muted terracotta, not alarm red |

Light scheme exists (`#F7F2E8` sand paper, accent darkened to `#9A7432` for
contrast) but dark is the default and the tuned one.

**Rule:** if an element is gold, it is either the primary action or the price.
Nothing else gets the accent.

## Type

| Role | Family | Notes |
|---|---|---|
| Display / product names / prices | Serif (`FontFamily.Serif`) | 19–44sp, tight tracking |
| Labels, body, everything functional | Sans (`FontFamily.SansSerif`) | 11–15sp |

Full scale in `core/designsystem/Type.kt`.

### Known compromise

The intended pairing was **Playfair Display + Inter**, bundled as variable TTFs.
Compose Multiplatform 1.12.1 exposes no `Font(FontResource)` overload on Android,
so that path does not compile. The app currently resolves platform families —
Noto Serif + Roboto on Android, New York + SF Pro on iOS. This reads well and
ships identical metrics on both platforms.

To restore branded fonts later, change only `PlayfairFamily` / `InterFamily` in
`Type.kt`. Nothing else references the font.

## Shape & elevation

Generous radii are the cheapest signal of “premium” — they echo the rounded case
profiles of the products.

- Cards `22dp`, panels `18–24dp`, hero `32dp`
- CTAs, search, chips: fully rounded pills
- Elevation is expressed with hairline `outline` borders, **not** drop shadows.
  Shadows read as cheap against warm dark; borders read as precise.

## Imagery

`ProductImage` draws a lit silhouette on a warm backdrop seeded per product, so
the catalogue reads as one curated set rather than one placeholder repeated.

Three rules:

1. **Every product gets a different backdrop**, derived from `artSeed` — spot
   position, warmth and spread all vary.
2. **Aspect ratio is owned by the caller**, not the renderer: `4:5` for cards,
   `3:4` for detail. Real imagery drops into the identical slot later.
3. **Contact shadows are built from stacked translucent ovals**, because blur is
   not available identically across targets.

This is a placeholder. Phase B replaces it with generated WebP assets — same slot,
no reflow. See `PLAN.md`.

## Components

| Component | Rule |
|---|---|
| `GoldButton` | The one gold CTA per screen. 56dp, pill, 14sp semibold. |
| `GhostIconButton` | Circular, outlined, transparent. Everything secondary. |
| `PriceText` | Serif, gold, always two decimals so columns align. |
| `ProductCard` | Brand label → serif name → gold price. Heart overlays the image. |
| `PillSearch` | Pill outline, quiet placeholder, gold cursor. |
| `SegmentedTabs` | One selected gold pill inside a bordered track. |
| `HighlightStrip` | Icon / title / micro-caption. The caption supports, never competes. |
| `KindChip` | Low-contrast surface pill, marks category on imagery. |

Icons in the highlight strip are **hand-drawn on Canvas** (`HighlightGlyph.kt`),
not Material Icons — the stock set is generic and pulls the eye off-brand.

## Interaction

- Tab roots reset the back stack; detail pushes onto it. System back pops.
- The bottom bar is custom, not `NavigationBar`: the active state needs a gold
  pill *and* a short rule above the icon, which the stock indicator slot cannot
  express.
- Every tappable icon has a `contentDescription`.

## Signature feature

**“Complete the set”** on product detail. Because the store sells watches *and*
cases separately, a watch page offers its matching case as a one-tap add. This is
the thing a two-category store can do that the reference cannot — it is why the
catalogue is split this way.