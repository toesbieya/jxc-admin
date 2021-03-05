const bodeParser = require('body-parser')
const {success} = require('./util')
const {apiPrefix} = require('../src/config')

const routes = require('./controller')

module.exports = app => {
    app.use(bodeParser.json())

    routes.forEach(route => {
        app[route.method](
            apiPrefix + route.url,
            (req, res) => {
                if (typeof route.res === 'function') {
                    route.res(req, res)
                }
                else res.send(success(route.res))
            })
    })
}
