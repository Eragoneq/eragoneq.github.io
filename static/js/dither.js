// ---- CONFIG ----
const PIXEL_SIZE = 2;
const FLAME_REACH = 0.1;
const INTENSITY = 0.6;
const TURBULENCE = 2.0;
const NOISE_SCALE = 15.0;
// ----------------

const canvas = document.getElementById("c");
const ctx = canvas.getContext("2d");

let perm;
function initPerm(seed) {
  const p = new Uint8Array(256);
  for (let i = 0; i < 256; i++) p[i] = i;
  let s = seed | 0;
  function rand() {
    s = (s * 1664525 + 1013904223) & 0xffffffff;
    return (s >>> 0) / 0xffffffff;
  }
  for (let i = 255; i > 0; i--) {
    const j = (rand() * (i + 1)) | 0;
    [p[i], p[j]] = [p[j], p[i]];
  }
  perm = new Uint8Array(512);
  for (let i = 0; i < 512; i++) perm[i] = p[i & 255];
}

const grads = [
  [1, 1],
  [-1, 1],
  [1, -1],
  [-1, -1],
  [1, 0],
  [-1, 0],
  [0, 1],
  [0, -1],
];
function fade(t) {
  return t * t * t * (t * (t * 6 - 15) + 10);
}
function lerp(a, b, t) {
  return a + (b - a) * t;
}

function perlin(x, y) {
  const xi = Math.floor(x) & 255,
    yi = Math.floor(y) & 255;
  const xf = x - Math.floor(x),
    yf = y - Math.floor(y);
  const u = fade(xf),
    v = fade(yf);
  const aa = perm[perm[xi] + yi],
    ab = perm[perm[xi] + yi + 1];
  const ba = perm[perm[xi + 1] + yi],
    bb = perm[perm[xi + 1] + yi + 1];
  const g = (hash, fx, fy) => {
    const gr = grads[hash & 7];
    return gr[0] * fx + gr[1] * fy;
  };
  return lerp(
    lerp(g(aa, xf, yf), g(ba, xf - 1, yf), u),
    lerp(g(ab, xf, yf - 1), g(bb, xf - 1, yf - 1), u),
    v,
  );
}

function fbm(x, y, octaves) {
  let val = 0,
    amp = 1,
    freq = 1,
    max = 0;
  for (let i = 0; i < octaves; i++) {
    val += perlin(x * freq, y * freq) * amp;
    max += amp;
    amp *= 0.5;
    freq *= 2;
  }
  return val / max;
}

function bayerMatrix(size) {
  if (size === 1) return [0];
  const half = bayerMatrix(size / 2),
    hs = size / 2;
  const out = new Array(size * size);
  for (let y = 0; y < size; y++)
    for (let x = 0; x < size; x++) {
      const base = half[(y % hs) * hs + (x % hs)] * 4;
      const qx = (x / hs) | 0,
        qy = (y / hs) | 0;
      out[y * size + x] =
        base + (qy === 0 ? (qx === 0 ? 0 : 2) : qx === 0 ? 3 : 1);
    }
  return out;
}

const BAYER_SIZE = 8;
const bayerNorm = bayerMatrix(BAYER_SIZE).map(
  (v) => (v + 0.5) / (BAYER_SIZE * BAYER_SIZE),
);

function flameValue(nx, ny) {
  const sx = nx * NOISE_SCALE,
    sy = ny * NOISE_SCALE;
  const warpX = fbm(sx * 0.8, sy * 1.2, 3) * TURBULENCE;
  const warpY = fbm(sx * 0.8 + 50, sy * 1.2 + 50, 3) * TURBULENCE;
  const n = fbm(sx + warpX * 1.5, sy * 2 + warpY * 0.8, 5);
  const noiseVal = n * 0.5 + 0.5;
  const grad = Math.max(0, 1 - nx / FLAME_REACH);
  return Math.min(
    1,
    Math.max(0, Math.pow(grad, 0.8) * (noiseVal * 0.7 + 0.3) * INTENSITY),
  );
}

function render() {
  const w = Math.ceil(window.innerWidth / PIXEL_SIZE);
  const h = Math.ceil(window.innerHeight / PIXEL_SIZE);
  canvas.width = w;
  canvas.height = h;
  canvas.style.width = window.innerWidth + "px";
  canvas.style.height = window.innerHeight + "px";

  initPerm((Math.random() * 999999) | 0);

  const imageData = ctx.createImageData(w, h);
  const data = imageData.data;

  for (let row = 0; row < h; row++) {
    for (let col = 0; col < w; col++) {
      const nx = col / w,
        ny = row / h;

      // Left flame + mirrored right flame, take the brighter of the two
      const leftVal = flameValue(nx, ny);
      const rightVal = flameValue(1 - nx, ny);
      const value = Math.max(leftVal, rightVal);

      const threshold =
        bayerNorm[(row % BAYER_SIZE) * BAYER_SIZE + (col % BAYER_SIZE)];
      const c = value > threshold ? 255 : 0;
      const i = (row * w + col) * 4;
      data[i] = data[i + 1] = data[i + 2] = c;
      data[i + 3] = 255;
    }
  }
  ctx.putImageData(imageData, 0, 0);
}

window.addEventListener("resize", render);
render();
