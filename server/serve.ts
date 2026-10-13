/**
 * Development catalogue API.
 *
 *   bun run server
 *
 * Serves the same catalogue the app has compiled in, so the network path can be
 * exercised end to end. The fixture is **derived from SeedCatalog.kt** and the
 * script fails if the two have drifted, exactly like the image generator.
 *
 * The Android emulator reaches this on http://10.0.2.2:8787.
 */
import { existsSync, readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const HERE = dirname(fileURLToPath(import.meta.url));
const ROOT = join(HERE, "..");
const SEED_CATALOG = join(
  ROOT,
  "composeApp/src/commonMain/kotlin/dev/watchstore/data/catalog/SeedCatalog.kt",
);

const PORT = Number(process.env.PORT ?? 8787);

if (!existsSync(SEED_CATALOG)) {
  console.error(`Cannot find SeedCatalog.kt at ${SEED_CATALOG}`);
  process.exit(1);
}

const source = readFileSync(SEED_CATALOG, "utf8");

// ---- extraction ------------------------------------------------------------

const num = (s: string | undefined, fallback = 0): number =>
  s === undefined ? fallback : Number(s.replace(/_/g, ""));

const str = (block: string, key: string): string | undefined =>
  block.match(new RegExp(`${key}\\s*=\\s*"([^"]*)"`))?.[1];

const bool = (block: string, key: string): boolean =>
  new RegExp(`${key}\\s*=\\s*true`).test(block);

/** `finishes(FinishTone.Obsidian to "Obsidian", ...)` or `tone = FinishTone.X` */
function tones(block: string): string[] {
  const list = block.match(/finishes\(([^)]*)\)/)?.[1];
  if (list) {
    return [...list.matchAll(/FinishTone\.(\w+)/g)].map((m) => m[1]);
  }
  return [...block.matchAll(/tone\s*=\s*FinishTone\.(\w+)/g)].map((m) => m[1]);
}

/** Highlight triples, mirroring the helpers in SeedCatalog.kt. */
function highlights(kind: "WATCH" | "BOX" | "ACCESSORY") {
  if (kind === "WATCH") {
    return [
      { icon: "Sapphire", title: "Sapphire crystal", caption: "Scratch resistant" },
      { icon: "Movement", title: "Automatic movement", caption: "Self-winding calibre" },
      { icon: "Water", title: "Water resistant", caption: "300 m / 1000 ft" },
    ];
  }
  if (kind === "BOX") {
    return [
      { icon: "Wood", title: "Hand-finished hardwood", caption: "Solid, not veneer" },
      { icon: "Interior", title: "Soft velvet interior", caption: "Protects your pieces" },
      { icon: "Lock", title: "Secure brass lock", caption: "Key included" },
    ];
  }
  return [
    { icon: "Craft", title: "Made to last", caption: "Serviceable parts" },
    { icon: "Warranty", title: "2 year cover", caption: "Included" },
    { icon: "Craft", title: "Made to last", caption: "Serviceable parts" },
  ];
}

function finishList(toneNames: string[]) {
  return toneNames.map((tone) => ({
    tone,
    name: tone.charAt(0) + tone.slice(1).toLowerCase(),
  }));
}

const products = [];

// watch(...) and watchBox(...) share a shape; accessory(...) differs.
const entryPattern = /\b(watch|watchBox|accessory)\(([\s\S]*?)\n    \),/g;
let match: RegExpExecArray | null;
while ((match = entryPattern.exec(source)) !== null) {
  const [, fn, block] = match;
  const id = str(block, "id");
  if (!id) continue;

  const kind = fn === "accessory" ? "ACCESSORY" : fn === "watchBox" ? "BOX" : "WATCH";
  const name = str(block, "name") ?? id;
  const brand = kind === "BOX" || kind === "ACCESSORY" ? "Atelier Case" : str(block, "brand") ?? "";
  const tagline = str(block, "tagline") ?? "";
  const toneNames = tones(block);
  const price = num(block.match(/price\s*=\s*([\d_]+)/)?.[1]);

  const description =
    kind === "WATCH"
      ? `${brand} ${name}. ${tagline}. Crafted in small runs and finished by hand.`
      : kind === "BOX"
        ? `Holds ${num(block.match(/slots\s*=\s*(\d+)/)?.[1], 2)} watches, with a hand-finished hardwood body, a soft velvet interior and a secure brass lock.`
        : tagline;

  products.push({
    id,
    kind,
    name,
    brand,
    tagline,
    description,
    priceCents: price,
    finishes: finishList(toneNames),
    highlights: highlights(kind),
    pairsWithId: str(block, "pairsWith") ?? null,
    rating: Number(block.match(/rating\s*=\s*([\d.]+)/)?.[1] ?? 0),
    reviewCount: num(block.match(/reviews\s*=\s*(\d+)/)?.[1]),
    isFeatured: bool(block, "featured"),
    isPopular: bool(block, "popular"),
    artSeed: num(block.match(/seed\s*=\s*(\d+)/)?.[1]),
  });
}

// Heroes are parsed separately — they carry their own productId.
const heroes = [];
const heroPattern = /Hero\(([\s\S]*?)\n    \),/g;
while ((match = heroPattern.exec(source)) !== null) {
  const block = match[1];
  const id = str(block, "id");
  if (!id) continue;
  heroes.push({
    id,
    eyebrow: str(block, "eyebrow") ?? "",
    title: str(block, "title") ?? "",
    subtitle: str(block, "subtitle") ?? "",
    cta: str(block, "cta") ?? "",
    productId: str(block, "productId") ?? "",
    // Unquoted enum reference in Kotlin, e.g. `targetKind = ProductKind.BOX`.
    targetKind:
      block.match(/targetKind\s*=\s*(?:ProductKind\.)?(\w+)/)?.[1] ?? "WATCH",
  });
}

// Drift guard: every product the app knows about must appear here. Scoped to
// the product list so `Hero(id = ...)` entries below the marker are excluded.
const productsSection = source.split("val seedHeroes")[0] ?? source;
const appIds = [...productsSection.matchAll(/\bid\s*=\s*"([a-z0-9-]+)"/g)].map((m) => m[1]);
const known = new Set(products.map((p) => p.id));
const missing = appIds.filter((id) => !known.has(id));
if (missing.length) {
  console.error("Extractor missed products — SeedCatalog.kt has changed shape.");
  console.error("  missing:", missing.join(", "));
  process.exit(1);
}

const catalog = { products, heroes };
console.log(`Derived ${products.length} products and ${heroes.length} heroes from SeedCatalog.kt`);

// ---- server ----------------------------------------------------------------

Bun.serve({
  port: PORT,
  fetch(req) {
    const url = new URL(req.url);

    if (url.pathname === "/api/v1/catalog") {
      return Response.json(catalog, {
        headers: { "cache-control": "no-store" },
      });
    }

    if (url.pathname === "/api/v1/health") {
      return Response.json({ ok: true, products: products.length });
    }

    return new Response("Not found", { status: 404 });
  },
});

console.log(`Catalogue API listening on http://127.0.0.1:${PORT}`);
console.log(`  Android emulator: http://10.0.2.2:${PORT}/api/v1/catalog`);