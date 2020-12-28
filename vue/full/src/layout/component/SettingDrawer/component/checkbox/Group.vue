<template>
    <div class="checkbox-group">
        <slot/>
    </div>
</template>

<script>
export default {
    name: "CheckboxGroup",

    props: {value: String},

    data: () => ({children: []})
}
</script>
