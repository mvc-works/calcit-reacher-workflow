
let path = require('path');
let MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = {
  mode: "development",
  entry: ["./entry/main.css"],
  output: {
    path: path.resolve(__dirname, '../target'),
    filename: 'bundle.js'
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
      filename: '[name].css',
      chunkFilename: '[id].css',
      ignoreOrder: false,
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
