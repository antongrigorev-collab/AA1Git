# Crown of Farmland (Krone von Ackerland) – Projekt-Wissensbasis

## Projektüberblick
Java-Spiel „Crown of Farmland“ (Programmieren WS 2025/26, Abschlussaufgabe 1). Textbasiertes 2-Team-Spiel auf 7×7-Brett mit Einheiten, Stapeln, Händen und Bauernkönigen. Spezifikation siehe Aufgaben-PDF (A.1–A.6).

## Technischer Stack
- **Java SE 21**
- Erlaubte Bibliotheken: `java.lang`, `java.io`, `java.util`, `java.util.regex`, `java.nio.file`, `java.nio.charset`, `java.util.Scanner`
- Kein `System.exit()` / `Runtime.exit()`
- Checkstyle-Regeln einhalten  
- **Code-Vorgaben (erlaubt/verboten, Stil):** siehe [.cursor/rules/code-anforderungen.md](.cursor/rules/code-anforderungen.md)

## Wichtige Projektstruktur
- **Einstieg:** `Main.java` → `ArgumentParser.parse(args)` → `ConfigLoader.load(kv)` → `CommandHandler(config)` → `handleUserInput()`
- **Config:** `ConfigLoader` liest units- und deck-Dateien, baut `GameConfig` (seed, Teamnamen, Verbosity, SymbolSet, units, deckCountsTeam1/Team2).
- **Spielzustand:** `Game` hält `GameBoard`, `Team` (je Deck, Hand, King), `currentTeam`, `selectedField`, `gameOver`. Initialisierung in `handleUserInput()`: `new Game(config)` → `game.initFromConfig()` (Decks füllen, mischen, 4 in Hand, Kings auf D1/D7).
- **Befehle:** `CommandHandler` registriert Commands (select, board, move, flip, block, hand, place, show, yield, state, quit). Commands nutzen `commandHandler.getGame()` für Spielzustand.
- **Model:** `Team` (Deck, Hand, King), `Unit`/`BasicUnit`/`King`, `GameBoard` (7×7 `Field`), `Field` (row, col, unit). Koordinaten: Zeile 0 = Spec-Zeile 1 (unten), Spalte 0 = A; D1 = (0,3), D7 = (6,3).

## Bereits umgesetzt (Stand Wissensbasis)
- **Programmstart:** Argument-Parsing, Config-Loading (units, deck/deck1+deck2), Validierung.
- **Spiel-Init:** Decks aus Config, Shuffle, 4 Karten pro Hand, Kings auf D1/D7; `Game.getBoardCount(Team)`, `Game.getKingPosition(Team)`, `Game.parseField(String)`, `Game.isAdjacent`, `Game.isAdjacentToKing`.
- **GameBoard:** `placeUnit`, `getField`, `render(Field selectedField, Team teamShownAsX, Team currentTeam)` – teamShownAsX = Team, das als x/X dargestellt wird (Aufrufer übergeben stets `game.getTeam1()`), currentTeam für *-Präfix (kann noch ziehen). Standard-/angepasster Symbolsatz, Verbosity all/compact, Einheiten (x/X/y/Y, *, b). Team 1 (Spieler) bleibt immer x/X, Team 2 (KI) immer y/Y, auch nach yield.
- **Befehle (alle implementiert):**
  - **quit:** `QuitCommand`, `CommandHandler.requestQuit()` beendet die Eingabeschleife.
  - **hand:** 1-basierte Liste mit Name und (ATK/DEF).
  - **select:** Feld A–G/1–7 parsen, `setSelectedField`, Ausgabe board + show.
  - **show:** `<no unit>` / Einheiteninfos / ??? bei verdecktem Gegner / Bauernkönig; `ShowCommand.printShow(Game)` wiederverwendbar.
  - **board:** Ausgabe über `GameBoard.render(...)`.
  - **state:** Teamnamen, LP/8000, DC/40, BC/5 (Zeilenlänge 31), dann board, dann show bei Auswahl.
  - **yield:** `Game.endTurn(Integer discardHandIndex)` mit Validierung (Hand voll → Abwurf nötig), Teamwechsel, Nachzug, Spielende bei leerem Stapel; danach bei Team 2 Aufruf von `AIPlayer.runTurn(game)`.
  - **move:** `MoveCommand.execute` ruft je nach Ziel: `executeMoveEnPlace`, `executeMoveToEmpty` oder `executeMoveToOccupied` (Duell bzw. Zusammenschluss); Validierung (Feld, Abstand, King-Regeln); Ausgaben inkl. „no longer blocks“, Duelltexte, join forces / Union failed.
  - **place:** `PlaceCommand.execute` nutzt `validatePlaceCommand` (optionales Feld-Argument, Prüfungen, Hand-Indizes) und `placeUnitsOnField` (von Hand entfernen, Merge/6.-Einheit-Regel); Feld an King angrenzend (8 Richtungen); danach board + show.
  - **flip:** Einheit aufdecken (nur wenn nicht bewegt, noch verdeckt).
  - **block:** Blockade einleiten, zählt als Bewegung.
