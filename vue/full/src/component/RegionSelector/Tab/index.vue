<template>
    <el-select
        ref="select"
        :value="value"
        :disabled="readonly"
        :size="size"
        popper-append-to-body
        clearable
        @clear="() => $emit('clear')"
        @visible-change="visibleChange"
    >
        <template v-slot:empty>
            <div class="rg-header">
                <h3>行政区划选择</h3>

                <button class="rg-removeall-button" type="button" title="清除已选" @click="removeAll">
                    <i class="el-icon-delete"/>
                </button>

                <button class="rg-done-button" type="button" title="完成" @click="done">
                    <i class="el-icon-check"/>
                </button>
            </div>

            <div class="rg-search">
                <input
                    ref="searchInput"
                    v-model="searchText"
                    class="rg-input"
                    type="text"
                    placeholder="输入地区id或名称搜索"
                    @input="search"
                >
            </div>

            <div class="rg-level-tabs">
                <ul>
                    <li v-for="(tab,index) in selected" :key="tab.name" :class="{active:index+1 === currentLevel}">
                        <a href="javascript:" @click="() => clickTab(index,tab)">{{ tab.name }}</a>
                    </li>
                </ul>
            </div>

            <div v-loading="loading" class="rg-results-container">
                <ul class="rg-results">
                    <li
                        v-for="item in items"
                        :key="item.id"
                        :class="{'rg-item': true, 'active': isItemActive(item.id)}"
                        @click="() => clickItem(item)"
                    >
                        {{ limit ? item.name + `(${item.value})` : item.name }}
                    </li>
                    <li v-if="items.length === 0" class="rg-message-box">无匹配项目</li>
                </ul>
            </div>
        </template>
    </el-select>
</template>

<script>
import {debounce, deepClone} from "@/util"
import {createLimitTree, getNodeId} from "@/util/tree"
import {store, init} from '../store'
import common from '../mixin'

const PROVINCE_CITIES = [
    {id: '11', name: '北京市'},
    {id: '12', name: '天津市'},
    {id: '31', name: '上海市'},
    {id: '50', name: '重庆市'}
]
const DEFAULT_TABS = [{name: '省/直辖市'}, {name: '市'}, {name: '区/县'}, {name: '乡/镇/街道'}]

//根据node的类型生成查找断言，支持{id:'10',...}、{name:'北京市',...}、'10'、'北京市' 等四种类型
function predicate(node) {
    if (typeof node === 'object') {
        const key = node.hasOwnProperty('id') ? 'id' : 'name'
        return item => item[key] === node[key]
    }
    else {
        const firstCharCode = node.charCodeAt(0)
        const key = 48 <= firstCharCode && firstCharCode <= 57 ? 'id' : 'name'
        return item => item[key] === node
    }
}

//根据叶子id获取树节点
function topDownById(tree, id) {
    //判断深度，深度=id.length/2
    const depth = Math.floor(id.length / 2)
    if (depth < 1) return []
    const result = []
    for (let i = 1; i <= depth; i++) {
        const parentId = id.substring(0, i * 2)
        const parent = tree.find(node => node.id === parentId)
        if (!parent) return result
        result.push(parent)
        if (i === 1 && PROVINCE_CITIES.some(i => i.id === parentId)) {
            i++
        }
        tree = parent.children || []
    }
    return result
}

