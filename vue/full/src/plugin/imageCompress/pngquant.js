import {createWorker} from "@/util/worker"

export default function (uint8Array, quality = 75, speed = 4) {
    const {protocol, host} = document.location
    const url = `${protocol}//${host}${process.env.BASE_URL}static/js/pngquant.min.js`
    return new Promise(resolve => {
        let worker = createWorker(handler, {url, data: uint8Array, quality, speed}, ({data}) => {
            resolve(data)
            worker.terminate()
        })
    })
}

function handler() {
    addEventListener('message', e => {
        const {url, data, quality, speed} = e.data
        importScripts(url)
        const start = performance.now()
        const result = pngquant(data, {quality: quality.join('-'), speed: speed + ''}, () => ({}))
        postMessage({time: performance.now() - start, data: result.data})
    })
}
