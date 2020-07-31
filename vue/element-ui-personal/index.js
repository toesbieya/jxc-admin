import MenuItem from "./components/Menu/MenuItem"
import Submenu from "./components/Menu/Submenu"
import Message from "./components/Message"
//import Select from './components/Select/Select'

const components = [MenuItem, Submenu]

export default function (Vue) {
    components.forEach(component => {
        Vue.component(component.name, component)
    })

    Vue.prototype.$message = Message
}
