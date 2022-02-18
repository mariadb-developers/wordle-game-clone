<script>
    import Square from "./Square.svelte";
    import {slide} from "svelte/transition";

    export let word = '';
    export let colors = '';
    export let shake = false;

</script>

<div class="row {shake ? 'shake' : ''}" in:slide>
    {#each word as letter, i}
        <Square letter="{letter}" color="{colors.charAt(i) || 'transparent'}"/>
    {/each}
</div>

<style>
    .row {
        display: flex;
        flex-direction: row;
    }

    .shake {
        animation: shake 0.5s;
    }

    @keyframes shake {
        0% { transform: translate(1px, 1px) rotate(0deg); }
        10% { transform: translate(-1px, -2px) rotate(-1deg); }
        20% { transform: translate(-3px, 0px) rotate(1deg); }
        30% { transform: translate(3px, 2px) rotate(0deg); }
        40% { transform: translate(1px, -1px) rotate(1deg); }
        50% { transform: translate(-1px, 2px) rotate(-1deg); }
        60% { transform: translate(-3px, 1px) rotate(0deg); }
        70% { transform: translate(3px, 1px) rotate(-1deg); }
        80% { transform: translate(-1px, -1px) rotate(1deg); }
        90% { transform: translate(1px, 2px) rotate(0deg); }
        100% { transform: translate(1px, -2px) rotate(-1deg); }
    }
</style>
