let insertEl = false

const image = function ({index = 0, urlList}) {
    if (!window.lightGallery || !Array.isArray(urlList) || urlList.length < 1) {
        return
    }

    if (!insertEl) {
        const div = document.createElement('div')
        div.setAttribute('id', 'light-gallery-el')
        div.style.display = 'none'
        document.body.appendChild(div)
        div.addEventListener(
            'onCloseAfter',
            () => window.lgData[div.getAttribute('lg-uid')].destroy(true),
            false
        )
        insertEl = true
    }

    window.lightGallery(document.getElementById('light-gallery-el'), {
        index,
        download: false,
        dynamic: true,
        dynamicEl: urlList.map(src => ({src, thumb: src}))
    })
}

image.close = function () {
    if (!insertEl) return
    window.lgData[insertEl.getAttribute('lg-uid')].destroy(true)
}

export default image
