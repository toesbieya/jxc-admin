import BottomTip from './BottomTip'
import Guide from './Guide'
import ImageViewer from './ImageViewer'
import Verify from './Verify'
import Signature from './SignautreBoard'

export default function (Vue) {
    Vue.prototype.$bottomTip = BottomTip
    Vue.prototype.$guide = Guide
    Vue.prototype.$image = ImageViewer
    Vue.prototype.$verify = Verify
    Vue.prototype.$signature = Signature
}
