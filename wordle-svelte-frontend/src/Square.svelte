<script>
    import {scale} from "svelte/transition";
    export let letter = '';
    export let color = '';
</script>

<div class="square color-{color}" in:scale>
    {letter}
</div>

<style>
    .square {
        text-align: center;
        width: 1.8em;
        height: 1.8em;
        line-height: 1.8em;
        font-weight: bold;
        text-transform: uppercase;
        margin: 0.1em;
        border: 1px solid white;
    }

    .color-transparent {
        border: 1px solid #ccc;
    }

    .color-0 {
        background-color: #777;
        color: white;
    }

    .color-1 {
        background-color: #e7bd00;
        color: white;
    }

    .color-2 {
        background-color: #abc74a;
        color: white;
    }
</style>
