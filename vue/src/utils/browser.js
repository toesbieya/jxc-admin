const {body} = document
const WIDTH = 768

export function isMobile() {
    const rect = body.getBoundingClientRect()
    return rect.width - 1 < WIDTH
}