export default {
    name: "RegionTabSelector",

    mixins: [common],

    props: {
        value: [String, Array],
        maxLevel: {type: Number, default: 3},
        separation: {type: String, default: ','}
    },

    data() {
        return {
            loading: false,
            searchText: '',
            currentLevel: 1,
            realMaxLevel: this.maxLevel,
            selected: [],
            treeData: [],
            items: []
        }
    },

    computed: {
        regionTree() {
            return store.data
        },
    },

    watch: {
        currentLevel(v) {
            this.setItems(v)
        }
    },

    methods: {
        //select的下拉框展开收起
        visibleChange(v) {
            if (v) {
                this.transformValueToSelected()
                this.$nextTick(() => this.$refs.searchInput.focus())
            }
            else this.removeAll()
        },

        search() {
            const parent = this.selected[this.currentLevel - 2] || {children: this.treeData}

            if (!this.searchText) return this.items = parent.children

            //以0-9开头的的搜索词都根据id去查
            const firstCharCode = this.searchText.charCodeAt(0)
            const useId = 48 <= firstCharCode && firstCharCode <= 57
            const predicate = (
                () => useId
                    ? item => item.id.startsWith(this.searchText)
                    : item => item.name.startsWith(this.searchText)
            )()

            this.items = parent.children.filter(predicate)
        },

        clickTab(index, tab) {
            //点击当前激活的tab时不处理
            if (index + 1 === this.currentLevel) return

            this.currentLevel = index + 1
        },

        clickItem(item) {
            this.setTabs(item, this.currentLevel)
            this.nextTab()
        },

        setTabs(item, level) {
            const prev = this.selected.slice(0, level - 1)
            prev.push(item)
            const next = deepClone(DEFAULT_TABS)

            //选择省份时
            if (level === 1) {
                //选择直辖市的时候，最大深度-1，移除'市'tab
                if (PROVINCE_CITIES.some(i => i.id === item.id)) {
                    next.splice(1, 1)
                    this.realMaxLevel = this.maxLevel - 1
                }
                else this.realMaxLevel = this.maxLevel
            }

            this.selected = prev.concat(next.slice(level, this.realMaxLevel))
        },

        nextTab() {
            this.searchText = ''
            //若选择的是最后一级节点，直接完成
            if (this.currentLevel >= this.realMaxLevel) {
                this.done()
            }
            else this.currentLevel++
        },

        //判断是否是已选项
        isItemActive(id) {
            const selected = this.selected[this.currentLevel - 1]
            return selected && selected.id === id
        },

        //根据激活的tab改变显示的行政区划
        setItems(level) {
            //顶级节点返回完整的树
            if (level === 1) this.items = this.treeData

            //否则返回上一级节点的children
            else {
                const parent = this.selected[level - 2]
                this.items = parent ? parent.children || [] : []
            }
        },

        //将传入的value转换为已选项
        transformValueToSelected() {
            if (!this.value) return

            const result = [],
                loopArray = Array.isArray(this.value)
                    ? this.value
                    : this.value.split(this.separation).filter(Boolean)

            for (let i = 0; i < loopArray.length; i++) {
                const str = loopArray[i], parent = result[i - 1] || {children: this.treeData}
                const node = parent.children.find(predicate(str))

                //只要有一次不满足就退出，保留之前的查找结果
                if (!node) break

                result.push(node)
            }

            result.forEach((item, index) => this.setTabs(item, index + 1))
        },

        removeAll() {
            this.realMaxLevel = this.maxLevel
            this.selected = deepClone(DEFAULT_TABS)
            this.currentLevel = 1
            this.searchText = ''
            this.setItems(this.currentLevel)
        },

        done() {
            const result = this.selected.filter(item => item.id)
            this.$emit('input', result.map(item => item.name).join(this.separation))

            if (result.length === 0) {
                this.$emit('select', {}, [])
                return this.$refs.select.blur()
            }

            const node = result[result.length - 1]
            const payload = [node]

            if (this.getChildrenOnSelect) {
                const ids = getNodeId(node.children)
                ids.unshift(node.id)
                payload.push(ids)
            }

            this.$emit('select', ...payload)

            this.$refs.select.blur()
        },

        init() {
            this.loading = true
            const hasInit = this.regionTree.length > 0
            const promise = () => hasInit ? Promise.resolve() : init(this.regionDataUrl)
            return promise()
                .then(() => this.limit ? this.limitApi() : Promise.resolve())
                .then(data => {
                    this.treeData = data ? createLimitTree(this.regionTree, data) : this.regionTree
                    this.setItems(this.currentLevel)
                })
                .finally(() => this.loading = false)
        }
    },

    created() {
        this.search = debounce(this.search, 200)
        this.selected = deepClone(DEFAULT_TABS)
    },

    mounted() {
        this.init()
    }
}
</script>

<style lang="scss" src="./style.scss"></style>
