<script>
    import {onMount} from "svelte";
    import Row from './Row.svelte';

    let topics = [];
    let topic = {};
    let length = 5;
    let wordToCheck = '';
    let results = [];
    let win = false;
    let resultsShare = '';
    let checkingWord = false;
    let wrongWord = false;

    onMount(() => {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has("length")) {
            length = parseInt(urlParams.get('length'));
        }

        let topicId = 1;
        if (urlParams.has("topic")) {
            topicId = parseInt(urlParams.get('topic'));
        }

        fetch("http://localhost:9090/api/v1/topics")
            .then(response => response.json())
            .then(data => topics = data)
            .then(() => topic = topics.find(t => t.id === topicId))
            .then(() => fillWithSpaces());
    });

    const checkWord = () => {
        checkingWord = true;
        wrongWord = false;
        fetch(`http://localhost:9090/api/v1/${topic.id}/${length}/${wordToCheck}/check`)
            .then(response => response.text())
            .then(data => addResult(data))
            .then(() => checkingWord = false);
    };

    const addResult = resultString => {
        if (resultString) {
            results = [...results, {colors: resultString, word: wordToCheck}];
            let correctLettersCount = resultString.split("2").length - 1;
            if (correctLettersCount === length) {
                resultsShare = '\n';
                for (const result of results) {
                    resultsShare += result.colors
                        .replaceAll('0', '⬛')
                        .replaceAll('1', "🟨")
                        .replaceAll('2', '🟩') + "\n";
                }
                win = true;
            }
            wordToCheck = '';
            fillWithSpaces();
            window.scrollTo(0, document.body.scrollHeight + 900);
        } else {
            wrongWord = true;
        }
    };

    const fillWithSpaces = () => {
        for (let i = wordToCheck.length; i < length; i++) {
            wordToCheck += ' ';
        }
    }

    function handleKeydown(event) {
        if (checkingWord || event.altKey || event.ctrlKey || event.metaKey
            || event.key === "Meta" || event.key === "Control"
            || event.key === "alt") {
            return;
        }

        let key = event.keyCode || event.charCode;
        if (key === 13 && wordToCheck.trim().length === length) { // enter
            checkWord();
        } else if (key === 8) { // backspace
            if (wordToCheck.length > 0) {
                wordToCheck = wordToCheck.trim().slice(0, -1);
            }
        } else if (event.key.length === 1 && wordToCheck.trim().length < length) {
            wordToCheck = wordToCheck.trim() + event.key;
        }
        fillWithSpaces();
    }
</script>

<svelte:window on:keydown={handleKeydown}/>

<main>
    <h1>
        {#if win}
            Congrats!
        {:else}
            {topic.name}
        {/if}
    </h1>
    <div class="centered">
        {#each results as result (result)}
            <Row word="{result.word}" colors="{result.colors}"/>
        {/each}
        {#if !win}
            <Row word="{wordToCheck}" shake="{wrongWord}"/>
            {#if checkingWord}
                <div>
                    <img alt="checking..." class="checking" src="./images/checking.gif">
                </div>
            {/if}
        {:else}
            <div class="twitter">
                <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>
                <a href="https://twitter.com/intent/tweet?screen_name=MariaDB"
                   class="twitter-mention-button"
                   data-size="large"
                   data-text="#Wordle: {topic.name} {resultsShare}"
                   data-show-count="false">
                </a>
            </div>
            <div class="footer">
                Powered by <a href="https://mariadb.com/">MariaDB</a>.
                Browse the <a href="https://github.com/mariadb-developers">source code</a>.
            </div>
        {/if}
    </div>
</main>

<style>
    main {
        text-align: center;
    }

    @media (min-width: 640px) {
        main {
            max-width: none;
        }
    }

    .centered {
        display: flex;
        flex-direction: column;
        padding-bottom: 3em;
    }

    .checking {
        width: 100px;
    }

    .twitter {
        margin-top: 1.5em;
    }

    .footer {
        font-size: small;
    }
</style>