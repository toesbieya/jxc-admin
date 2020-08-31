import MenuItem from "./component/Menu/MenuItem"
import Submenu from "./component/Menu/Submenu"
import Message from "./component/Message"

const components = [MenuItem, Submenu]

export default function (Vue) {
    components.forEach(component => {
        Vue.component(component.name, component)
    })

    Vue.prototype.$message = Message
}
