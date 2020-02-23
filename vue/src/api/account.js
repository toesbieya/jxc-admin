import request from '@/config/request'

export function login(data) {
    return request.post('/login', data).then(({data}) => data.data)
}

export function logout() {
    return request.get('/logout')
}

export function register(data) {
    return request.post('/register', data)
}
