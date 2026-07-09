# postcss-px2rem-exclude

Based on [postcss-px2rem](https://www.npmjs.com/package/postcss-px2rem) added the exclude folder option.

[![Downloads][downloads-image]][downloads-url]

[downloads-image]: https://img.shields.io/npm/dm/postcss-px2rem-exclude.svg?style=flat-square
[downloads-url]: https://www.npmjs.com/package/postcss-px2rem-exclude

## Useage

### .postcssrc.js
```javascript
module.exports = {
  'plugins': {
    'postcss-px2rem-exclude': {
      remUnit: 75,
      exclude: /node_modules|folder_name/i
    }
  }
}
```
