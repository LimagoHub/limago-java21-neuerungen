# Java 17–21 – Praktische Neuerungen im Überblick

Dieses Repository zeigt anhand kleiner, fokussierter Beispiele die wichtigsten Sprach- und API-Neuerungen von **Java 17** bis **Java 21 (LTS)**.

Ziel ist ein **praxisnahes Tutorial**, das du lokal ausführen und als Grundlage für eigene Schulungen oder Workshops nutzen kannst.

---

## Inhaltsverzeichnis

- [Erweiterungen in der Syntax von Java](#erweiterungen-in-der-syntax-von-java)
  - [switch Expressions](#switch-expressions)
  - [Text Blocks](#text-blocks)

- [Neuerungen in der Standard-API](#neuerungen-in-der-standard-api)
  - [SequencedCollection E API](#sequencedcollection-e-api)

- [Neuerungen in einzelnen Klassen](#neuerungen-in-einzelnen-klassen)
  - [Objects, String](#objects-string)
  - [Stream T](#stream-t)
  - [NullPointerException](#nullpointerexception)

- [Entfernte APIs und Bibliotheken](#entfernte-apis-und-bibliotheken)
  - [Deprecated APIs und Klassen](#deprecated-apis-und-klassen)
  - [Werkzeug jdeprscan](#werkzeug-jdeprscan)

- [Algebraische Datentypen und Pattern Matching](#algebraische-datentypen-und-pattern-matching)
  - [Sealed Classes](#sealed-classes)
  - [Records](#records)
  - [Pattern Matching fuer instanceof](#pattern-matching-fuer-instanceof)
  - [Pattern Matching fuer switch](#pattern-matching-fuer-switch)
  - [Pattern Matching zur Record-Dekonstruktion](#pattern-matching-zur-record-dekonstruktion)

- [Neuerungen fuer die nebenlaeufige Programmierung](#neuerungen-fuer-die-nebenlaeufige-programmierung)
  - [Virtual Threads](#virtual-threads)
  - [Structured Concurrency](#structured-concurrency)
  - [Virtual Threads in Quarkus](#virtual-threads-in-quarkus)

- [Weitere Neuerungen im Ueberblick](#weitere-neuerungen-im-ueberblick)
  - [JVM-Aenderungen, Wegfall der Finalization](#jvm-aenderungen-wegfall-der-finalization)
  - [Java-Kommandozeile jshell](#java-kommandozeile-jshell)
  - [Direkte Programmausfuehrung ohne Compilerlauf](#direkte-programmausfuehrung-ohne-compilerlauf)
  - [Simple Web Server](#simple-web-server)

---

## Voraussetzungen

- Java Development Kit (**JDK 21**) installiert  
  (frühere Versionen funktionieren für einige Beispiele nicht, da Features erst in Java 21 final wurden)
