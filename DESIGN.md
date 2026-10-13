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

Generous radii are the cheapest signal of "premium" — they echo the rounded case
profiles of the products.

- Cards `14dp`, panels `14–20dp`, hero `24dp`, detail sheet top `26dp`
- CTAs, search, chips: fully rounded pills
- Elevation is expressed with hairline `outline` borders, **not** drop shadows.
  Shadows read as cheap against warm dark; borders read as precise.

## Glass

Search field, filter button, category chips, the detail screen's back/heart/share
buttons and the pagination pill all use a frosted treatment: a translucent white
wash plus a light hairline edge.

True backdrop blur is deliberately avoided — `Modifier.blur` is Android-only and
API 31+, so a real blur would look different on iOS. On a warm dark ground the
wash + hairline reads as glass and stays identical across targets.

## Component sizing

Measured against the reference mock (phone width ≈ 411dp, 0.893 dp per mock px).

| Component | Size |
|---|---|
| Search field | 44dp pill, 14sp placeholder, 17dp icon trailing |
| Filter button | 44dp circle |
| Hero panel | 312dp tall, 24dp corners |
| Hero CTA | 34dp pill, 13sp |
| Pagination dots | 18×6dp active pill, 6dp inactive, 5dp gap, on a glass pill |
| Catalogue card | image 0.94 aspect, footer 55dp, corner 14dp |
| Card add-to-cart | 36dp gold circle |
| Bottom bar | 4 tabs, 22dp icons, 11sp labels |
| Detail controls | 44dp glass circles |
| Finish swatch | 34dp, 38dp when active |
| Detail CTA | 56dp pill + 56dp outlined button, 16dp corners |

## Components

| Component | Rule |
|---|---|
| `GoldButton` / hero CTA | Gold means primary action or price. Nothing else. |
| `GlassCircleButton` | Every secondary icon control. Circular, hairline. |
| `CatalogCard` | Artwork, then a footer strip: name, price, gold cart disc. |
| `PillSearch` | Glass pill, placeholder left, search icon right. |
| `FinishSwatch` | Colour dot; selected grows and gains a gold ring. |
| `HighlightStrip` | Icon / title / micro-caption. The caption supports, never competes. |
| `DotsPill` | Hero and detail pagination on a translucent pill. |
| `FilterGlyph` | Hand-drawn sliders — no filter icon exists in core Material Icons. |

Icons in the highlight strip and the filter button are **hand-drawn on Canvas**,
not Material Icons: the core set is generic and pulls the eye off-brand, and
Extended is frozen at 1.7.3.
