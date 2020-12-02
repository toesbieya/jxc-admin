//从quasar处搬运 https://quasar.dev/vue-directives/material-ripple
import './style.scss'

let lastKeyCompositionStatus = false

function isKeyCode(evt, keyCodes) {
    return (
        lastKeyCompositionStatus === true ||
        evt !== Object(evt) ||
        evt.isComposing === true ||
        evt.qKeyEvent === true
    )
        ? false
        : [].concat(keyCodes).includes(evt.keyCode)
}

function css(element, css) {
    let style = element.style

    Object.keys(css).forEach(prop => {
        style[prop] = css[prop]
    })
}

function position(e) {
    if (e.touches && e.touches[0]) {
        e = e.touches[0]
    }
    else if (e.changedTouches && e.changedTouches[0]) {
        e = e.changedTouches[0]
    }
    else if (e.targetTouches && e.targetTouches[0]) {
        e = e.targetTouches[0]
    }

    return {
        top: e.clientY,
        left: e.clientX
    }
}

function stop(e) {
    e.stopPropagation()
}

const listenOpts = {
    hasPassive: false,
    passiveCapture: true,
    notPassiveCapture: true
}

function showRipple(evt, el, ctx, forceCenter) {
    ctx.modifiers.stop === true && stop(evt)

    let {center, color} = ctx.modifiers
    center = center === true || forceCenter === true

    const
        node = document.createElement('span'),
        innerNode = document.createElement('span'),
        pos = position(evt),
        {left, top, width, height} = el.getBoundingClientRect(),
        diameter = Math.sqrt(width * width + height * height),
        radius = diameter / 2,
        centerX = `${(width - diameter) / 2}px`,
        x = center ? centerX : `${pos.left - left - radius}px`,
        centerY = `${(height - diameter) / 2}px`,
        y = center ? centerY : `${pos.top - top - radius}px`

    innerNode.className = 'q-ripple__inner'
    css(innerNode, {
        height: `${diameter}px`,
        width: `${diameter}px`,
        transform: `translate3d(${x},${y},0) scale3d(.2,.2,1)`,
        opacity: 0
    })

    node.className = `q-ripple${color ? ' text-' + color : ''}`
    node.setAttribute('dir', 'ltr')
    node.appendChild(innerNode)
    el.appendChild(node)

    const abort = () => {
        node.remove()
        window.clearTimeout(timer)
    }
    ctx.abort.push(abort)

    let timer = window.setTimeout(() => {
        innerNode.classList.add('q-ripple__inner--enter')
        innerNode.style.transform = `translate3d(${centerX},${centerY},0) scale3d(1,1,1)`
        innerNode.style.opacity = 0.2

        timer = window.setTimeout(() => {
            innerNode.classList.remove('q-ripple__inner--enter')
            innerNode.classList.add('q-ripple__inner--leave')
            innerNode.style.opacity = 0

            timer = window.setTimeout(() => {
                node.remove()
                ctx.abort.splice(ctx.abort.indexOf(abort), 1)
            }, 275)
        }, 250)
    }, 50)
}

function updateCtx(ctx, {value, modifiers, arg}) {
    ctx.enabled = value !== false

    if (ctx.enabled === true) {
        ctx.modifiers = Object(value) === value
            ? {
                stop: value.stop === true || modifiers.stop === true,
                center: value.center === true || modifiers.center === true,
                color: value.color || arg,
                keyCodes: [].concat(value.keyCodes || 13)
            }
            : {
                stop: modifiers.stop,
                center: modifiers.center,
                color: arg,
                keyCodes: [13]
            }
    }
}

export default {
    name: 'ripple',

    inserted(el, binding) {
        const ctx = {
            modifiers: {},
            abort: [],

            click(evt) {
                if (ctx.enabled === true && evt.qSkipRipple !== true && evt.clientX >= 0) {
                    showRipple(evt, el, ctx, evt.qKeyEvent === true)
                }
            },

            keyup(evt) {
                if (
                    ctx.enabled === true &&
                    evt.qSkipRipple !== true &&
                    isKeyCode(evt, ctx.modifiers.keyCodes) === true
                ) {
                    showRipple(evt, el, ctx, true)
                }
            }
        }

        updateCtx(ctx, binding)

        if (el.__qripple) {
            el.__qripple_old = el.__qripple
        }

        const computed = window.getComputedStyle(el)
        if (computed && computed.position === 'static') {
            el.style.position = 'relative'
            el.dataset.previousPosition = 'static'
        }

        el.__qripple = ctx
        el.addEventListener('click', ctx.click, listenOpts.passive)
        el.addEventListener('keyup', ctx.keyup, listenOpts.passive)
    },

    update(el, binding) {
        el.__qripple !== void 0 && updateCtx(el.__qripple, binding)
    },

    unbind(el) {
        const ctx = el.__qripple_old || el.__qripple
        if (ctx !== void 0) {
            ctx.abort.forEach(fn => {
                fn()
            })
            el.removeEventListener('click', ctx.click, listenOpts.passive)
            el.removeEventListener('keyup', ctx.keyup, listenOpts.passive)
            delete el[el.__qripple_old ? '__qripple_old' : '__qripple']
        }
    }
}
