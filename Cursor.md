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
- **Programmstart:** Argument-Parsing, Config-Loading (units, deck/deck1+deck2), Validierung.
- **Spiel-Init:** Decks aus Config, Shuffle, 4 Karten pro Hand, Kings auf D1/D7; `Game.getBoardCount(Team)`, `Game.getKingPosition(Team)`, `Game.parseField(String)`, `Game.isAdjacent`, `Game.isAdjacentToKing`.
- **GameBoard:** `placeUnit`, `getField`, `render(Field selectedField, Team currentTeam)` mit Standard-/angepasstem Symbolsatz, Verbosity all/compact, Einheiten (x/X/y/Y, *, b).
- **Befehle (alle implementiert):**
  - **quit:** `QuitCommand`, `CommandHandler.requestQuit()` beendet die Eingabeschleife.
  - **hand:** 1-basierte Liste mit Name und (ATK/DEF).
  - **select:** Feld A–G/1–7 parsen, `setSelectedField`, Ausgabe board + show.
  - **show:** `<no unit>` / Einheiteninfos / ??? bei verdecktem Gegner / Bauernkönig; `ShowCommand.printShow(Game)` wiederverwendbar.
  - **board:** Ausgabe über `GameBoard.render(...)`.
  - **state:** Teamnamen, LP/8000, DC/40, BC/5 (Zeilenlänge 31), dann board, dann show bei Auswahl.
  - **yield:** `Game.endTurn(Integer discardHandIndex)` mit Validierung (Hand voll → Abwurf nötig), Teamwechsel, Nachzug, Spielende bei leerem Stapel; danach bei Team 2 Aufruf von `AIPlayer.runTurn(game)`.
  - **move:** Validierung (Feld, Abstand, King-Regeln), leeres Feld / Duell / Zusammenschluss; Ausgaben inkl. „no longer blocks“, Duelltexte, join forces / Union failed.
  - **place:** Feld an King angrenzend (8 Richtungen), ein oder mehrere Hand-Indizes, Zusammenschluss, 6.-Einheit-Regel; danach board + show.
  - **flip:** Einheit aufdecken (nur wenn nicht bewegt, noch verdeckt).
  - **block:** Blockade einleiten, zählt als Bewegung.
- **Duell (A.1.4):** `Game.performDuel(attacker, defender, defenderBlocked, fromRow, fromCol, toRow, toCol)` mit Blockade-, König- und Standardfällen; Schaden, Elimination, Aufdecken; `DuelResult` mit Zeilen und Gewinner.
- **Kompatibilität (A.1.10):** `Compatibility.check(unitA, unitB)` → `MergeStats` oder null (Symbiose, Gleichgesinntheit, Prim); `Game.createMergedUnit(unitA, unitB, stats)` für Namen und Werte.
- **Zusammenschluss (A.1.9):** in move/place umgesetzt (Erfolg/Fehlschlag, 6. Einheit sofort entfernt).
- **KI-Gegner (A.2):** `AIPlayer.runTurn(Game)` – Königbewegung (Score, gewichtete Zufallsauswahl), Platzierung (Feld- und Einheitenwahl), Einheitenzüge (Bewertung, gewichtete Auswahl), Abwurf (umgekehrt gewichtet); wird nach `yield` ausgeführt, wenn `currentTeam == team2`.

## Noch offen / geplant
- Ggf. Feinschliff an Ausgabeformaten (z. B. Duell „???“ vor Aufdecken) und Tests gegen Beispielinteraktion aus dem Aufgaben-PDF.
- Checkstyle durchlaufen lassen und ggf. Anpassungen.

## Erledigt (Beispielinteraktion A.5.10)
- **yield bei voller Hand:** Ausgabe `ERROR: Player's hand is full!` (war bereits korrekt).
- **place bei voller Hand:** Ausgabe `ERROR: cannot place a card, you must discard!` (MustDiscardException von „place“ auf „place a card“ geändert).
- **place 3 C2:** Befehl akzeptiert (optionales Feldargument), danach gleiche Fehlermeldung wenn Hand voll. Eingabedateien `input/units/default.txt` und `input/decks/default.txt` für die Beispiel-Parameter angelegt.

## Konventionen & Besonderheiten
- Exceptions: `GameException` (mit `getFormattedMessage()`), `StartupException` für Startfehler, `CommandException` für Laufzeitfehler bei Befehlen.
- Einheiten aus Config: `UnitTemplate` (qualifier, role, atk, def) → beim Befüllen der Decks `new BasicUnit(...)` pro Karte.
- Team-Referenz auf Units: `unit.setTeam(team)` beim Ziehen in die Hand.
- Spielname in Doku: exakt „Krone von Ackerland“ mit Zeichen а (U+0430).
