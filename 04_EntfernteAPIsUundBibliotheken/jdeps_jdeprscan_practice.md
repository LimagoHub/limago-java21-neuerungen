# Üben mit jdeps und jdeprscan (Migration Java 8 → 11 → 17 → 21)

Dieses Dokument ist eine **praxisorientierte Übung** für die beiden wichtigsten
Analysetools im JDK:

- `jdeps` – Analyse von Abhängigkeiten und Modulen
- `jdeprscan` – Finden von deprecated und zur Entfernung vorgesehenen APIs

Ziel ist, ein bewusst „problematisches“ JAR schrittweise gegen **Java 11, 17 und 21**
zu prüfen – so wie es bei einer realen Migration von Java 8 auf moderne LTS-Releases
der Fall wäre.

---

## 1. Ziel der Übung

Mit dieser Übung lernst du:

- welche Abhängigkeiten ein JAR wirklich hat (inkl. entfernter Module)
- welche Java-8-APIs in Java 11, 17 und 21 problematisch oder entfernt sind
- wie sich die Warnungen von `jdeps` und `jdeprscan` mit höheren JDK-Versionen verändern
- wie man daraus konkrete Migrationsschritte ableitet

---

## 2. Beispielcode für ein Legacy-JAR (Java-8-Stil)

Der folgende Code nutzt **veraltete und entfernte APIs**, um realistische
Warnungen zu erzeugen – ideal als Übungsobjekt für alle Migrationen
von Java 8 bis 21.

```java
package demo;

import javax.xml.bind.DatatypeConverter; // ab Java 11 nicht mehr im JDK
import java.util.Date;

public class LegacyExample {

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(date.toGMTString()); // deprecated seit langem

        byte[] bytes = DatatypeConverter.parseHexBinary("CAFE");
        System.out.println(bytes.length);
    }
}
```

Typische Java-8-Altlasten in diesem Beispiel:

- Verwendung von `javax.xml.bind` (JAXB) – in Java 11 aus dem JDK entfernt
- Nutzung einer alten, seit langem deprecated Methode in `java.util.Date`

---

## 3. Kompilieren und JAR erstellen

Kompiliere zuerst mit einem JDK deiner Wahl (z. B. Java 8 oder 11):

```bash
javac demo/LegacyExample.java
jar --create --file legacy-example.jar demo/*.class
```

Ergebnis:
```
legacy-example.jar
```

Dieses JAR verwenden wir anschließend mit verschiedenen JDK-Versionen (11, 17, 21).

---

## 4. Analyse mit jdeps – Schritt für Schritt über die JDK-Versionen

### 4.1 Grundlegende Abhängigkeitsanalyse (z. B. mit JDK 11)

```bash
jdeps legacy-example.jar
```

Typische Erkenntnisse:

- Abhängigkeit zu `java.base`
- Abhängigkeit zu Modulen/Bibliotheken, die **nicht mehr im JDK** enthalten sind
  (z. B. JAXB, Java-EE-Module)

---

### 4.2 Zusammenfassung anzeigen

```bash
jdeps --summary legacy-example.jar
```

✔ Gut geeignet für einen ersten Überblick  
✔ Zeigt Modul-zu-Modul-Abhängigkeiten

---

### 4.3 Analyse im Kontext eines bestimmten Releases (z. B. Java 11)

Mit `--multi-release` kannst du gezielt ein Ziel-Release betrachten:

```bash
jdeps --multi-release 11 legacy-example.jar
```

- Zeigt, welche Abhängigkeiten **unter Java 11** problematisch sind
- Du kannst dasselbe JAR später auch mit JDK 17 und 21 analysieren:

```bash
jdeps --multi-release 17 legacy-example.jar
jdeps --multi-release 21 legacy-example.jar
```

> So siehst du, wie sich die Lage mit zunehmender JDK-Version verändert.

---

### 4.4 Suche nach JDK-Interna

```bash
jdeps --jdk-internals legacy-example.jar
```

- Erkennt die Nutzung interner JDK-APIs (`sun.*`, etc.)
- Diese werden mit höheren JDK-Versionen zunehmend restriktiver gehandhabt

