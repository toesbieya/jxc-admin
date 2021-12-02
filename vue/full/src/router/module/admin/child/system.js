/*路由表：系统管理*/

const router = {
    path: 'system',
    meta: {title: '系统管理', icon: 'svg-system'},
    children: [
        {
            path: 'department',
            name: 'departmentManagement',
            component: 'admin/system/department/',
            meta: {title: '部门管理'}
        },
        {
            path: 'menu',
            name: 'menuManagement',
            component: 'admin/system/menu/',
            meta: {title: '菜单管理', noCache: true}
        },
        {
            path: 'role',
            name: 'roleManagement',
            component: 'admin/system/role/',
            meta: {title: '角色管理'}
        },
        {
            path: 'user',
            name: 'userManagement',
            component: 'admin/system/user/',
            meta: {title: '用户管理'}
        },
        {
            path: 'category',
            name: 'categorySetting',
            component: 'admin/system/category/',
            meta: {title: '商品分类'}
        },
        {
            path: 'customer',
            name: 'customerManagement',
            component: 'admin/system/customer/',
            meta: {title: '客户管理'}
        },
        {
            path: 'supplier',
            name: 'supplierManagement',
            component: 'admin/system/supplier/',
            meta: {title: '供应商管理'}
        }
    ]
}

export default router
