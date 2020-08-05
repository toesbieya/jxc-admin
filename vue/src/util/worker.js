export function createWorker(func, data, callback) {
    const blob = new Blob(['(' + func.toString() + ')()'])
    const url = window.URL.createObjectURL(blob)
    const worker = new Worker(url)

    worker.onmessage = callback
    worker.postMessage(data)

    window.URL.revokeObjectURL(url)

    return worker
}
