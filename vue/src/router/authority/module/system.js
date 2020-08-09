/*路由表：系统管理*/
import Layout from '@/layout'
import {lazyLoadView} from "@/router/util"

const router = {
    path: '/system',
    component: Layout,
    meta: {title: '系统管理', icon: 'system', alwaysShow: true},
    children: [
        {
            path: 'department',
            name: 'departmentManagement',
            component: lazyLoadView(import('@/view/system/department/indexPage')),
            meta: {title: '部门管理'}
        },
        {
            path: 'role',
            name: 'roleManagement',
            component: lazyLoadView(import('@/view/system/role/indexPage')),
            meta: {title: '角色管理'}
        },
        {
            path: 'user',
            name: 'userManagement',
            component: lazyLoadView(import('@/view/system/user/indexPage')),
            meta: {title: '用户管理'}
        },
        {
            path: 'category',
            name: 'categorySetting',
            component: lazyLoadView(import('@/view/system/category/indexPage')),
            meta: {title: '商品分类'}
        },
        {
            path: 'customer',
            name: 'customerManagement',
            component: lazyLoadView(import('@/view/system/customer/indexPage')),
            meta: {title: '客户管理'}
        },
        {
            path: 'supplier',
            name: 'supplierManagement',
            component: lazyLoadView(import('@/view/system/supplier/indexPage')),
            meta: {title: '供应商管理'}
        }
    ]
}

export default router
