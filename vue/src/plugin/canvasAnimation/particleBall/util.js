const util = {
    norm: function (value, min, max) {
        return (value - min) / (max - min)
    },
    lerp: function (norm, min, max) {
        return min + norm * (max - min)
    },
    map: function (value, sourceMin, sourceMax, destMin, destMax) {
        return this.lerp(this.norm(value, sourceMin, sourceMax), destMin, destMax)
    }
}

export default util
