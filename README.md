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

`GET /state` - Get current GameState of the game, e.g. on system, in combat, etc. Returns all relevant information about
current state, such as goods for sale on a system, or opponent hull strength in combat. Also returns all possible
actions.

`POST /action/[action]` - Take an action, such as "attack" or "buyShip". Returns the new GameState.

`GET /galaxy` - Get information about the entire galaxy - system locations, political status, etc.

`GET /ships` - Get information about every ship in the game. (Maybe this will be removed?)

`GET /captain` - Get captain specific statistics.

`GET /bank` - Get financial information such as debt and insurance status.