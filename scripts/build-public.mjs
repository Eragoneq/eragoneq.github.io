import path from "node:path";
import { fileURLToPath } from "node:url";
import { spawn } from "node:child_process";
import webpack from "webpack";

import webpackConfig from "../webpack.config.mjs";

const projectRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");

function generateProductionSource() {
  const isWindows = process.platform === "win32";
  const command = isWindows ? process.env.ComSpec ?? "cmd.exe" : "./gradlew";
  const args = isWindows
    ? ["/d", "/c", ".\\gradlew.bat", "generateProductionSource"]
    : ["generateProductionSource"];

  return new Promise((resolve, reject) => {
    const child = spawn(command, args, {
      cwd: projectRoot,
      stdio: "inherit",
      windowsHide: true,
    });

    child.once("error", reject);
    child.once("exit", (code, signal) => {
      if (code === 0) {
        resolve();
        return;
      }

      const reason = signal ? `signal ${signal}` : `exit code ${code}`;
      reject(new Error(`Gradle source generation failed with ${reason}.`));
    });
  });
}

function bundlePublicSite() {
  return new Promise((resolve, reject) => {
    webpack(webpackConfig, (error, stats) => {
      if (error) {
        reject(error);
        return;
      }

      if (!stats || stats.hasErrors()) {
        reject(new Error(stats?.toString({
          all: false,
          errorDetails: true,
          errors: true,
          warnings: true,
        }) ?? "Webpack completed without build statistics."));
        return;
      }

      console.log(stats.toString({
        all: false,
        assets: true,
        colors: process.stdout.isTTY,
        timings: true,
        warnings: true,
      }));
      resolve();
    });
  });
}

async function main() {
  await generateProductionSource();
  await bundlePublicSite();
  console.log("Production site ready in public/.");
}

main().catch((error) => {
  console.error(error instanceof Error ? error.message : error);
  process.exitCode = 1;
});
