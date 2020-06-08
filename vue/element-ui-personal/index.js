import Message from "./components/Message"
import Popover from './components/Popover'
import Select from './components/Select/Select'

const components = [Popover, Select]

export default function (Vue) {
    components.forEach(component => {
        Vue.component(component.name, component)
    })

    Vue.prototype.$message = Message
}
