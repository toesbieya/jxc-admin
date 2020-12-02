<script type="text/jsx">
//样式搬的https://antdv.com/component/skeleton-cn/
import QSkeleton from '@/component/Skeleton'

export default {
    name: "SkeletonPage",

    functional: true,

    render() {
        const single = (
            <div class="skeleton-page">
                <div class="skeleton-page__header">
                    <QSkeleton type="circle"/>
                </div>
                <div class="skeleton-page__content">
                    <QSkeleton tag="h3"/>
                    <ul class="skeleton-page__paragraph">{new Array(3).fill(<QSkeleton/>)}</ul>
                </div>
            </div>
        )

        return <div class="skeleton-page-wrapper">{new Array(4).fill(single)}</div>
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
