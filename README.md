# A Wordle game clone
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=plastic)](https://opensource.org/licenses/MIT)

A [Wordle](https://en.wikipedia.org/wiki/Wordle) clone implemented with [Spring Boot](https://spring.io/projects/spring-boot), [R2DBC](https://r2dbc.io/), [MariaDB](https://mariadb.com/), and [Svelte](https://svelte.dev/).

> ⚠️ This implementation is intended to show MariaDB's SQL features rather than to demostrate the most efficient way to implement the game. We purposely implemented the game logic as SQL statements to demonstrate some of these features.

## Requirements

- Java 17 or later. Previous versions should work (update the version
  in the **pom.xml** file).
- [Apache Maven](https://maven.apache.org).
- [Node.js](https://nodejs.org)
- MariaDB server. If you don't want to install anything extra, try creating a
  [free SkySQL account](https://mariadb.com/products/skysql)).
  [Docker](https://hub.docker.com/u/mariadb) is also a good option.
- An SQL client tool like `mariadb`, DBeaver, or an SQL integration for
  your IDE.

## Running the app

Prepare the database:

```sql
CREATE DATABASE wordle;
CREATE USER 'user'@'%';
GRANT ALL ON wordle.* TO 'user'@'%' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;

USE wordle;

CREATE TABLE topic
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL UNIQUE
) ENGINE = Aria;

CREATE TABLE word
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    text      VARCHAR(64),
    topic_id  INT NOT NULL REFERENCES topic (id),
    published DATE,
    length    INT NOT NULL DEFAULT LENGTH(text),
    UNIQUE KEY word_topic_text (topic_id, text),
    UNIQUE KEY word_topic_published_length_uk (topic_id, published, length)
) ENGINE = Aria;

INSERT INTO topic(name) VALUES ('Test data set');

INSERT INTO word(text, topic_id)
VALUES ('berry', 1),
       ('ferry', 1),
       ('hello', 1),
       ('world', 1),
       ('great', 1),
       ('query', 1),
       ('maria', 1);
```

Start the back end:

```
git clone git@github.com:mariadb-developers/wordle-game-clone.git
cd wordle-game-clone/wordle-java-spring-backend/
mvn package
java -jar target/wordle-java-spring-backend-0.0.1-SNAPSHOT.jar
```

Start the front end:

```
cd ../wordle-svelte-frontend/
npm install
npm run dev
```

Go to http://localhost:8080:

![Screenshot](https://repository-images.githubusercontent.com/451800068/0e2f27cc-10d8-404c-b090-950ff8396561)

## Add topics

Add your own topics. For example, download the [370k English words corpus](https://www.kaggle.com/ruchi798/part-of-speech-tagging) dataset and import it to the MariaDB database:

```sql
INSERT INTO topic(name) VALUES ('370k English words corpus');

LOAD DATA LOCAL INFILE '/path/to/words_pos.csv'
    IGNORE INTO TABLE word
    FIELDS TERMINATED BY ','
    LINES TERMINATED BY '\n'
    IGNORE 0 LINES
    (@dummy, text) SET topic_id = 2, text = UPPER(text);
```

Go to http://localhost:8080/?topic=2.

You can also specify the length of the word: http://localhost:8080/?topic=2&length=4.

## Support and Contribution

Please feel free to submit PR's, issues or requests to this project
directly.

If you have any other questions, comments, or looking for more information
on MariaDB please check out:

* [MariaDB Developer Hub](https://mariadb.com/developers)
* [MariaDB Community Slack](https://r.mariadb.com/join-community-slack)

Or reach out to us directly via:

* [developers@mariadb.com](mailto:developers@mariadb.com)
* [MariaDB Twitter](https://twitter.com/mariadb)
