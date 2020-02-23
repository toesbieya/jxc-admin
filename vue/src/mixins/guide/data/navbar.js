/*
* 导航栏引导步骤
* */

const steps = [
    {
        element: '#header-search',
        content: '这是搜索栏，可以根据关键字搜索菜单',
    },
    {
        element: '.fullscreen-btn.right-menu-item',
        content: '这是全屏按钮',
    },
    {
        element: '.lock-btn.right-menu-item',
        content: '这是锁屏按钮',
    },
    {
        element: '.setting-btn.right-menu-item',
        content: '这是个性设置按钮，可以根据自己喜好进行一些设置',
    },
    {
        element: '.avatar-container.right-menu-item',
        content: '这是用户中心',
    },
    {
        element: '.tags-view-container',
        content: `<p>这是tab栏，可以右键tab页进行相关操作</p>
                  <p>ctrl + ← → 可以进行tab页的左右切换</p>
                  <p>当tab过多时通过鼠标滚轮来滚动</p>
                  <p>非固定的tab可以拖动排序</p>`,
        /*forceShowNextBtn: true,
        nextBtnText: '下一阶段',
        onNext() {
            this.$router.push({
                path: '/redirect/index',
                query: {
                    params: JSON.stringify({name: 'index', params: {guide: 'first'}})
                }
            })
            return false
        }*/
    },
]

export default steps
