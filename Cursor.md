# Crown of Farmland (Krone von Ackerland) – Projekt-Wissensbasis

## Projektüberblick
Java-Spiel „Crown of Farmland“ (Programmieren WS 2025/26, Abschlussaufgabe 1). Textbasiertes 2-Team-Spiel auf 7×7-Brett mit Einheiten, Stapeln, Händen und Bauernkönigen. Spezifikation siehe Aufgaben-PDF (A.1–A.6).

## Technischer Stack
- **Java SE 21**
- Erlaubte Bibliotheken: `java.lang`, `java.io`, `java.util`, `java.util.regex`, `java.nio.file`, `java.nio.charset`, `java.util.Scanner`
- Kein `System.exit()` / `Runtime.exit()`
- Checkstyle-Regeln einhalten

## Wichtige Projektstruktur
- **Einstieg:** `Main.java` → `ArgumentParser.parse(args)` → `ConfigLoader.load(kv)` → `CommandHandler(config)` → `handleUserInput()`
- **Config:** `ConfigLoader` liest units- und deck-Dateien, baut `GameConfig` (seed, Teamnamen, Verbosity, SymbolSet, units, deckCountsTeam1/Team2).
- **Spielzustand:** `Game` hält `GameBoard`, `Team` (je Deck, Hand, King), `currentTeam`, `selectedField`, `gameOver`. Initialisierung in `handleUserInput()`: `new Game(config)` → `game.initFromConfig()` (Decks füllen, mischen, 4 in Hand, Kings auf D1/D7).
- **Befehle:** `CommandHandler` registriert Commands (select, board, move, flip, block, hand, place, show, yield, state, quit). Commands nutzen `commandHandler.getGame()` für Spielzustand.
- **Model:** `Team` (Deck, Hand, King), `Unit`/`BasicUnit`/`King`, `GameBoard` (7×7 `Field`), `Field` (row, col, unit). Koordinaten: Zeile 0 = Spec-Zeile 1 (unten), Spalte 0 = A; D1 = (0,3), D7 = (6,3).

## Bereits umgesetzt (Stand Wissensbasis)
- Programmstart: Argument-Parsing, Config-Loading (units, deck/deck1+deck2), Validierung.
- Spiel-Init in `handleUserInput()`: Decks aus Config, Shuffle, 4 Karten pro Hand, Kings auf D1/D7.
- GameBoard mit `placeUnit(row, col, unit)` und `getField(row, col)`.
- Command-Struktur und Hilfetext; viele Commands noch Stubs (z. B. `YieldCommand.execute()` leer).

## Noch offen / geplant
- **Zugwechsel:** `yield`-Logik (Zug beenden, currentTeam wechseln, Auswahl zurücksetzen, ggf. Karte abwerfen, neues Team zieht vom Stapel, „It is <team>'s turn!“, ggf. Spielende bei leerem Stapel). Dafür in `Game` z. B. `setCurrentTeam(Team)` oder `endTurn()` ergänzen.
- Weitere Befehle implementieren (select, board, move, flip, block, hand, place, show, state, quit) gemäß Spec A.5.
- KI-Gegner (A.2) für Team 2.
- Duell-, Zusammenschluss- und Kompatibilitätslogik (A.1.4, A.1.9, A.1.10).

## Konventionen & Besonderheiten
- Exceptions: `GameException` (mit `getFormattedMessage()`), `StartupException` für Startfehler, `CommandException` für Laufzeitfehler bei Befehlen.
- Einheiten aus Config: `UnitTemplate` (qualifier, role, atk, def) → beim Befüllen der Decks `new BasicUnit(...)` pro Karte.
- Team-Referenz auf Units: `unit.setTeam(team)` beim Ziehen in die Hand.
- Spielname in Doku: exakt „Krone von Ackerland“ mit Zeichen а (U+0430).
