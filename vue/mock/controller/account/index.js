const {success, fail} = require('../../util')

const base = '/account'

module.exports = [
    {
        url: `${base}/login`, method: 'post', res(req, res) {
            const {username, password} = req.body
            //默认账号密码：admin/123456
            if (username !== 'admin' || password !== 'e10adc3949ba59abbe56e057f20f883e') {
                res.send(fail('账号或密码错误'))
            }
            else res.send(success({
                id: 1,
                name: 'admin',
                admin: true,
                token: 'test'
            }))
        }
    },

    {url: `${base}/logout`, method: 'get', res: null}
]
