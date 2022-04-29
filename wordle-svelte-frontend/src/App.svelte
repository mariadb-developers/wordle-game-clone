<script>
	import { onMount } from "svelte";
	import Row from "./Row.svelte";
	import Keyboard from "simple-keyboard";
	import "simple-keyboard/build/css/index.css";
	import "./app.css";

	let backendUrl = "";
	let topic = {};
	let length = 5;
	let wordToCheck = "";
	let results = [];
	let win = false;
	let resultsShare = "";
	let checkingWord = false;
	let wrongWord = false;

	onMount(() => {
		fetchBackendUrl("backend.json");
	});

	function fetchBackendUrl(backendFetchUrl) {
		fetch(backendFetchUrl)
			.then((response) => {
				if (!response.ok) {
					console.log("Using default backend URL");
					return { url: "http://localhost:9090/api/v1" };
				}
				return response.json();
			})
			.then((backend) => (backendUrl = backend.url))
			.then(() => fetchTopic());
	}

	function fetchTopic() {
		const urlParams = new URLSearchParams(window.location.search);
		if (urlParams.has("length")) {
			length = parseInt(urlParams.get("length"));
		}

		let topicId = 1;
		if (urlParams.has("topic")) {
			topicId = parseInt(urlParams.get("topic"));
		}

		fetch(`${backendUrl}/topics`)
			.then((response) => response.json())
			.then((topics) => (topic = topics.find((t) => t.id === topicId)))
			.then(() => fillWithSpaces())
			.then(() => screenKeyboardSetup());
	}

	function checkWord() {
		checkingWord = true;
		wrongWord = false;
		fetch(`${backendUrl}/${topic.id}/${length}/${wordToCheck}/check`)
			.then((response) => response.text())
			.then((data) => addResult(data))
			.then(() => (checkingWord = false))
			.catch(() => {
				setTimeout(() => checkWord(), 5000);
			});
	}

	function addResult(resultString) {
		if (resultString) {
			results = [...results, { colors: resultString, word: wordToCheck }];
			let correctLettersCount = resultString.split("2").length - 1;
			if (correctLettersCount === length) {
				resultsShare = "\n";
				for (const result of results) {
					resultsShare +=
						result.colors
							.replaceAll("0", "⬛")
							.replaceAll("1", "🟨")
							.replaceAll("2", "🟩") + "\n";
				}
				win = true;
			} else {
				wordToCheck = "";
				fillWithSpaces();
				setTimeout(
					() => window.scrollTo(0, document.body.scrollHeight),
					355
				);
			}
		} else {
			wrongWord = true;
		}
	}

	function fillWithSpaces() {
		for (let i = wordToCheck.length; i < length; i++) {
			wordToCheck += " ";
		}
	}

	function screenKeyboardSetup() {
		new Keyboard({
			onKeyPress: (key) => handleKey(key),
			layout: {
				default: [
					"Q W E R T Y U I O P",
					"A S D F G H J K L",
					"✓ Z X C V B N M ❮",
				],
			},
		});
	}

	function handleKey(key) {
		if (key === "Enter" || key === "✓") {
			if (wordToCheck.trim().length === length) {
				checkWord();
			}
		} else if (key === "Backspace" || key === "❮") {
			if (wordToCheck.length > 0) {
				wordToCheck = wordToCheck.trim().slice(0, -1);
			}
		} else if (key.length === 1 && wordToCheck.trim().length < length) {
			wordToCheck = wordToCheck.trim() + key;
		}
		fillWithSpaces();
	}

	function handleKeydown(event) {
		if (
			checkingWord ||
			event.altKey ||
			event.ctrlKey ||
			event.metaKey ||
			event.key === "Meta" ||
			event.key === "Control" ||
			event.key === "alt"
		) {
			return;
		}
		handleKey(event.key);
	}
</script>

<svelte:window on:keydown={handleKeydown} />

<main>
	{#if topic.name === undefined}
		<p>Connecting to the backend...</p>
	{:else}
		<h1>
			{topic.name}
		</h1>
		<div class="board">
			{#each results as result (result)}
				<Row word={result.word} colors={result.colors} />
			{/each}

			{#if !win}
				<Row word={wordToCheck} shake={wrongWord} />
			{:else}
				<div class="meaning">
					Lookup <a
						href="https://en.wikipedia.org/w/index.php?search={wordToCheck}"
					>
						<span class="word">{wordToCheck}</span></a
					>
					in Wikipedia.
				</div>
				<div class="twitter">
					<script
						async
						src="https://platform.twitter.com/widgets.js"
						charset="utf-8"></script>
					<a
						href="https://twitter.com/intent/tweet?screen_name=MariaDB"
						class="twitter-mention-button"
						data-size="large"
						data-text="#Wordle: {topic.name} {resultsShare}"
						data-show-count="false"
					/>
				</div>
				<div class="footer">
					Powered by <a href="https://mariadb.com/">MariaDB</a>.
					Browse the
					<a href="https://github.com/mariadb-developers"
						>source code</a
					>.
				</div>
			{/if}
		</div>
		{#if !win}
			<div class="keyboard-wrapper">
				<div class="simple-keyboard" />
			</div>
		{/if}
	{/if}
</main>
