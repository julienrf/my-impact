var config = require('./scalajs.webpack.config');

config.module = {
  rules: [{
    test: /\.css$/,
    use: ['style-loader', 'css-loader']
  }]
};

module.exports = config;
