<script type="text/jsx">
//样式搬的https://antdv.com/components/skeleton-cn/
import QSkeleton from './index'

export default {
    name: "PageSkeleton",

    functional: true,

    render() {
        const single = (
            <div class="page-skeleton">
                <div class="page-skeleton__header">
                    <QSkeleton type="circle"/>
                </div>
                <div class="page-skeleton__content">
                    <QSkeleton tag="h3"/>
                    <ul class="page-skeleton__paragraph">{new Array(3).fill(<QSkeleton/>)}</ul>
                </div>
            </div>
        )

        return <div class="page-skeleton-wrapper">{new Array(4).fill(single)}</div>
    }
}
</script>

<style lang="scss">
@import "~@/assets/styles/variables.scss";

.page-skeleton-wrapper {
    margin: $page-view-margin;
}

.page-skeleton {
    width: 100%;
    display: table;

    & + & {
        margin-top: 20px;
    }

    &__header,
    &__content {
        display: table-cell;
    }

    &__header {
        padding-top: 2px;
        padding-right: 16px;
        vertical-align: top;
    }

    &__content {
        width: 100%;

        > .q-skeleton {
            width: 50%;
        }

        .page-skeleton__paragraph {
            margin-top: 28px;
            padding: 0;

            .q-skeleton:last-child:not(:first-child):not(:nth-child(2)) {
                width: 61%;
            }

            .q-skeleton + .q-skeleton {
                margin-top: 16px;
            }
        }
    }
}
</style>
