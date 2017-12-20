# NOTE: This is now a relic of a prior era and is kept for historical reasons only.

I originally created this repo partially to port a great game, but mostly to learn Java. I never finished it unfortunately. But now I'm working on the same project but to learn Kotlin. This will be kept in a separate branch and left alone.

# Space Trader (backend)

This is the WIP backend for a web-based Space Trader clone. I'm aiming to reach feature-parity with the original
version. The initial prototype will only support a single player, and no database persistence.

## What is Space Trader?

Space Trader is a game loosely inspired by Dope Wars and Elite released for Palm devices. Open source, it later
saw ports on Windows and mobile platforms.

## Installation

mvn package... I can't remember right now. I just run it in IntelliJ

## API

Interaction with the game is via a REST API. These are subject to change.

`GET /games` - List current games available

`POST /games/new?name=[commanderName]&fighter=[fighterSkill]&engineer=[engineerSkill]&pilot=[pilotSkill]&trader=[traderSkill]&difficulty=[difficultyLevel]`
  - Start new game. Returns `id` for newly created game. Skills are 0-10 and must add up to 20. Difficulty is 0-4.

`GET /games/[id]/state` - Get current GameState of the game, e.g. on system, in combat, etc. Returns all relevant information about
current state, such as goods for sale on a system, or opponent hull strength in combat. Also returns all possible
actions.

`POST /games/[id]/action/[action]` - Take an action, such as "attack" or "buyShip". Returns the new GameState.

`GET /games/[id]/galaxy` - Get information about the entire galaxy - system locations, political status, etc.

`GET /games/[id]/ships` - Get information about every ship in the game. (Maybe this will be removed?)

`GET /games/[id]/captain` - Get captain specific statistics.

`GET /games/[id]/bank` - Get financial information such as debt and insurance status.
