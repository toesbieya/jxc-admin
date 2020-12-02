module.exports = {
    success(data, msg) {
        return {status: 200, data, msg}
    },
    fail(msg) {
        return {status: 500, msg: msg || '操作失败'}
    }
}
