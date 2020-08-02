<template functional>
    <div class="detail-page-footer">
        <div class="detail-page-footer-left">
            <slot name="left"/>
        </div>
        <div class="detail-page-footer-right">
            <slot/>
        </div>
    </div>
</template>

<script>
    export default {
        name: "DocDetailFooter"
    }
</script>
