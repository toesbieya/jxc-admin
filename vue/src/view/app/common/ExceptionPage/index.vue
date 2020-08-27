<script type="text/jsx">
function getInfo(type) {
    const base = process.env.BASE_URL
    switch (type) {
        case '403':
            return {
                img: `${base}static/img/403.svg`,
                title: '403',
                desc: '抱歉，你无权访问该页面'
            }
        case '404':
            return {
                img: `${base}static/img/404.svg`,
                title: '404',
                desc: '抱歉，你访问的页面不存在'
            }
        case '500':
            return {
                img: `${base}static/img/500.svg`,
                title: '500',
                desc: '抱歉，服务器出错了'
            }
    }
}

export default {
    name: "ExceptionPage",

    functional: true,

    props: {type: String},

    render(h, context) {
        const {img, title, desc} = getInfo(context.props.type)
        return (
            <div class="exception">
                <img src={img}/>
                <div class="content">
                    <h1>{title}</h1>
                    <div class="desc">{desc}</div>
                    <router-link to="/">
                        <el-button size="small" type="primary">返回首页</el-button>
                    </router-link>
                </div>
            </div>
        )
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
