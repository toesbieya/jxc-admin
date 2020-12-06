<template functional>
    <footer class="page-footer">
        <div class="copyright">
            Copyright © 2020 - <a href="https://github.com/toesbieya" target="_blank">toesbieya</a>
        </div>
    </footer>
</template>

<script>
export default {
    name: "PageFooter"
}
</script>

<style lang="scss">
@import "~@/asset/style/variables.scss";

.page-footer {
    height: $page-footer-height;
    padding: $page-view-margin;
    text-align: center;

    .copyright {
        color: #808695;
        font-size: 14px;
    }
}
</style>
