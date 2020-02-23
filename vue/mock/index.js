const {success,fail} = require('./util')
const base = require('../src/config').apiPrefix
const user = require('./data/user')

const routes = [
    {url: '/login', method: 'post', res: user},
]

module.exports = app => routes.forEach(route => app[route.method](base + route.url, (req, res) => res.send(success(route.res))))
