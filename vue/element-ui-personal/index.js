import Message from "./components/Message"
import Select from './components/Select/Select'

const components = [Select]

export default function (Vue) {
    components.forEach(component => {
        Vue.component(component.name, component)
    })

    Vue.prototype.$message = Message
}
