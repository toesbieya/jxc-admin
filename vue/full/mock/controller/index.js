const accountRoutes = require('./account')
const sysResourceRoutes = require('./system/resource')

module.exports = accountRoutes.concat(sysResourceRoutes)
