/**
 * Generates product "photography" for the catalogue.
 *
 * The point is continuity with the app: the backdrop maths here is the same
 * formula that `ProductImage.kt` used for its drawn placeholders, so when these
 * land the screens do not visibly change apart from gaining real detail.
 *
 * Output: composeApp/src/commonMain/composeResources/drawable/products_<id>.webp
 *
 *   bun run tools/gen-mock-images.ts
 */
import { createCanvas } from "@napi-rs/canvas";
import { existsSync, mkdirSync, readFileSync, readdirSync, rmSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const HERE = dirname(fileURLToPath(import.meta.url));
const ROOT = join(HERE, "..");
const OUT_DIR = join(
  ROOT,
  "composeApp/src/commonMain/composeResources/drawable",
);
const SEED_CATALOG = join(
  ROOT,
  "composeApp/src/commonMain/kotlin/dev/watchstore/data/catalog/SeedCatalog.kt",
);
const PRODUCT_IMAGES_KT = join(
  ROOT,
  "composeApp/src/commonMain/kotlin/dev/watchstore/data/catalog/ProductImages.kt",
);

const W = 1200;
const H = 1500; // 4:5, matching CARD_ASPECT
const QUALITY = 82;

// --- Brand palette (mirrors core/designsystem/Color.kt) ----------------------

const BASE_DARK = "#14110D";
const GOLD = "#D8B26A";
const GOLD_SOFT = "#EBD3A3";
const ON_GOLD = "#2A1F0E";

/** Mirrors FinishTone in data/model/Product.kt. */
const TONES: Record<string, string> = {
  Obsidian: "#1B1B1D",
  Walnut: "#6B4526",
  Sand: "#D8C4A0",
  Emerald: "#1F4D3D",
  Midnight: "#1B2A4A",
  Cognac: "#8A4B22",
  Oyster: "#B8B2A6",
  Bordeaux: "#5C1F2B",
  Steel: "#9AA0A6",
  Champagne: "#D9C08A",
};

type Kind = "WATCH" | "BOX" | "ACCESSORY";

/**
 * Mirrors SeedCatalog.kt. The `verifyAgainstSeedCatalog` check below fails the
 * run if these drift apart, so this cannot silently rot.
 */
const MANIFEST: { id: string; kind: Kind; tones: string[]; seed: number }[] =
  [
    { id: "meridian-38", kind: "WATCH", tones: ["Obsidian", "Steel"], seed: 1 },
    { id: "solstice-diver", kind: "WATCH", tones: ["Emerald", "Midnight"], seed: 2 },
    { id: "onyx-chrono", kind: "WATCH", tones: ["Obsidian"], seed: 3 },
    { id: "aurum-field", kind: "WATCH", tones: ["Champagne", "Sand"], seed: 4 },
    { id: "nocturne-moon", kind: "WATCH", tones: ["Midnight", "Bordeaux"], seed: 5 },
    { id: "terrane-gmt", kind: "WATCH", tones: ["Cognac", "Walnut"], seed: 6 },
    { id: "halcyon-34", kind: "WATCH", tones: ["Oyster", "Sand"], seed: 7 },
    { id: "basalt-auto", kind: "WATCH", tones: ["Obsidian", "Bordeaux"], seed: 8 },
    { id: "cascade-diver", kind: "WATCH", tones: ["Emerald", "Oyster"], seed: 9 },
    { id: "vesper-slim", kind: "WATCH", tones: ["Oyster"], seed: 10 },
    { id: "walnut-prestige", kind: "BOX", tones: ["Walnut"], seed: 21 },
    { id: "midnight-blue", kind: "BOX", tones: ["Midnight"], seed: 22 },
    { id: "emerald-hardshell", kind: "BOX", tones: ["Emerald"], seed: 25 },
    { id: "sand-linen", kind: "BOX", tones: ["Sand"], seed: 24 },
    { id: "cognac-roll", kind: "BOX", tones: ["Cognac"], seed: 26 },
    { id: "oyster-roll", kind: "BOX", tones: ["Oyster"], seed: 27 },
    { id: "bordeaux-presentation", kind: "BOX", tones: ["Bordeaux"], seed: 28 },
    { id: "basalt-compact", kind: "BOX", tones: ["Obsidian"], seed: 29 },
    { id: "cascade-trunk", kind: "BOX", tones: ["Walnut", "Steel"], seed: 30 },
    { id: "obsidian-black", kind: "BOX", tones: ["Obsidian"], seed: 23 },
    { id: "velvet-cushions", kind: "ACCESSORY", tones: ["Bordeaux"], seed: 41 },
    { id: "spring-bar-tool", kind: "ACCESSORY", tones: ["Steel"], seed: 42 },
    { id: "leather-strap", kind: "ACCESSORY", tones: ["Cognac"], seed: 43 },
    { id: "steel-bracelet", kind: "ACCESSORY", tones: ["Steel"], seed: 44 },
  ];

/**
 * The Kotlin catalogue is the source of truth. If a product is added or renamed
 * there and not here, this throws rather than emitting a stale asset set.
 */
function verifyAgainstSeedCatalog(): void {
  const source = readFileSync(SEED_CATALOG, "utf8");
  // Only the product list: `Hero(id = ...)` entries below the marker would
  // otherwise be picked up too.
  const productsSection = source.split("val seedHeroes")[0] ?? source;
  const kotlinIds = [...productsSection.matchAll(/\bid\s*=\s*"([a-z0-9-]+)"/g)].map(
    (m) => m[1],
  );
  const manifestIds = MANIFEST.map((p) => p.id);

  const missing = kotlinIds.filter((id) => !manifestIds.includes(id));
  const extra = manifestIds.filter((id) => !kotlinIds.includes(id));

  if (missing.length || extra.length) {
    console.error("Catalogue and image manifest have drifted.");
    if (missing.length) console.error("  missing from manifest:", missing.join(", "));
    if (extra.length) console.error("  not in SeedCatalog.kt:", extra.join(", "));
    process.exit(1);
  }
  console.log(`Manifest matches SeedCatalog.kt (${manifestIds.length} products).`);
}

// --- colour helpers ---------------------------------------------------------

function hexToRgb(hex: string): [number, number, number] {
  const h = hex.replace("#", "");
  return [
    parseInt(h.slice(0, 2), 16),
    parseInt(h.slice(2, 4), 16),
    parseInt(h.slice(4, 6), 16),
  ];
}

/** Component-wise sRGB lerp, matching Compose's `Color.lerp` default. */
function mix(a: string, b: string, t: number): string {
  const [r1, g1, b1] = hexToRgb(a);
  const [r2, g2, b2] = hexToRgb(b);
  const c = (x: number, y: number) => Math.round(x + (y - x) * t);
  return `#${[c(r1, r2), c(g1, g2), c(b1, b2)]
    .map((v) => v.toString(16).padStart(2, "0"))
    .join("")}`;
}

const lighten = (hex: string, t: number) => mix(hex, "#FFFFFF", t);
const darken = (hex: string, t: number) => mix(hex, "#000000", t);

// --- drawing ----------------------------------------------------------------

function drawBackdrop(
  ctx: CanvasRenderingContext2D,
  tone: string,
  seed: number,
): void {
  // Same formula as the placeholder renderer in ProductImage.kt.
  const spread = 0.88 + (seed % 4) * 0.05;
  const spotY = 0.28 + (seed % 3) * 0.05;

  // A product photo sits on a *studio sweep* that is clearly lighter than the
  // app background, otherwise dark products (Obsidian, Midnight) render as
  // near-black rectangles that disappear into the card grid.
  const sweep = mix(BASE_DARK, "#4C4234", 0.62); // warm dark surface
  const tint = 0.18 + (seed % 5) * 0.035;

  const spot = mix(sweep, tone, tint);
  const mid = mix(sweep, tone, tint * 0.5);
  const deep = mix(mix(BASE_DARK, sweep, 0.55), tone, tint * 0.45);

  ctx.fillStyle = BASE_DARK;
  ctx.fillRect(0, 0, W, H);

  const g = ctx.createRadialGradient(
    W * 0.5,
    H * spotY,
    0,
    W * 0.5,
    H * spotY,
    Math.max(W, H) * spread,
  );
  g.addColorStop(0, spot);
  g.addColorStop(0.52, mid);
  g.addColorStop(1, deep);
  ctx.fillStyle = g;
  ctx.fillRect(0, 0, W, H);
}

/** Soft elliptical ground shadow, built from a radial gradient. */
function groundShadow(
  ctx: CanvasRenderingContext2D,
  cx: number,
  cy: number,
  rx: number,
  ry: number,
  alpha: number,
): void {
  ctx.save();
  ctx.translate(cx, cy);
  ctx.scale(1, ry / rx);
  const g = ctx.createRadialGradient(0, 0, 0, 0, 0, rx);
  g.addColorStop(0, `rgba(0,0,0,${alpha})`);
  g.addColorStop(0.45, `rgba(0,0,0,${alpha * 0.55})`);
  g.addColorStop(0.78, `rgba(0,0,0,${alpha * 0.18})`);
  g.addColorStop(1, "rgba(0,0,0,0)");
  ctx.fillStyle = g;
  ctx.beginPath();
  ctx.arc(0, 0, rx, 0, Math.PI * 2);
  ctx.fill();
  ctx.restore();
}

function roundRectPath(
  ctx: CanvasRenderingContext2D,
  x: number,
  y: number,
  w: number,
  h: number,
  r: number,
): void {
  const rr = Math.min(r, w / 2, h / 2);
  ctx.beginPath();
  ctx.moveTo(x + rr, y);
  ctx.lineTo(x + w - rr, y);
  ctx.quadraticCurveTo(x + w, y, x + w, y + rr);
  ctx.lineTo(x + w, y + h - rr);
  ctx.quadraticCurveTo(x + w, y + h, x + w - rr, y + h);
  ctx.lineTo(x + rr, y + h);
  ctx.quadraticCurveTo(x, y + h, x, y + h - rr);
  ctx.lineTo(x, y + rr);
  ctx.quadraticCurveTo(x, y, x + rr, y);
  ctx.closePath();
}

/** Tapered strap segment, narrower at the far end. */
function strapPath(
  ctx: CanvasRenderingContext2D,
  cx: number,
  y: number,
  len: number,
  nearW: number,
  farW: number,
): void {
  ctx.beginPath();
  ctx.moveTo(cx - nearW / 2, y);
  ctx.lineTo(cx + nearW / 2, y);
  ctx.lineTo(cx + farW / 2, y + len);
  ctx.lineTo(cx - farW / 2, y + len);
  ctx.closePath();
}

function drawWatch(ctx: CanvasRenderingContext2D, tone: string): void {
  const cx = W / 2;
  const cy = H * 0.47;
  const r = Math.min(W, H) * 0.235;

  groundShadow(ctx, cx, cy + r * 1.45, r * 1.5, r * 0.3, 0.6);

  // --- strap -----------------------------------------------------------------
  const strapNear = r * 0.82;
  const strapFar = r * 0.62;
  const strapLen = r * 1.05;
  const strapTop = darken(tone, 0.62);
  const strapBottom = darken(tone, 0.5);

  ctx.save();
  strapPath(ctx, cx, cy - r * 1.95, strapLen, strapNear, strapFar);
  const gs = ctx.createLinearGradient(cx, cy - r * 1.95, cx, cy - r * 0.9);
  gs.addColorStop(0, darken(strapTop, 0.25));
  gs.addColorStop(1, strapTop);
  ctx.fillStyle = gs;
  ctx.fill();

  strapPath(ctx, cx, cy + r * 0.95, strapLen, strapNear, strapFar);
  const gs2 = ctx.createLinearGradient(cx, cy + r * 0.95, cx, cy + r * 2);
  gs2.addColorStop(0, strapBottom);
  gs2.addColorStop(1, darken(strapBottom, 0.3));
  ctx.fillStyle = gs2;
  ctx.fill();
  ctx.restore();

  // Stitching on the lower strap.
  ctx.save();
  ctx.strokeStyle = lighten(darken(tone, 0.55), 0.22);
  ctx.lineWidth = Math.max(1, r * 0.018);
  ctx.setLineDash([r * 0.07, r * 0.07]);
  ctx.beginPath();
  ctx.moveTo(cx - strapFar * 0.3, cy + r * 1.05);
  ctx.lineTo(cx - strapFar * 0.34, cy + r * 1.88);
  ctx.moveTo(cx + strapFar * 0.3, cy + r * 1.05);
  ctx.lineTo(cx + strapFar * 0.34, cy + r * 1.88);
  ctx.stroke();
  ctx.restore();

  // --- case ------------------------------------------------------------------
  ctx.save();
  ctx.beginPath();
  ctx.arc(cx, cy, r, 0, Math.PI * 2);

  const metal = ctx.createLinearGradient(cx - r, cy - r, cx + r * 0.8, cy + r);
  metal.addColorStop(0, lighten(tone, 0.5));
  metal.addColorStop(0.22, lighten(tone, 0.18));
  metal.addColorStop(0.45, darken(tone, 0.18));
  metal.addColorStop(0.62, lighten(tone, 0.3));
  metal.addColorStop(0.85, darken(tone, 0.42));
  metal.addColorStop(1, darken(tone, 0.6));
  ctx.fillStyle = metal;
  ctx.fill();

  // Specular sweep across the upper-left of the metal.
  ctx.clip();
  const spec = ctx.createRadialGradient(
    cx - r * 0.45,
    cy - r * 0.5,
    0,
    cx - r * 0.45,
    cy - r * 0.5,
    r * 1.25,
  );
  spec.addColorStop(0, "rgba(255,255,255,0.34)");
  spec.addColorStop(0.5, "rgba(255,255,255,0.06)");
  spec.addColorStop(1, "rgba(255,255,255,0)");
  ctx.fillStyle = spec;
  ctx.fillRect(cx - r, cy - r, r * 2, r * 2);
  ctx.restore();

  // Bezel ring.
  ctx.save();
  ctx.lineWidth = r * 0.075;
  const bezel = ctx.createLinearGradient(cx - r, cy - r, cx + r, cy + r);
  bezel.addColorStop(0, lighten(tone, 0.55));
  bezel.addColorStop(0.5, darken(tone, 0.25));
  bezel.addColorStop(1, lighten(tone, 0.3));
  ctx.strokeStyle = bezel;
  ctx.beginPath();
  ctx.arc(cx, cy, r - ctx.lineWidth / 2, 0, Math.PI * 2);
  ctx.stroke();
  ctx.restore();

  // --- dial ------------------------------------------------------------------
  const dialR = r * 0.8;
  ctx.save();
  ctx.beginPath();
  ctx.arc(cx, cy, dialR, 0, Math.PI * 2);
  const dial = ctx.createRadialGradient(
    cx - dialR * 0.35,
    cy - dialR * 0.4,
    dialR * 0.05,
    cx,
    cy,
    dialR * 1.15,
  );
  dial.addColorStop(0, lighten(darken(tone, 0.55), 0.1));
  dial.addColorStop(0.55, darken(tone, 0.72));
  dial.addColorStop(1, darken(tone, 0.86));
  ctx.fillStyle = dial;
  ctx.fill();
  ctx.restore();

  // Hour markers.
  ctx.save();
  ctx.lineCap = "round";
  for (let i = 0; i < 12; i++) {
    const a = ((i * 30 - 90) * Math.PI) / 180;
    const long = i % 3 === 0;
    const rOuter = dialR * (long ? 0.93 : 0.86);
    const rInner = dialR * 0.72;
    ctx.lineWidth = dialR * (long ? 0.055 : 0.034);
    ctx.strokeStyle = long ? GOLD_SOFT : mix(GOLD_SOFT, tone, 0.35);
    ctx.beginPath();
    ctx.moveTo(cx + Math.cos(a) * rInner, cy + Math.sin(a) * rInner);
    ctx.lineTo(cx + Math.cos(a) * rOuter, cy + Math.sin(a) * rOuter);
    ctx.stroke();
  }
  ctx.restore();

  // Hands, 10:10.
  ctx.save();
  ctx.lineCap = "round";
  ctx.strokeStyle = GOLD_SOFT;
  ctx.shadowColor = "rgba(0,0,0,0.5)";
  ctx.shadowBlur = dialR * 0.12;

  ctx.lineWidth = dialR * 0.075;
  ctx.beginPath();
  ctx.moveTo(cx, cy);
  ctx.lineTo(cx + dialR * 0.44, cy - dialR * 0.44);
  ctx.stroke();

  ctx.lineWidth = dialR * 0.058;
  ctx.beginPath();
  ctx.moveTo(cx, cy);
  ctx.lineTo(cx - dialR * 0.3, cy - dialR * 0.2);
  ctx.stroke();
  ctx.restore();

  // Centre cap.
  ctx.save();
  const cap = ctx.createRadialGradient(
    cx - dialR * 0.03,
    cy - dialR * 0.03,
    0,
    cx,
    cy,
    dialR * 0.09,
  );
  cap.addColorStop(0, lighten(GOLD_SOFT, 0.4));
  cap.addColorStop(1, darken(GOLD, 0.25));
  ctx.fillStyle = cap;
  ctx.beginPath();
  ctx.arc(cx, cy, dialR * 0.08, 0, Math.PI * 2);
  ctx.fill();
  ctx.restore();

  // Crystal sheen.
  ctx.save();
  ctx.beginPath();
  ctx.arc(cx, cy, dialR, 0, Math.PI * 2);
  ctx.clip();
  const sheen = ctx.createRadialGradient(
    cx - dialR * 0.42,
    cy - dialR * 0.46,
    0,
    cx - dialR * 0.42,
    cy - dialR * 0.46,
    dialR * 1.1,
  );
  sheen.addColorStop(0, "rgba(255,255,255,0.22)");
  sheen.addColorStop(0.55, "rgba(255,255,255,0.04)");
  sheen.addColorStop(1, "rgba(255,255,255,0)");
  ctx.fillStyle = sheen;
  ctx.fillRect(cx - dialR, cy - dialR, dialR * 2, dialR * 2);

  // Rim light on the lower-right of the crystal.
  const rim = ctx.createLinearGradient(cx, cy, cx + dialR, cy + dialR);
  rim.addColorStop(0, "rgba(255,255,255,0)");
  rim.addColorStop(1, "rgba(255,255,255,0.13)");
  ctx.fillStyle = rim;
  ctx.fillRect(cx - dialR, cy - dialR, dialR * 2, dialR * 2);
  ctx.restore();
}

function drawBox(ctx: CanvasRenderingContext2D, tone: string): void {
  const w = W * 0.52;
  const h = H * 0.3;
  const cx = W / 2;
  const cy = H * 0.53;
  const left = cx - w / 2;
  const top = cy - h / 2;

  groundShadow(ctx, cx, top + h * 1.32, w * 0.62, h * 0.17, 0.62);

  // --- lid -------------------------------------------------------------------
  const lidH = h * 0.34;
  ctx.save();
  roundRectPath(ctx, left, top - lidH * 0.78, w, lidH * 1.3, lidH * 0.34);
  const lid = ctx.createLinearGradient(left, top - lidH, left + w, top);
  lid.addColorStop(0, lighten(tone, 0.42));
  lid.addColorStop(0.35, lighten(tone, 0.16));
  lid.addColorStop(0.7, darken(tone, 0.12));
  lid.addColorStop(1, darken(tone, 0.34));
  ctx.fillStyle = lid;
  ctx.fill();
  ctx.restore();

  // --- body ------------------------------------------------------------------
  ctx.save();
  roundRectPath(ctx, left, top, w, h, h * 0.14);
  const body = ctx.createLinearGradient(left, top, left + w * 0.5, top + h);
  body.addColorStop(0, lighten(tone, 0.22));
  body.addColorStop(0.28, tone);
  body.addColorStop(0.72, darken(tone, 0.3));
  body.addColorStop(1, darken(tone, 0.55));
  ctx.fillStyle = body;
  ctx.fill();

  // Left-edge highlight.
  ctx.clip();
  const edge = ctx.createLinearGradient(left, 0, left + w * 0.22, 0);
  edge.addColorStop(0, "rgba(255,255,255,0.16)");
  edge.addColorStop(1, "rgba(255,255,255,0)");
  ctx.fillStyle = edge;
  ctx.fillRect(left, top, w, h);

  // Lid seam.
  ctx.fillStyle = "rgba(0,0,0,0.34)";
  ctx.fillRect(left, top, w, h * 0.045);
  ctx.restore();

  // Base shadow for weight.
  ctx.save();
  roundRectPath(ctx, left, top + h * 0.84, w, h * 0.16, h * 0.07);
  ctx.fillStyle = "rgba(0,0,0,0.3)";
  ctx.fill();
  ctx.restore();

  // --- brass clasp -----------------------------------------------------------
  ctx.save();
  const claspW = w * 0.12;
  const claspH = h * 0.2;
  roundRectPath(ctx, cx - claspW / 2, top + h * 0.36, claspW, claspH, h * 0.05);
  const clasp = ctx.createLinearGradient(0, top + h * 0.36, 0, top + h * 0.36 + claspH);
  clasp.addColorStop(0, lighten(GOLD_SOFT, 0.35));
  clasp.addColorStop(0.45, GOLD);
  clasp.addColorStop(1, darken(GOLD, 0.4));
  ctx.fillStyle = clasp;
  ctx.fill();

  // Keyhole.
  ctx.fillStyle = ON_GOLD;
  ctx.globalAlpha = 0.75;
  ctx.beginPath();
  ctx.arc(cx, top + h * 0.44, claspW * 0.13, 0, Math.PI * 2);
  ctx.fill();
  ctx.restore();

  // Lid highlight strip.
  ctx.save();
  roundRectPath(ctx, left + w * 0.04, top - lidH * 0.72, w * 0.92, lidH * 0.14, lidH * 0.06);
  ctx.fillStyle = "rgba(255,255,255,0.14)";
  ctx.fill();
  ctx.restore();
}

function drawAccessory(ctx: CanvasRenderingContext2D, tone: string): void {
  const w = W * 0.52;
  const h = H * 0.17;
  const cx = W / 2;
  const cy = H * 0.5;
  const left = cx - w / 2;
  const top = cy - h / 2;

  groundShadow(ctx, cx, top + h * 1.5, w * 0.54, h * 0.2, 0.6);

  // Lugs.
  ctx.save();
  const lugW = w * 0.1;
  for (const lx of [left - lugW * 0.85, left + w - lugW * 0.75]) {
    roundRectPath(ctx, lx, top + h * 0.24, lugW * 1.6, h * 0.52, lugW * 0.4);
    const lug = ctx.createLinearGradient(lx, top, lx, top + h);
    lug.addColorStop(0, lighten(tone, 0.3));
    lug.addColorStop(1, darken(tone, 0.4));
    ctx.fillStyle = lug;
    ctx.fill();
  }
  ctx.restore();

  // Main band.
  ctx.save();
  roundRectPath(ctx, left, top, w, h, h * 0.34);
  const band = ctx.createLinearGradient(left, 0, left + w, 0);
  band.addColorStop(0, darken(tone, 0.45));
  band.addColorStop(0.18, lighten(tone, 0.2));
  band.addColorStop(0.42, lighten(tone, 0.42));
  band.addColorStop(0.62, tone);
  band.addColorStop(1, darken(tone, 0.5));
  ctx.fillStyle = band;
  ctx.fill();

  // Link separations.
  ctx.strokeStyle = "rgba(0,0,0,0.22)";
  ctx.lineWidth = Math.max(1, h * 0.02);
  for (let i = 1; i < 7; i++) {
    const x = left + (w / 7) * i;
    ctx.beginPath();
    ctx.moveTo(x, top + h * 0.16);
    ctx.lineTo(x, top + h * 0.84);
    ctx.stroke();
  }
  ctx.restore();

  // Gold clasp.
  ctx.save();
  const claspW = w * 0.16;
  roundRectPath(ctx, cx - claspW / 2, top + h * 0.2, claspW, h * 0.6, h * 0.12);
  const clasp = ctx.createLinearGradient(0, top, 0, top + h);
  clasp.addColorStop(0, lighten(GOLD_SOFT, 0.3));
  clasp.addColorStop(0.5, GOLD);
  clasp.addColorStop(1, darken(GOLD, 0.35));
  ctx.fillStyle = clasp;
  ctx.fill();
  ctx.restore();
}

/** Slight edge falloff so the frame reads photographic rather than flat. */
function vignette(ctx: CanvasRenderingContext2D): void {
  const g = ctx.createRadialGradient(
    W / 2,
    H * 0.45,
    Math.min(W, H) * 0.25,
    W / 2,
    H * 0.5,
    Math.max(W, H) * 0.78,
  );
  g.addColorStop(0, "rgba(0,0,0,0)");
  g.addColorStop(1, "rgba(0,0,0,0.34)");
  ctx.fillStyle = g;
  ctx.fillRect(0, 0, W, H);
}

/**
 * Emits the Kotlin id -> gallery mapping.
 *
 * Compose Resources accessors are compile-time symbols, so the mapping has to be
 * written out by hand — which means it can drift from the files on disk. Writing
 * it from the same manifest that produced the images makes that impossible.
 */
function emitKotlinMapping(): void {
  // Group by product id so the lookup returns the whole gallery in one go.
  const byProduct = new Map<string, string[]>();
  const importSet = new Set<string>();

  for (const product of MANIFEST) {
    const shots = product.tones.map((_, i) => fileNameFor(product.id, i, false));
    if (product.tones.length === 1) shots.push(fileNameFor(product.id, 0, true));
    shots.forEach((s) => importSet.add(s));
    byProduct.set(product.id, shots);
  }

  const body = [...byProduct.entries()]
    .map(
      ([id, shots]) =>
        `    "${id}" to listOf(\n` +
        shots.map((s) => `        Res.drawable.${s},`).join("\n") +
        `\n    ),`,
    )
    .join("\n");

  const header = `package dev.watchstore.data.catalog

import dev.watchstore.resources.Res
${[...importSet].sort().map((s) => `import dev.watchstore.resources.${s}`).join("\n")}
import org.jetbrains.compose.resources.DrawableResource

/**
 * Product id -> artwork gallery.
 *
 * GENERATED FILE — do not edit by hand. Run \`bun run tools/gen-mock-images.ts\`.
 *
 * One shot per finish, so choosing a colour swatch changes the photograph.
 * Single-finish products carry a second, closer angle so the detail pager is
 * always swipeable.
 *
 * Returning an empty list is safe: ProductImage falls back to its drawn
 * silhouette, so a product can exist before its artwork does.
 */
private val galleries: Map<String, List<DrawableResource>> = mapOf(
${body}
)

fun galleryFor(id: String): List<DrawableResource> = galleries[id].orEmpty()
`;

  writeFileSync(PRODUCT_IMAGES_KT, header);
  console.log(`Wrote ${PRODUCT_IMAGES_KT.replace(ROOT + "\\", "")}`);
}

// --- main -------------------------------------------------------------------

function fileNameFor(id: string, finishIndex: number, alt: boolean): string {
  const base = `products_${id.replace(/-/g, "_")}_f${finishIndex}`;
  return alt ? `${base}_alt` : base;
}

/** Closer crop used as a second gallery angle. */
const ALT_ZOOM = 1.22;

async function main(): Promise<void> {
  verifyAgainstSeedCatalog();

  if (!existsSync(OUT_DIR)) mkdirSync(OUT_DIR, { recursive: true });

  // Clear previously generated art so renamed/removed products cannot leave
  // stale files behind in the resource folder.
  for (const f of readdirSync(OUT_DIR)) {
    if (f.startsWith("products_") && f.endsWith(".webp")) {
      rmSync(join(OUT_DIR, f), { force: true });
    }
  }

  let bytes = 0;
  let count = 0;

  for (const product of MANIFEST) {
    // One image per finish, so picking a colour swatch changes the photograph.
    // Single-finish products get a second, closer angle so the detail pager is
    // always swipeable.
    const variants: { finish: number; alt: boolean }[] = product.tones.map(
      (_, i) => ({ finish: i, alt: false }),
    );
    if (product.tones.length === 1) {
      variants.push({ finish: 0, alt: true });
    }

    for (const v of variants) {
      const tone = TONES[product.tones[v.finish]];
      if (!tone) {
        throw new Error(`Unknown tone "${product.tones[v.finish]}" for ${product.id}`);
      }

      const canvas = createCanvas(W, H);
      const ctx = canvas.getContext("2d")!;

      drawBackdrop(ctx, tone, product.seed);

      ctx.save();
      if (v.alt) {
        // Zoom about the frame centre so the product reads as a closer shot.
        ctx.translate(W / 2, H / 2);
        ctx.scale(ALT_ZOOM, ALT_ZOOM);
        ctx.translate(-W / 2, -H / 2);
      }
      if (product.kind === "WATCH") drawWatch(ctx, tone);
      else if (product.kind === "BOX") drawBox(ctx, tone);
      else drawAccessory(ctx, tone);
      ctx.restore();

      vignette(ctx);

      const encoded = Buffer.from(await canvas.encode("webp", QUALITY));
      const file = join(OUT_DIR, `${fileNameFor(product.id, v.finish, v.alt)}.webp`);
      writeFileSync(file, encoded);
      bytes += encoded.byteLength;
      count++;
    }

    console.log(
      `  ${product.id.padEnd(22)} ${product.kind.padEnd(10)} ${variants.length} shot(s)`,
    );
  }

  console.log(
    `\nWrote ${count} images to ${OUT_DIR.replace(ROOT + "\\", "")} (${(bytes / 1024 / 1024).toFixed(2)} MB total)`,
  );

  emitKotlinMapping();
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});