# UHC Meetups

### This is still in development

UHC Meetups is a lightweight and modular Minecraft plugin designed to run fast-paced UHC (Ultra Hardcore) meetup-style PvP games. It includes arena management, player scattering, border control, and full game lifecycle handling.

---

## Overview

This plugin provides a complete backend system for running automated UHC Meetup matches:

- Queue-based matchmaking
- Multi-arena support
- Automatic player scattering
- World border control system
- Spectator handling
- Fully managed game states
- Clean game reset system

---

## Features

### Game System
- WAITING → STARTING → ACTIVE → ENDING game states
- Automatic match flow control
- Safe game shutdown and reset

###  Arena System
- Multiple arenas supported
- YAML-based saving system (`arenas.yml`)
- Lobby, spectator, world, and border center setup

###  Scatter System
- Randomized safe player placement
- Anti-overlap spawning logic
- Configurable scatter radius

###  Border System
- Dynamic world border control
- Automatic border shrinking during match
- Clean reset after match ends

###  Player System
- Queue system for matchmaking
- Spectator mode support
- Player data tracking

---

## 🚀 Installation

### 1. Download Plugin
Place the compiled `.jar` file into your server’s `plugins/` folder.

### 2. Start Server
Run your server once to generate configuration files.

### 3. Setup Arena

Use the in-game commands:

