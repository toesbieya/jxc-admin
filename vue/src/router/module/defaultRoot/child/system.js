/*路由表：系统管理*/

const router = {
    path: 'system',
    meta: {title: '系统管理', icon: 'svg-system', alwaysShow: true},
    children: [
        {
            path: 'department',
            name: 'departmentManagement',
            component: 'system/department/',
            meta: {title: '部门管理'}
        },
        {
            path: 'menu',
            name: 'menuManagement',
            component: 'system/menu/',
            meta: {title: '菜单管理', noCache: true}
        },
        {
            path: 'role',
            name: 'roleManagement',
            component: 'system/role/',
            meta: {title: '角色管理'}
        },
        {
            path: 'user',
            name: 'userManagement',
            component: 'system/user/',
            meta: {title: '用户管理'}
        },
        {
            path: 'category',
            name: 'categorySetting',
            component: 'system/category/',
            meta: {title: '商品分类'}
        },
        {
            path: 'customer',
            name: 'customerManagement',
            component: 'system/customer/',
            meta: {title: '客户管理'}
        },
        {
            path: 'supplier',
            name: 'supplierManagement',
            component: 'system/supplier/',
            meta: {title: '供应商管理'}
        }
    ]
}

export default router
