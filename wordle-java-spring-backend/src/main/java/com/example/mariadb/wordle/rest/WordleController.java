package com.example.mariadb.wordle.rest;

import com.example.mariadb.wordle.data.Topic;
import com.example.mariadb.wordle.data.WordleRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class WordleController {

    private final WordleRepository wordleRepository;

    public WordleController(WordleRepository wordleRepository) {
        this.wordleRepository = wordleRepository;
    }

    @GetMapping("/topics")
    public Flux<Topic> findTopics() {
        return wordleRepository.findTopics();
    }

    @GetMapping("/{topicId}/{length}/{testWord}/check")
    public Flux<String> checkWord(@PathVariable long topicId,
                                  @PathVariable int length,
                                  @PathVariable String testWord) {
        wordleRepository.pickTodaysWord(topicId, length).onErrorReturn(0).subscribe();
        return wordleRepository.checkWord(testWord, topicId, length);
    }

}
