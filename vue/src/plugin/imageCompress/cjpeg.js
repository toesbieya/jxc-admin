import {createWorker} from "@/utils/worker"

export default function (uint8Array, quality = 75) {
    const url = document.location.protocol + '//' + document.location.host
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
        importScripts(url + '/static/js/cjpeg.min.js')
        const start = performance.now()
        const result = cjpeg(data, ['-quality', quality])
        postMessage({time: performance.now() - start, data: result.data})
    })
}
