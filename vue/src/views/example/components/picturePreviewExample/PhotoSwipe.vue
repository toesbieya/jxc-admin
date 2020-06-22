<template>
    <div aria-hidden="true" class="pswp" role="dialog" tabindex="-1">

        <!-- Background of PhotoSwipe.
             It's a separate element as animating opacity is faster than rgba(). -->
        <div class="pswp__bg"></div>

        <!-- Slides wrapper with overflow:hidden. -->
        <div class="pswp__scroll-wrap">

            <!-- Container that holds slides.
                PhotoSwipe keeps only 3 of them in the DOM to save memory.
                Don't modify these 3 pswp__item elements, data is added later on. -->
            <div class="pswp__container">
                <div class="pswp__item"></div>
                <div class="pswp__item"></div>
                <div class="pswp__item"></div>
            </div>

            <!-- Default (PhotoSwipeUI_Default) interface on top of sliding area. Can be changed. -->
            <div class="pswp__ui pswp__ui--hidden">

                <div class="pswp__top-bar">

                    <!--  Controls are self-explanatory. Order can be changed. -->

                    <div class="pswp__counter"></div>

                    <button class="pswp__button pswp__button--close" title="Close (Esc)"></button>

                    <button class="pswp__button pswp__button--share" title="Share"></button>

                    <button class="pswp__button pswp__button--fs" title="Toggle fullscreen"></button>

                    <button class="pswp__button pswp__button--zoom" title="Zoom in/out"></button>

                    <!-- Preloader demo https://codepen.io/dimsemenov/pen/yyBWoR -->
                    <!-- element will get class pswp__preloader--active when preloader is running -->
                    <div class="pswp__preloader">
                        <div class="pswp__preloader__icn">
                            <div class="pswp__preloader__cut">
                                <div class="pswp__preloader__donut"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="pswp__share-modal pswp__share-modal--hidden pswp__single-tap">
                    <div class="pswp__share-tooltip"></div>
                </div>

                <button class="pswp__button pswp__button--arrow--left" title="Previous (arrow left)">
                </button>

                <button class="pswp__button pswp__button--arrow--right" title="Next (arrow right)">
                </button>

                <div class="pswp__caption">
                    <div class="pswp__caption__center"></div>
                </div>

            </div>

        </div>

    </div>
</template>

<script>
    /*
    * https://photoswipe.com/
    * 需要加载以下文件
    * photoSwipe/photoswipe.css
    * photoSwipe/default-skin/default-skin.css
    * photoSwipe/photoswipe.js
    * photoSwipe/photoswipe-ui-default.js
    * */

    export default {
        name: "PhotoSwipe",
        props: {
            data: {type: Array, default: () => []},
            index: {type: Number, default: 0},
            options: Object
        },
        data() {
            return {
                gallery: null,
                photoSwipeOptions: {
                    index: 0,//初始索引
                    escKey: true,
                    closeOnScroll: false,//滚动时是否关闭
                    closeOnVerticalDrag: false,//垂直拖动为缩放的图片时是否关闭
                    history: false,//是否启用url历史
                },
                uiOptions: {
                    timeToIdle: 2000,//鼠标停止移动后多久隐藏ui，单位毫秒
                    timeToIdleOutside: 0,//鼠标移出可视窗口后多久隐藏ui，单位毫秒

                    /*按钮显示*/
                    closeEl: true,
                    captionEl: true,
                    fullscreenEl: true,
                    zoomEl: true,
                    shareEl: false,
                    counterEl: true,
                    arrowEl: true,
                    preloaderEl: true,

                    clickToCloseNonZoomable: false
                }
            }
        },
        methods: {
            mergeOptions() {
                this.photoSwipeOptions.index = this.index
                if (!this.options) return {...this.photoSwipeOptions, ...this.uiOptions}
                Object.keys(this.options).forEach(key => {
                    if (key in this.photoSwipeOptions) {
                        this.photoSwipeOptions[key] = this.options[key]
                    }
                    else if (key in this.uiOptions) {
                        this.uiOptions[key] = this.options[key]
                    }
                })
                return {...this.photoSwipeOptions, ...this.uiOptions}
            },
            show() {
                this.gallery = new window.PhotoSwipe(this.$el, window.PhotoSwipeUI_Default, this.data, this.mergeOptions())
                this.gallery.init()
            }
        }
    }
</script>
