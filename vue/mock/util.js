module.exports = {
    success(data) {
        return {status: 200, data}
    },
    fail() {
        return {status: 500, msg: '操作失败'}
    }
}
