# Catalogue API

The app reads its catalogue over HTTP. A development server that implements this
contract ships in `server/`:

```bash
bun run server
```

The emulator reaches it at `http://10.0.2.2:8787`.

## Endpoints

### `GET /api/v1/catalog`

Returns the whole catalogue in one payload.

```jsonc
{
  "products": [ /* Product */ ],
  "heroes":   [ /* Hero */ ]
}
```

### `GET /api/v1/health`

`{"ok": true, "products": 24}` — used by tooling, not the app.

## Product

```jsonc
{
  "id": "meridian-38",              // required, stable, used for artwork + cart keys
  "kind": "WATCH",                 // WATCH | BOX | ACCESSORY
  "name": "Meridian 38",
  "brand": "Halcyon",
  "tagline": "Steel automatic with a slate dial",
  "description": "Halcyon Meridian 38. …",
  "priceCents": 124000,            // integer cents — never a float
  "finishes": [
    { "tone": "Obsidian", "name": "Obsidian" },
    { "tone": "Steel",     "name": "Steel"     }
  ],
  "highlights": [
    { "icon": "Sapphire", "title": "Sapphire crystal", "caption": "Scratch resistant" }
  ],
  "pairsWithId": "obsidian-black",  // optional; drives "Complete the set"
  "rating": 4.8,
  "reviewCount": 214,
  "isFeatured": true,
  "isPopular": true,
  "artSeed": 1
}
```

`finishes` order matters: it maps 1:1 onto the detail pager's pages, so swiping
the image and tapping a swatch stay in sync. Products with a single finish get a
second generated angle so the pager is still swipeable.

## Hero

```jsonc
{
  "id": "hero-craft",
  "eyebrow": "Autumn collection",
  "title": "Built to\noutlive us",
  "subtitle": "Small-run cases in walnut, oak and painted steel.",
  "cta": "Shop Now",
  "productId": "cascade-trunk",    // must match a product id
  "targetKind": "BOX"              // where the CTA navigates
}
```

## Compatibility rules

The client is written to survive a server that is ahead of or behind it.

| Situation | Behaviour |
|---|---|
| Unknown field in the payload | Ignored (`ignoreUnknownKeys`) |
| Unknown enum value (`kind`, `tone`, `icon`) | Falls back to a default; never crashes |
| Optional field missing | Uses the DTO default |
| Empty `products` array | Treated as a failure, falls back to the seed |
| Non-200 | Treated as a failure, falls back to the seed |
| Timeout / no network | Falls back to the seed |

**A response the client cannot use is a failure, not a half-rendered screen.**
`ApiProductRepository` throws in those cases and
`FallbackProductRepository` answers with the compiled-in catalogue. Which one won
is reported in `CatalogSnapshot.source` and shown on the Profile screen, so stale
stock is never silently invisible.

## Artwork

Artwork is **not** served by the API. Images are generated locally
(`bun run tools/gen-mock-images.ts`) and keyed by product id, so the catalogue can
move to a real backend without shipping image URLs.

## Implementing this elsewhere

Replace `ApiProductRepository`'s base URL in
`HttpClientFactory.android.kt` (and add the iOS actual when that target is
enabled). Nothing above `ProductRepository` changes.