<template>
    <div :style="orgTreeStyle" class="org-tree-wrapper" @mousedown="mousedownView">
        <org-tree :data="data" :horizontal="horizontal" collapsable expand-all @on-node-click="nodeClick">
            <div
                slot-scope="data"
                :class="{'has-children-label':data.children && data.children.length}"
                class="custom-org-node"
                @contextmenu.prevent="e => $emit('node-contextmenu',e,data)"
            >
                {{ data.label }}
            </div>
        </org-tree>
    </div>
</template>

<script>
import OrgTree from '@/component/OrgTree'

export default {
    name: "OrgTreeView",

    components: {OrgTree},

    props: {
        data: {type: Object, default: () => ({})},
        horizontal: Boolean,
        collapsable: Boolean,
        zoom: {type: Number, default: 1},
    },

    data() {
        return {
            orgTreeOffsetLeft: 0,
            orgTreeOffsetTop: 0,
            initPageX: 0,
            initPageY: 0,
            oldMarginLeft: 0,
            oldMarginTop: 0,
            canMove: false,
            expandAll: true
        }
    },

    computed: {
        orgTreeStyle() {
            return {
                transform: `translate(-50%, -50%) scale(${this.zoom}, ${this.zoom})`,
                marginLeft: `${this.orgTreeOffsetLeft}px`,
                marginTop: `${this.orgTreeOffsetTop}px`
            }
        }
    },

    methods: {
        nodeClick(e, data, expand) {
            expand()
        },

        mousedownView(event) {
            this.canMove = true
            this.initPageX = event.pageX
            this.initPageY = event.pageY
            this.oldMarginLeft = this.orgTreeOffsetLeft
            this.oldMarginTop = this.orgTreeOffsetTop
            window.addEventListener('mousemove', this.mousemoveView)
            window.addEventListener('mouseup', this.mouseupView)
        },
        mousemoveView(event) {
            if (!this.canMove) return
            const {pageX, pageY} = event
            this.orgTreeOffsetLeft = this.oldMarginLeft + pageX - this.initPageX
            this.orgTreeOffsetTop = this.oldMarginTop + pageY - this.initPageY
        },
        mouseupView() {
            this.canMove = false
            window.removeEventListener('mousemove', this.mousemoveView)
            window.removeEventListener('mouseup', this.mouseupView)
        }
    }
}
</script>

<style lang="scss">
.org-tree-wrapper {
    cursor: move;
    display: inline-block;
    position: absolute;
    left: 50%;
    top: 50%;
    transition: transform 0.2s ease-out;
    z-index: 2;

    .org-tree-node-label {
        .org-tree-node-label-inner {
            padding: 0;

            .custom-org-node {
                padding: 14px 41px;
                background: #738699;
                user-select: none;
                word-wrap: normal;
                white-space: nowrap;
                border-radius: 4px;
                color: #ffffff;
                font-size: 14px;
                font-weight: 500;
                line-height: 20px;
                transition: background 0.1s ease-in;
                cursor: default;

                &:hover {
                    background: #5d6c7b;
                    transition: background 0.1s ease-in;
                }

                &.has-children-label {
                    cursor: pointer;
                }
            }
        }
    }

    .horizontal .custom-org-node {
        padding: 14px 19px;
    }
}
</style>
