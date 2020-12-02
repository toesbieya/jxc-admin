const CLASS_HIGHLIGHTED_ELEMENT = 'guide-highlighted-element'
const CLASS_POSITION_RELATIVE = 'guide-position-relative'
const CLASS_FIX_STACKING_CONTEXT = 'guide-fix-stacking'

export function getStyleProperty(element, propertyName, prefixVendor = false) {
    if (prefixVendor) {
        const prefixes = ['', '-webkit-', '-ms-', 'moz-', '-o-']
        for (let counter = 0; counter < prefixes.length; counter++) {
            const prefixedProperty = prefixes[counter] + propertyName
            const foundValue = getStyleProperty(element, prefixedProperty)

            if (foundValue) return foundValue
        }

        return ''
    }

    let propertyValue = ''

    if (element.currentStyle) {
        propertyValue = element.currentStyle[propertyName]
    }
    else if (document.defaultView && document.defaultView.getComputedStyle) {
        propertyValue =
            document
                .defaultView
                .getComputedStyle(element, null)
                .getPropertyValue(propertyName)
    }

    return propertyValue && propertyValue.toLowerCase ? propertyValue.toLowerCase() : propertyValue
}

export function removeHighlightClasses(element) {
    element.classList.remove(CLASS_HIGHLIGHTED_ELEMENT)
    element.classList.remove(CLASS_POSITION_RELATIVE)

    const stackFixes = document.querySelectorAll(`.${CLASS_FIX_STACKING_CONTEXT}`)
    for (let counter = 0; counter < stackFixes.length; counter++) {
        stackFixes[counter].classList.remove(CLASS_FIX_STACKING_CONTEXT)
    }
}

export function addHighlightClasses(element) {
    element.classList.add(CLASS_HIGHLIGHTED_ELEMENT)

    if (canMakeRelative(element)) {
        element.classList.add(CLASS_POSITION_RELATIVE)
    }

    fixStackingContext(element)
}

export function getCalculatedPosition(element) {
    const body = document.body
    const documentElement = document.documentElement

    const scrollTop = window.pageYOffset || documentElement.scrollTop || body.scrollTop
    const scrollLeft = window.pageXOffset || documentElement.scrollLeft || body.scrollLeft
    const elementRect = element.getBoundingClientRect()

    return ({
        top: elementRect.top + scrollTop,
        left: elementRect.left + scrollLeft,
        right: elementRect.left + scrollLeft + elementRect.width,
        bottom: elementRect.top + scrollTop + elementRect.height,
    })
}

function canMakeRelative(element) {
    const currentPosition = getStyleProperty(element, 'position')
    const avoidPositionsList = ['absolute', 'fixed', 'relative']

    return avoidPositionsList.indexOf(currentPosition) === -1
}

function fixStackingContext(element) {
    let parentNode = element.parentNode
    while (parentNode) {
        if (!parentNode.tagName || parentNode.tagName.toLowerCase() === 'body') {
            break
        }

        const zIndex = getStyleProperty(parentNode, 'z-index')
        const opacity = parseFloat(getStyleProperty(parentNode, 'opacity'))
        const transform = getStyleProperty(parentNode, 'transform', true)
        const transformStyle = getStyleProperty(parentNode, 'transform-style', true)
        const transformBox = getStyleProperty(parentNode, 'transform-box', true)
        const filter = getStyleProperty(parentNode, 'filter', true)
        const perspective = getStyleProperty(parentNode, 'perspective', true)

        if (
            /[0-9]+/.test(zIndex)
            || opacity < 1
            || (transform && transform !== 'none')
            || (transformStyle && transformStyle !== 'flat')
            || (transformBox && transformBox !== 'border-box')
            || (filter && filter !== 'none')
            || (perspective && perspective !== 'none')
        ) {
            parentNode.classList.add(CLASS_FIX_STACKING_CONTEXT)
        }

        parentNode = parentNode.parentNode
    }
}
