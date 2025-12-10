# Weitere Neuerungen im Überblick (Java 9–21)

Dieses Kapitel fasst **wichtige, oft unterschätzte Neuerungen** zusammen,
die nicht direkt Sprachsyntax betreffen, aber **den Alltag mit Java stark verändert haben**.

Behandelt werden:

- JVM-Änderungen & Wegfall der Finalization
- Java-Kommandozeile: `jshell`
- Direkte Programmausführung ohne expliziten Compilerlauf
- Simple Web Server

---

## 1. JVM-Änderungen: Wegfall der Finalization

### 1.1 Was war Finalization?

Finalization basierte auf der Methode:

```java
protected void finalize() throws Throwable { }
```

Sie wurde vom Garbage Collector **irgendwann** vor dem Löschen eines Objekts aufgerufen.

### Probleme von Finalization ❌

- unvorhersehbare Ausführung
- schlechte Performance
- Sicherheitsprobleme
- Ressourcen-Leaks
- kompliziertes Lebenszyklusmodell

➡️ **Finalization galt seit Jahren als Anti-Pattern**

---

### 1.2 Entwicklung & Entfernung

| Java-Version | Status |
|-------------|-------|
| Java 9 | Deprecated (Warnung) |
| Java 18 | Deprecated for removal |
| Java 21 | Finalization faktisch entfernt |

➡️ In modernem Java **nicht mehr verwenden**.

---

### 1.3 Ersatz für Finalization ✅

✅ **`try-with-resources`**

```java
try (var in = new FileInputStream("data.txt")) {
    // arbeiten mit Ressource
}
```

✅ **`AutoCloseable` / `Closeable`**

✅ **`Cleaner` API** (für Sonderfälle)

```java
Cleaner cleaner = Cleaner.create();
```

---

## 2. Java-Kommandozeile: JShell

### 2.1 Was ist JShell?

`jshell` ist eine **interaktive Java-Shell** (REPL),
eingeführt in **Java 9**.

➡️ Java-Code **ohne Projekt, ohne Klasse, ohne main-Methode**

---

### 2.2 Starten von JShell

```bash
jshell
```

Beispiel:

```java
jshell> int x = 10;
jshell> x * 2
$2 ==> 20
```

---

### 2.3 Typische Anwendungsfälle

✅ schnelles Ausprobieren von APIs  
✅ Lernen von Java  
✅ Prototyping  
✅ Debugging kleiner Logik  

---

### 2.4 JShell mit Klassen & Methoden

```java
jshell> int add(int a, int b) { return a + b; }
jshell> add(2, 3)
$3 ==> 5
```

---

## 3. Direkte Programmausführung ohne Compilerlauf

### 3.1 Klassischer Weg (früher)

```bash
javac Hello.java
java Hello
```

---

### 3.2 Neuer Weg (seit Java 11 ✅)

```bash
java Hello.java
```

➡️ `javac` wird **implizit** ausgeführt  
➡️ ideal für:
- Skripte
- kleine Tools
- Lernbeispiele

---

### 3.3 Beispiel

```java
// Hello.java
System.out.println("Hello Java!");
```

Ausführen:

```bash
java Hello.java
```

✅ kein `main`-Wrapper nötig (Single-File-Source-Code)

---

### 3.4 Einschränkungen ⚠️

- nur **eine** Quelldatei
- keine expliziten Module
- nicht für große Projekte gedacht

---

## 4. Simple Web Server

### 4.1 Motivation

Schnell einen **lokalen HTTP-Server** starten:

- ohne Framework
- ohne Code
- ohne Build

---

### 4.2 Start des Simple Web Servers (Java 18+)

```bash
jwebserver
```

Standard:

- Port: `8000`
- Root: aktuelles Verzeichnis

Aufruf im Browser:

```
http://localhost:8000
```

---

### 4.3 Konfigurationsoptionen

```bash
jwebserver --port 9000 --directory ./public
```

---

### 4.4 Typische Einsatzfälle

✅ lokale Tests  
✅ Frontend-Entwicklung  
✅ Demos  
✅ Schulungen  

❌ **kein Produktionsserver**

---

## 5. Zusammenfassung

| Feature | Java-Version |
|------|-------------|
| JShell | Java 9 |
| Single-File-Ausführung | Java 11 |
| Simple Web Server | Java 18 |
| Finalization entfernt | Java 21 |

---

## 6. Merksätze

- **Finalization ist tot – nutze `try-with-resources`.**
- **JShell ist perfekt zum Lernen & Experimentieren.**
- **`java Hello.java` ist ideal für kleine Tools.**
- **`jwebserver` ist schnell, aber nur für lokale Nutzung.**

---

Dieses Kapitel eignet sich als:
- Überblickskapitel in Java-17–21-Tutorials
- Nachschlagewerk
- Ergänzung zu Sprach- und API-Neuerungen
