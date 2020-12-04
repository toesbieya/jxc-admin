import ImageViewer from './ImageViewer'
import Verify from './Verify'

export default function (Vue) {
    Vue.prototype.$image = ImageViewer
    Vue.prototype.$verify = Verify
}
