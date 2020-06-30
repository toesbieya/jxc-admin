const modulesFiles = require.context('./data', false, /\.js$/)
import baseMixin from './base'

const modules = modulesFiles.keys().reduce((modules, modulePath) => {
    const moduleName = modulePath.replace(/^\.\/(.*)\.\w+$/, '$1')
    const value = modulesFiles(modulePath)
    modules[moduleName] = {
        data() {
            return {
                guideSteps: value.default
            }
        },
        ...baseMixin
    }
    return modules
}, {})

export default modules
