import { copyFile, mkdir, readFile, stat, writeFile } from "node:fs/promises";
import path from "node:path";

const root = path.resolve(import.meta.dirname, "../../../..");
const destination = path.join(import.meta.dirname, "public");
const source = path.join(root, ".verification/gravigo-product-promo/assets");
await mkdir(destination, { recursive: true });
for (const file of [
  "01-conversation.png",
  "02-instruction.png",
  "03-plan-ready.png",
  "04-plan.png",
  "native-demo.mp4",
]) {
  await copyFile(path.join(source, file), path.join(destination, file));
}
await copyFile(
  path.join(
    root,
    "android/app/src/main/res/drawable-nodpi/multigravity_mark.png",
  ),
  path.join(destination, "brand.png"),
);
for (const [name, url] of [
  [
    "NotoSansSC.ttf",
    "https://raw.githubusercontent.com/google/fonts/main/ofl/notosanssc/NotoSansSC%5Bwght%5D.ttf",
  ],
  [
    "NotoSansSC-OFL.txt",
    "https://raw.githubusercontent.com/google/fonts/main/ofl/notosanssc/OFL.txt",
  ],
  [
    "Manrope.ttf",
    "https://raw.githubusercontent.com/google/fonts/main/ofl/manrope/Manrope%5Bwght%5D.ttf",
  ],
  [
    "Manrope-OFL.txt",
    "https://raw.githubusercontent.com/google/fonts/main/ofl/manrope/OFL.txt",
  ],
]) {
  try {
    if ((await stat(path.join(destination, name))).size > 0) continue;
  } catch (error) {
    if (error.code !== "ENOENT") throw error;
  }
  const response = await fetch(url, { signal: AbortSignal.timeout(120000) });
  if (!response.ok)
    throw new Error(`Asset download failed: ${response.status} ${name}`);
  await writeFile(
    path.join(destination, name),
    Buffer.from(await response.arrayBuffer()),
  );
}
await writeFile(
  path.join(destination, "SOURCES.txt"),
  "Native UI: isolated GraviGo ProductPromoCaptureTest demonstration.\nBrand: approved GraviGo icon.\nFonts: Google Fonts, original SIL OFL files included.\n" +
    (
      await readFile(path.join(destination, "NotoSansSC-OFL.txt"), "utf8")
    ).slice(0, 250),
);
console.log(
  "Prepared native demonstration media, approved brand, and licensed fonts.",
);
