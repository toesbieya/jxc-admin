/*
* 首页引导步骤
* */

const steps = [
    {
        element: '.radar-chart',
        content: '雷达图',
        forceShowPrevBtn: true,
        prevBtnText: '上一阶段',
        onPrevious() {
            this.$router.push({
                path: '/redirect/index',
                query: {
                    params: JSON.stringify({name: 'index', params: {navbar: 1, guide: 'last'}})
                }
            })
            return false
        }
    },
    {
        element: '.pie-chart',
        content: '饼图'
    },
    {
        element: '.bar-chart',
        content: '柱状图'
    },
]

export default steps
