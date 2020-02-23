/*路由表：系统管理*/
import Layout from '@/layout'

const router = {
    path: '/system',
    component: Layout,
    alwaysShow: true,
    meta: {title: '系统管理', icon: 'system', breadcrumb: false},
    children: [
        {
            path: 'department',
            name: 'departmentManagement',
            component: () => import('@/views/system/department'),
            meta: {title: '部门管理'}
        },
        {
            path: 'role',
            name: 'roleManagement',
            component: () => import('@/views/system/role'),
            meta: {title: '角色管理'}
        },
        {
            path: 'user',
            name: 'userManagement',
            component: () => import('@/views/system/user'),
            meta: {title: '用户管理'}
        },
        {
            path: 'category',
            name: 'categorySetting',
            component: () => import('@/views/system/category'),
            meta: {title: '商品分类'}
        },
        {
            path: 'customer',
            name: 'customerManagement',
            component: () => import('@/views/system/customer'),
            meta: {title: '客户管理'}
        },
        {
            path: 'supplier',
            name: 'supplierManagement',
            component: () => import('@/views/system/supplier'),
            meta: {title: '供应商管理'}
        },
        {
            path: 'resource',
            name: 'resourceManagement',
            component: () => import('@/views/system/resource'),
            hidden: true,
            meta: {title: '接口设置'}
        },
        {
            path: 'monitor',
            name: 'systemMonitor',
            component: () => import('@/views/system/monitor'),
            hidden: true,
            meta: {title: '系统监控'}
        }
    ]
}

export default router
