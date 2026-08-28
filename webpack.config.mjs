import path from "node:path";
import { fileURLToPath } from "node:url";

import CopyPlugin from "copy-webpack-plugin";
import CssMinimizerPlugin from "css-minimizer-webpack-plugin";
import HtmlMinimizerPlugin from "html-minimizer-webpack-plugin";
import MiniCssExtractPlugin from "mini-css-extract-plugin";

const projectRoot = path.dirname(fileURLToPath(import.meta.url));
const generatedSite = path.join(projectRoot, "build", "generated-site");

export default {
  mode: "production",
  context: generatedSite,
  entry: {
    site: [
      path.join(generatedSite, "static", "css", "main.css"),
      path.join(generatedSite, "static", "js", "script.js"),
    ],
  },
  output: {
    clean: true,
    filename: "static/js/script.js",
    path: path.join(projectRoot, "public"),
  },
  module: {
    rules: [
      {
        test: /\.css$/i,
        use: [MiniCssExtractPlugin.loader, "css-loader"],
      },
    ],
  },
  optimization: {
    minimize: true,
    minimizer: [
      "...",
      new CssMinimizerPlugin(),
      new HtmlMinimizerPlugin(),
    ],
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: "static/css/main.css",
      runtime: false,
    }),
    new CopyPlugin({
      patterns: [
        {
          context: generatedSite,
          from: "**/*",
          globOptions: {
            dot: true,
            ignore: [
              "**/static/css/**",
              "**/static/js/script.js",
            ],
          },
        },
      ],
    }),
  ],
  performance: {
    hints: false,
  },
};
