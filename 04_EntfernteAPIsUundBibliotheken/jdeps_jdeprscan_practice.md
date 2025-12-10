# Üben mit jdeps und jdeprscan (Java 8 → Java 11)

Dieses Dokument dient als **praxisorientierte Übung** für die beiden wichtigsten
Migrationswerkzeuge beim Übergang von **Java 8 zu Java 11**:

- `jdeps` – Analyse von Abhängigkeiten
- `jdeprscan` – Finden von deprecated APIs

Das Ziel ist, ein bewusst „problematisches“ JAR zu analysieren, wie es in vielen
realen Legacy-Anwendungen vorkommt.

---

## 1. Ziel der Übung

Mit dieser Übung lernst du:

- welche Abhängigkeiten ein JAR wirklich hat
- welche Java‑8‑APIs in Java 11 problematisch oder entfernt sind
- wie man typische Migrationsrisiken frühzeitig erkennt

---

## 2. Beispielcode für ein Legacy‑JAR

Der folgende Code nutzt **veraltete und entfernte APIs**, um realistische
Warnungen zu erzeugen.

```java
package demo;

import javax.xml.bind.DatatypeConverter; // ab Java 11 nicht mehr im JDK
import java.util.Date;

public class LegacyExample {

    public static void main(String[] args) {
        Date date = new Date();
        System.out.println(date.toGMTString()); // deprecated

        byte[] bytes = DatatypeConverter.parseHexBinary("CAFE");
        System.out.println(bytes.length);
    }
}
```

---

## 3. Kompilieren und JAR erstellen

```bash
javac demo/LegacyExample.java
jar --create --file legacy-example.jar demo/*.class
```

Ergebnis:
```
legacy-example.jar
```

---

## 4. Analyse mit jdeps

### 4.1 Grundlegende Abhängigkeitsanalyse

```bash
jdeps legacy-example.jar
```

Typische Erkenntnisse:

- Abhängigkeiten zu `java.base`
- Abhängigkeiten zu Modulen, die ab Java 11 fehlen (z. B. JAXB)

---

### 4.2 Zusammenfassung anzeigen

```bash
jdeps --summary legacy-example.jar
```

✔ Gut geeignet für einen schnellen Überblick  
✔ Zeigt nur Modul‑zu‑Modul‑Abhängigkeiten

---

### 4.3 Analyse im Kontext von Java 11

```bash
jdeps --multi-release 11 legacy-example.jar
```

➡️ Zeigt, welche Abhängigkeiten **unter Java 11 problematisch** sind.

---

### 4.4 Suche nach JDK‑Interna

```bash
jdeps --jdk-internals legacy-example.jar
```

✔ Erkennt verbotene oder instabile interne APIs  
✔ Wichtig für langfristige Wartbarkeit

---

## 5. Analyse mit jdeprscan

`jdeprscan` sucht gezielt nach **deprecated APIs**, die:

- entfernt wurden
- in zukünftigen Releases verschwinden könnten

### 5.1 Einfacher Scan

```bash
jdeprscan legacy-example.jar
```

Typische Ausgabe:

- Nutzung von `Date.toGMTString()` (deprecated)
- Hinweise auf veraltete APIs aus `javax.*`

---

### 5.2 Scan mit Detailinformationen

```bash
jdeprscan --verbose legacy-example.jar
```

✔ zeigt Klassen und Methoden im Detail  
✔ ideal für Migrationslisten

---

## 6. Typische Ergebnisse & Interpretation

| Tool | Erkennt |
|----|--------|
| jdeps | fehlende Module, externe Abhängigkeiten |
| jdeps --multi-release | Java‑11‑Inkompatibilitäten |
| jdeps --jdk-internals | verbotene interne APIs |
| jdeprscan | veraltete / entfernte APIs |

---

## 7. Warum dieses JAR gut zum Üben ist

Dieses Übungs‑JAR:

- simuliert reale Java‑8‑Altanwendungen
- nutzt entfernte Module (`javax.xml.bind`)
- nutzt deprecated APIs
- ist klein und leicht verständlich

➡️ Ideal für Schulungen, Tutorials und eigene Experimente.

---

## 8. Fazit

`jdeps` und `jdeprscan` sind **essenzielle Werkzeuge** für jede Java‑8‑→‑Java‑11‑Migration.

Best Practice:

1. zuerst `jdeps` → Abhängigkeiten verstehen  
2. dann `jdeprscan` → API‑Risiken erkennen  
3. danach Refactoring & Abhängigkeiten anpassen  

Dieses Vorgehen reduziert Migrationsrisiken erheblich.

---

Dieses Dokument eignet sich als **Kapitel in einem Java‑Migrations‑Tutorial**
oder als **README** für ein Übungs‑Repository.
