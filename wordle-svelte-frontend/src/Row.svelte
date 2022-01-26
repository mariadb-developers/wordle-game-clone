<script>
    import Square from "./Square.svelte";
    import {slide} from "svelte/transition";

    export let word = '';
    export let colors = '';

</script>

<div class="row" in:slide>
    {#each word as letter, i}
        <Square letter="{letter}" color="{colors.charAt(i) || 'transparent'}"/>
    {/each}
</div>

<style>
    .row {
        display: flex;
        flex-direction: row;
        margin: 0 auto;
        text-align: center;
    }
</style>
