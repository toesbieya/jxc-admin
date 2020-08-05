import BottomTip from './BottomTip'
import Guide from './Guide'
import ImageViewer from './ImageViewer'
import PuzzleVerify from './PuzzleVerify'
import Signature from './SignautreBoard'

export default function (Vue) {
    Vue.prototype.$bottomTip = BottomTip
    Vue.prototype.$guide = Guide
    Vue.prototype.$image = ImageViewer
    Vue.prototype.$puzzleVerify = PuzzleVerify
    Vue.prototype.$signature = Signature
}
