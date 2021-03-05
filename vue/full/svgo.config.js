const { extendDefaultPlugins } = require('svgo');

module.exports = {
    plugins: extendDefaultPlugins([
        {
            name: 'removeAttrs',
            params: {
                attr: '(fill|fill-rule)'
            }
        }
    ])
}
