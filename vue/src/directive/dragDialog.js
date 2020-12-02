export default {
    bind(el, bind, vnode) {
        const dialogHeaderEl = el.querySelector('.el-dialog__header')
        const dragDom = el.querySelector('.el-dialog')

        dialogHeaderEl.style.setProperty('cursor', 'move')

        dialogHeaderEl.addEventListener('mousedown', e => {
            // 鼠标按下，计算当前元素距离可视区的距离
            const disX = e.clientX - dialogHeaderEl.offsetLeft
            const disY = e.clientY - dialogHeaderEl.offsetTop

            const dragDomWidth = dragDom.offsetWidth
            const dragDomHeight = dragDom.offsetHeight

            const screenWidth = document.body.clientWidth
            const screenHeight = document.body.clientHeight

            const minDragDomLeft = dragDom.offsetLeft
            const maxDragDomLeft = screenWidth - minDragDomLeft - dragDomWidth

            const minDragDomTop = dragDom.offsetTop
            const maxDragDomTop = screenHeight - minDragDomTop - dragDomHeight

            // 获取到的值带px 正则匹配替换
            let {left: styL, top: styT} = window.getComputedStyle(dragDom)

            if (styL.includes('%')) {
                styL = +document.body.clientWidth * (+styL.replace(/%/g, '') / 100)
                styT = +document.body.clientHeight * (+styT.replace(/%/g, '') / 100)
            }
            else {
                styL = +styL.replace(/px/g, '')
                styT = +styT.replace(/px/g, '')
            }

            function onMousemove(e) {
                // 通过事件委托，计算移动的距离
                let left = e.clientX - disX
                let top = e.clientY - disY

                // 边界处理
                if (-(left) > minDragDomLeft) {
                    left = -minDragDomLeft
                }
                else if (left > maxDragDomLeft) {
                    left = maxDragDomLeft
                }

                if (-(top) > minDragDomTop) {
                    top = -minDragDomTop
                }
                else if (top > maxDragDomTop) {
                    top = maxDragDomTop
                }

                // 移动当前元素
                dragDom.style.setProperty('left', `${left + styL}px`)
                dragDom.style.setProperty('top', `${top + styT}px`)
            }

            function onMouseup() {
                window.removeEventListener('mousemove', onMousemove)
                window.removeEventListener('mouseup', onMouseup)
            }

            window.addEventListener('mousemove', onMousemove)
            window.addEventListener('mouseup', onMouseup)
        })

        function resetStyle() {
            dragDom.style.removeProperty('left')
            dragDom.style.removeProperty('top')
        }

        //当dialog关闭时恢复原位
        vnode.componentInstance.$on('closed', resetStyle)
        vnode.componentInstance.$once('hook:beforeDestroy', () => {
            vnode.componentInstance.$off('closed', resetStyle)
        })
    }
}
