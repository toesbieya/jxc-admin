<script type="text/jsx">
    import {isExternal} from "@/utils/validate"

    export default {
        name: 'Link',
        functional: true,
        render(h, context) {
            let to = context.props.to
            if (isExternal(to)) return (
                <a href={to} target="_blank" rel="nofollow">
                    {context.children}
                </a>
            )
            else return (
                <router-link to={to}>
                    {context.children}
                </router-link>
            )
        }
    }
</script>
