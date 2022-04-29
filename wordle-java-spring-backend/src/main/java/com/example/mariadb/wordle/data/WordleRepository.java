package com.example.mariadb.wordle.data;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WordleRepository extends ReactiveCrudRepository<Topic, Long> {

	@Modifying
	@Query("""
			UPDATE word
			SET published = CURRENT_DATE()
			WHERE published IS NULL
				AND topic_id = :topicId
				AND length = :length
			ORDER BY RAND()
			LIMIT 1;
			""")
	Mono<Integer> pickTodaysWord(@PathVariable long topicId, int length);

	@Query("""
			SELECT id, name
			FROM topic
			""")
	Flux<Topic> findTopics();

	@Query("""
			WITH RECURSIVE number AS (
				SELECT 1 AS i
				UNION ALL
				SELECT i + 1
				FROM number
				WHERE i < :length
			)
			SELECT CASE
				WHEN SUBSTR(test_word.text, i, 1) = SUBSTR(todays_word.text, i, 1) THEN 2
				WHEN LOCATE(SUBSTR(test_word.text, i, 1), todays_word.text) = 0 THEN 0
				ELSE 1
				END
			FROM number
				JOIN word todays_word
				JOIN word test_word ON todays_word.topic_id = test_word.topic_id
			WHERE todays_word.published = CURRENT_DATE()
				AND todays_word.topic_id = :topicId
				AND todays_word.length = :length
				AND LOWER(test_word.text) = LOWER(:testWord)
			""")
	Flux<String> checkWord(String testWord, long topicId, int length);

}
