
let path = require('path');
let MiniCssExtractPlugin = require('mini-css-extract-plugin');
let { StatsWriterPlugin } = require("webpack-stats-plugin")

module.exports = {
  mode: "production",
  entry: ["./entry/main.css"],
  output: {
    path: path.resolve(__dirname, '../dist'),
    filename: 'bundle.[hash:8].js'
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [
          {
            loader: MiniCssExtractPlugin.loader,
            options: {
              // publicPath: '../',
            },
          },
          {
            loader: "css-loader",
          },
        ],
      },
      {
        test: /\.(eot|svg|ttf|jpg|png|woff|woff2?)(\?.+)?$/,
        use: [
          {
            loader: "file-loader",
            options: {
              name: "assets/[hash:8].[ext]",
            },
          },
        ]
      }
    ]
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: '[name].[hash:8].css',
      chunkFilename: '[id].[hash:8].css',
      ignoreOrder: false,
    }),
    new StatsWriterPlugin({
      filename: "stats.json"
    }),
  ],
  stats: {
    all: false,
    colors: true,
    errors: true,
    errorDetails: true,
    performance: true,
    reasons: true,
    timings: true,
    warnings: true,
  },
};
