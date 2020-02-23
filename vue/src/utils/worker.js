export function createWorker(func, initData, c) {
    let blob = new Blob(['(' + func.toString() + ')()'])
    let url = window.URL.createObjectURL(blob)
    let worker = new Worker(url)
    worker.onmessage = c
    worker.postMessage(initData)
    window.URL.revokeObjectURL(url)
    return worker
}