- **Duell (A.1.4):** `Game.performDuel(...)` orchestriert; Hilfsmethoden: `addDuelIntroLines` (Blockade aufheben, Angriffszeile, Flips), `resolveDuelVsKing`, `resolveBlockedDuel`, `resolveStandardDuel`; Schaden, Elimination, Aufdecken; `DuelResult` mit Zeilen und Gewinner.
- **Kompatibilität (A.1.10):** `Compatibility.check(unitA, unitB)` → `MergeStats` oder null (Symbiose, Gleichgesinntheit, Prim); `Game.createMergedUnit(unitA, unitB, stats)` für Namen und Werte.
- **Zusammenschluss (A.1.9):** in move/place umgesetzt (Erfolg/Fehlschlag, 6. Einheit sofort entfernt).
- **KI-Gegner (A.2):** `AIPlayer.runTurn(Game)` orchestriert: `runKingMove` (Königbewegung, Score `fellows - 2*enemies - distance - 3*fellowPresent`, bei Gleichstand `selectAmongMaxScore`), `runPlacePhase` (bewertet alle angrenzenden, für Platzieren zulässigen Felder um den KI-König – also leere Felder und Felder mit eigenen Einheiten – mit Score `-steps + 2*enemies - fellows` und wählt anhand der ATK-gewichteten Hand eine Einheit; **vor dem Platzieren** wird das Zielfeld per `setSelectedField` gewählt, analog zu select vor place), `runUnitMovesLoop` (bewegliche Einheiten, `computeUnitMoveOptions` pro Einheit, Block/Zug/`executeMove`; bei **Bewegung en place** wird `<name> moves to <field>.` vor dem Board ausgegeben), `yieldTurn`. `executeMove` nutzt für Merge auf eigenem Team `executeMergeOrEliminate`. Wird nach `yield` ausgeführt, wenn `currentTeam == team2`.

## Noch offen / geplant
- Ggf. Feinschliff an Ausgabeformaten (z. B. Duell „???“ vor Aufdecken) und Tests gegen Beispielinteraktion aus dem Aufgaben-PDF.
- Checkstyle durchlaufen lassen und ggf. Anpassungen.



## Konventionen & Besonderheiten
- **Methodenlänge:** Jede Methode ist maximal 60 Zeilen lang (Refaktorierung umgesetzt). Lange Abläufe sind in Hilfsmethoden ausgelagert (z. B. AIPlayer: `runKingMove`, `runPlacePhase`, `runUnitMovesLoop`, `computeUnitMoveOptions`, `executeMergeOrEliminate`; Game: `addDuelIntroLines`, `resolveDuelVsKing`, `resolveBlockedDuel`, `resolveStandardDuel`; MoveCommand/PlaceCommand: execute-Pfade und Validierung in eigene Methoden).
- Exceptions: `GameException` (mit `getFormattedMessage()`), `StartupException` für Startfehler, `CommandException` für Laufzeitfehler bei Befehlen.
- Einheiten aus Config: `UnitTemplate` (qualifier, role, atk, def) → beim Befüllen der Decks `new BasicUnit(...)` pro Karte.
- Team-Referenz auf Units: `unit.setTeam(team)` beim Ziehen in die Hand.
- Spielname in Doku: exakt „Krone von Ackerland“ mit Zeichen а (U+0430).
