<template>
    <el-drawer
        :visible="visible"
        :with-header="false"
        custom-class="setting-drawer"
        append-to-body
        size="300px"
        @close="visible = false"
    >
        <el-divider>导航模式</el-divider>
        <div class="setting-drawer-item">
            <checkbox-group :value="setting.app.navMode" @input="v => onChange('app/navMode', v)">
                <img-checkbox
                    v-for="{label, value, img} in navModes"
                    :key="value"
                    :label="label"
                    v-model="value"
                    :img="img"
                />
            </checkbox-group>
        </div>

        <el-divider>顶栏设置</el-divider>
        <div class="setting-drawer-item">
            <span>主题风格</span>
            <el-select
                :value="setting.header.theme"
                @input="v => onChange('header/theme', v)"
            >
                <el-option value="light" label="亮色"/>
                <el-option value="dark" label="暗色"/>
            </el-select>
        </div>

        <el-divider>侧边栏设置</el-divider>
        <div class="setting-drawer-item">
            <span>主题风格</span>
            <el-select
                :value="setting.aside.theme"
                @input="v => onChange('aside/theme', v)"
            >
                <el-option value="light" label="亮色"/>
                <el-option value="dark" label="暗色"/>
            </el-select>
        </div>
        <div class="setting-drawer-item">
            <span>手风琴效果</span>
            <el-switch
                :value="setting.aside.uniqueOpen"
                @input="v => onChange('aside/uniqueOpen', v)"
            />
        </div>
        <div class="setting-drawer-item">
            <span>折叠</span>
            <el-switch
                :value="setting.aside.collapse"
                @input="v => onChange('aside/collapse', v)"
            />
        </div>
        <div class="setting-drawer-item">
            <span>折叠时显示上级</span>
            <el-switch
                :value="setting.aside.showParentOnCollapse"
                @input="v => onChange('aside/showParentOnCollapse', v)"
            />
        </div>
        <div class="setting-drawer-item">
            <span>自动隐藏</span>
            <el-switch
                :value="setting.aside.autoHide"
                @input="v => onChange('aside/autoHide', v)"
            />
        </div>

        <el-divider>页面设置</el-divider>
        <div class="setting-drawer-item">
            <span>分层结构</span>
            <el-select
                :value="setting.app.struct"
                @input="v => onChange('app/struct', v)"
            >
                <el-option value="top-bottom" label="上下"/>
                <el-option value="left-right" label="左右"/>
            </el-select>
        </div>
        <div class="setting-drawer-item">
            <span>显示logo</span>
            <el-switch
                :value="setting.app.showLogo"
                @input="v => onChange('app/showLogo', v)"
            />
        </div>
        <div class="setting-drawer-item">
            <span>显示页头</span>
            <el-switch
                :value="setting.page.showHeader"
                @input="v => onChange('page/showHeader', v)"
            />
        </div>
        <div class="setting-drawer-item">
            <span>显示页脚</span>
            <el-switch
                :value="setting.page.showFooter"
                @input="v => onChange('page/showFooter', v)"
            />
        </div>

        <el-divider>多页签设置</el-divider>
        <div class="setting-drawer-item">
            <span>启用</span>
            <el-switch
                :value="setting.tagsView.enabled"
                @input="v => onChange('tagsView/enabled', v)"
            />
        </div>
        <div class="setting-drawer-item">
            <span>启用缓存</span>
            <el-switch
                :value="setting.tagsView.enableCache"
                @input="v => onChange('tagsView/enableCache', v)"
            />
        </div>
        <div class="setting-drawer-item">
            <span>启用快捷键切换</span>
            <el-switch
                :value="setting.tagsView.shortcut"
                @input="v => onChange('tagsView/shortcut', v)"
            />
        </div>
        <div class="setting-drawer-item">
            <span>持久化</span>
            <el-switch
                :value="setting.tagsView.persistent"
                @input="v => onChange('tagsView/persistent', v)"
            />
        </div>

        <el-divider/>
        <el-button
            type="primary"
            size="medium"
            icon="el-icon-delete"
            @click="clearCacheAndReload"
        >
            清除设置缓存并刷新
        </el-button>
    </el-drawer>
</template>

<script>
import tagsViewPersistentMixin from "./mixin/tagsViewPersistent"
import tagsViewShortcutMixin from "./mixin/tagsViewShortcut"
import CheckboxGroup from "./component/checkbox/Group"
import ColorCheckbox from "./component/checkbox/ColorCheckbox"
import ImgCheckbox from "./component/checkbox/ImgCheckbox"
import {setLocalPersonalSettings} from "@/util/storage"

export default {
    name: "SettingDrawer",

    mixins: [tagsViewPersistentMixin, tagsViewShortcutMixin],

    //让el-select的size为mini
    provide: () => ({elFormItem: {elFormItemSize: 'mini'}}),

    components: {CheckboxGroup, ColorCheckbox, ImgCheckbox},

    data() {
        return {
            visible: false,
            navModes: [
                {
                    label: '侧边栏导航',
                    value: 'aside',
                    img: 'https://gw.alipayobjects.com/zos/antfincdn/XwFOFbLkSM/LCkqqYNmvBEbokSDscrm.svg'
                },
                {
                    label: '顶部导航',
                    value: 'head',
                    img: 'https://gw.alipayobjects.com/zos/antfincdn/URETY8%24STp/KDNDBbriJhLwuqMoxcAr.svg'
                },
                {
                    label: '混合导航',
                    value: 'mix',
                    img: 'https://gw.alipayobjects.com/zos/antfincdn/x8Ob%26B8cy8/LCkqqYNmvBEbokSDscrm.svg'
                }
            ]
        }
    },

    computed: {
        setting() {
            return this.$store.state.setting
        }
    },

    methods: {
        //表单数据发生改变时，修改vuex的数据
        onChange(type, val) {
            this.$store.commit(`setting/${type}`, val)
        },

        //设置抽屉的数据结构与本地存储不一致时，可能导致layout渲染异常，所以加了这个功能
        clearCacheAndReload() {
            setLocalPersonalSettings()
            window.location.reload()
        }
    }
}
</script>
