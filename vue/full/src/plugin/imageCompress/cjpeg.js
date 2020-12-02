import {createWorker} from "@/util/worker"

export default function (uint8Array, quality = 75) {
    const {protocol, host} = document.location
    const url = `${protocol}//${host}${process.env.BASE_URL}static/js/cjpeg.min.js`
    return new Promise(resolve => {
        let worker = createWorker(handler, {url, data: uint8Array, quality}, ({data}) => {
            resolve(data)
            worker.terminate()
        })
    })
}

function handler() {
    addEventListener('message', e => {
        const {url, data, quality} = e.data
        importScripts(url)
        const start = performance.now()
        const result = cjpeg(data, ['-quality', quality])
        postMessage({time: performance.now() - start, data: result.data})
    })
}
