# jPokeBot

![alt text](https://raw.githubusercontent.com/tassoneroberto/jpokebot/master/logo.png?token=ACYT353LV72KZPGHG76GNES46WCHI)

## Introduction
:robot: Pokémon GO bot written in Java.

:exclamation: This project is not working with lastest Niantic API versions! :exclamation:

N.B.
This project uses the following APIs: https://github.com/Grover-c13/PokeGOAPI-Java

## Installation
Clone:
```
git clone https://github.com/tassoneroberto/jpokebot.git
```

Add ```PokeGOAPI-library-x.y.z.jar``` file as a project dependency. Lastest versions (0.4.1) is deprecated.

Build maven:
```
mvn package
mvn compile
```

Run ```Bot.java``` class.

## Note
This is an old project decompiled from a JAR file (Yes, I lost original .java files!), so some classes are broken or even missed.
Also, when this bot was created (august 2016), you just needed a Pokemon GO account to login and start botting.
After a while people lost in interest in the Pokemon GO game.
Consequently the developers of the API used by bots and maps stopped to maintain the project updated (more info here: https://www.reddit.com/r/pokemongodev/).
This lead to a difficulty in cracking Niantic game protection.
At the moment doesn't exist working API to use.
Anyway, I decided to upload the project with the hope that one day some API will be available again to use.
