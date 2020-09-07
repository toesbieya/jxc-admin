/*
* 导航栏引导步骤
* */

const steps = [
    {
        element: '.setting-btn.navbar-item',
        content: '这是个性设置按钮，可以根据自己喜好进行一些设置',
    },
    {
        element: '.navbar .el-dropdown.navbar-item',
        content: '这是用户中心',
    },
    {
        element: '.tags-view-container',
        content: `<p>这是tab栏，可以右键tab页进行相关操作</p>
                  <p>ctrl + ← → 可以进行tab页的左右切换</p>
                  <p>当tab过多时通过鼠标滚轮来滚动</p>
                  <p>双击可以关闭</p>`,
    },
]

export default steps
