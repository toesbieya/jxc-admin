function getColorChannels(color) {
    color = color.replace('#', '')
    if (/^[0-9a-fA-F]{3}$/.test(color)) {
        color = color.split('')
        for (let i = 2; i >= 0; i--) {
            color.splice(i, 0, color[i])
        }
        color = color.join('')
    }
    if (/^[0-9a-fA-F]{6}$/.test(color)) {
        return {
            red: parseInt(color.slice(0, 2), 16),
            green: parseInt(color.slice(2, 4), 16),
            blue: parseInt(color.slice(4, 6), 16)
        }
    }
    else {
        return {
            red: 255,
            green: 255,
            blue: 255
        }
    }
}

export function mixColor(color, percent) {
    let {red, green, blue} = getColorChannels(color)
    if (percent > 0) { // shade given color
        red *= 1 - percent
        green *= 1 - percent
        blue *= 1 - percent
    }
    else { // tint given color
        red += (255 - red) * percent
        green += (255 - green) * percent
        blue += (255 - blue) * percent
    }
    return `rgb(${Math.round(red)}, ${Math.round(green)}, ${Math.round(blue)})`
}
