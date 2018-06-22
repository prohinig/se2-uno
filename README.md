[![Build Status](https://travis-ci.org/cprohinig/se2-uno.svg?branch=master)](https://travis-ci.org/cprohinig/se2-uno)
![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=se2%3Auno-deluxe&metric=alert_status)
![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=se2%3Auno-deluxe&metric=sqale_rating)

![Coverage](https://sonarcloud.io/api/project_badges/measure?project=se2%3Auno-deluxe&metric=coverage)
![Bug Badge](https://sonarcloud.io/api/project_badges/measure?project=se2%3Auno-deluxe&metric=bugs)
![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=se2%3Auno-deluxe&metric=code_smells)
![Duplicated Lines](https://sonarcloud.io/api/project_badges/measure?project=se2%3Auno-deluxe&metric=duplicated_lines_density)
![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=se2%3Auno-deluxe&metric=vulnerabilities)
# Sofware Engineering II - UNO Deluxe App

An Unu Deluxe implementation for Android with Java.

The task was to implement a game for Android and with java only.


The requirements were:
* A playable game
* Multiplayer functionality using P2P
* A way to 'cheat' and detect cheating as a player, and react to it
* Additional custom rules or functionality, which is not part of the original game
* Not a new game, but one that is based on an existing card or board game

It was created for the course _Software Engineering II_ on _Alpen-Adria-University Klagenfurt_.

### Description
An __UNO Deluxe__ implementation, with custom cards and cheating functionality.

* Max supported players: 4
* Custom cards
* Cheating supported
* Cheating detection supported
* Hand sorting

### Cheating and detection
Cheating is possible in a build in way. It is possible to drop a single card every game out of the hand. To do so, swipe down on the card you want to let disappear.

If you detect that a player suddenly has one card less then he should have, than you can blame him for cheating. If you are correct, he has to draw cards, if not, you have to draw.

### Networking
The networking component is completely own-written, and is based on tcp sockets.
The Host opens a game, and other devices can connect to him via the ip.
Its written completely non-blocking, so a easy usability is given.

## Build with
* [Android Studio](https://developer.android.com/studio/) - The IDE used
* [Git](https://git-scm.com) - for VC
* [Github](https://github.com) - online VC, pull requests, webhooks, etc.
* [Sonarcloud](https://sonarcloud.io) - for linting (code quality, code smells, coverage)
* [Jacoco](https://www.eclemma.org/jacoco) - test coverage
* [Travis](https://travis.com) - build service
* [Gradle](https://gradle.org) - package manager

## Versioning
We used __git__ for versioning, and __Github__ for pull requests, reviews, hooks etc. 

## Authors
* __Hobisch Manuel__
  * Logic implementation
  * Card design
  * Sensor integration
* __Laubreiter David__
  * Networking
  * Refactoring
  * Assuring Code Quality
* __Prohinig Christian__
  * Game framework
  * Component integration
  * Interface design

## Acknowledgment
Thank you [stackoverflow](https://stackoverflow.com), for answering many questions :)