---

## 5. Analyse mit jdeprscan – Deprecated-APIs über Releases verfolgen

`jdeprscan` durchsucht Code nach:

- deprecated APIs
- APIs mit „forRemoval=true“ (also zur Entfernung vorgesehen)

Wichtig:  
`jdeprscan` nutzt den Wissensstand des **aktuellen JDKs**, mit dem du es ausführst.

---

### 5.1 Scan mit JDK 11

```bash
jdeprscan legacy-example.jar
```

Erwartete Treffer (u. a.):

- `Date.toGMTString()` – seit langem deprecated  
- ggf. Hinweise auf JAXB-Nutzung

---

### 5.2 Scan mit JDK 17 und 21 wiederholen

Führe denselben Befehl mit einem JDK 17 und JDK 21 aus:

```bash
jdeprscan legacy-example.jar   # mit JDK 17
jdeprscan legacy-example.jar   # mit JDK 21
```

Mit jedem neueren LTS-Release:

- kommen **neue deprecations** hinzu
- werden einige APIs als „forRemoval“ markiert
- manche sind bereits entfernt (dann schlägt eher das Kompilieren/Laden fehl)

So kannst du erkennen:

- welche Stellen **kurzfristig** (bis 11) angepasst werden müssen
- welche Stellen **mittelfristig** (17) und **langfristig** (21+) Risiken bergen

---

### 5.3 Detaillierte Ausgabe

```bash
jdeprscan --verbose legacy-example.jar
```

- zeigt Klassen, Methoden und die JDK-Version, seit wann eine API deprecated ist
- ideal, um Migrationstabellen zu erstellen

---

## 6. Migration schrittweise planen: 8 → 11 → 17 → 21

Ein mögliches Vorgehen:

1. **Von Java 8 auf Java 11:**
   - mit JDK 11 `jdeps` und `jdeprscan` laufen lassen
   - entfernte Module (JAXB, Java-EE) identifizieren und durch externe Dependencies ersetzen
   - schwerwiegende Deprecated-APIs ersetzen

2. **Von Java 11 auf Java 17:**
   - mit JDK 17 erneut `jdeprscan` ausführen
   - neue deprecations betrachten (z. B. Security Manager)
   - auf neuere APIs / Patterns migrieren

3. **Von Java 17 auf Java 21:**
   - mit JDK 21 `jdeps` / `jdeprscan` laufen lassen
   - letzte Deprecated-APIs und Interna aufspüren
   - Code stilistisch/technisch modernisieren (Records, Pattern Matching, etc.)

---

## 7. Typische Ergebnisse & Interpretation

| Tool | Erkennt |
|----|--------|
| jdeps | Modulabhängigkeiten, fehlende Java-EE/JAXB-Module |
| jdeps --multi-release | Unterschiede zwischen Ziel-Releases (11 / 17 / 21) |
| jdeps --jdk-internals | Nutzung interner JDK-APIs |
| jdeprscan | deprecated und „forRemoval“-APIs je nach JDK-Version |

---

## 8. Warum dieses JAR gut zum Üben bis Java 21 ist

Das Beispiel-JAR:

- nutzt klassische Java-8-APIs (JAXB, alte Date-Methoden)
- triggert realistische Warnungen in **11, 17 und 21**
- ist klein, leicht verständlich und modifizierbar

Du kannst es erweitern, z. B.:

- veraltete Security-APIs
- alte Collections-Patterns
- eigene, historische Utility-Klassen

---

## 9. Fazit

Für Migrationen von Java 8 bis 21 bieten sich `jdeps` und `jdeprscan` als
**erste, automatisierte Analysewerkzeuge** an:

1. **Mit verschiedenen JDK-Versionen ausführen** (11, 17, 21)
2. Unterschiede in den Warnungen beobachten
3. Migrationsschritte priorisieren:
   - entfernte Module/APIs → kritisch
   - deprecated → mittelfristig anpassen
   - forRemoval → bald kritisch

Dieses Vorgehen hilft, Migrationen strukturiert zu planen – statt erst
bei Compile- oder Laufzeitfehlern überrascht zu werden.

---

