## Neuerungen in `java.util.Objects` seit Java 11

Die Klasse `java.util.Objects` ist seit Java 11 weitgehend stabil, wurde aber in zwei Versionen erweitert:

- **Java 16**: neue `long`-Überladungen für Index-/Bereichsprüfungen  
- **Java 19**: neue Methode `toIdentityString(Object)`

---

### 🧮 Java 16 – `long`-Varianten der Bounds-Checks

Bereits seit Java 9 gab es die folgenden Methoden mit `int`-Parametern:

```java
int  Objects.checkIndex(int index, int length);
int  Objects.checkFromToIndex(int fromIndex, int toIndex, int length);
int  Objects.checkFromIndexSize(int fromIndex, int size, int length);
```

In **Java 16** kamen äquivalente Varianten für sehr große Datenstrukturen mit `long`-Indizes dazu:

```java
long Objects.checkIndex(long index, long length);
long Objects.checkFromToIndex(long fromIndex, long toIndex, long length);
long Objects.checkFromIndexSize(long fromIndex, long size, long length);
```

**Zweck:**  
Die Methoden prüfen, ob Indizes bzw. Bereiche gültig sind, und werfen bei Fehlern eine `IndexOutOfBoundsException`.  
Sie geben den geprüften Wert zurück, sodass man sie gut in Ausdrücke einbauen kann.

**Beispiel:**

```java
public static byte[] slice(byte[] data, long fromIndex, long size) {
    long length = data.length;
    Objects.checkFromIndexSize(fromIndex, size, length); // throws bei Ungültigkeit

    int from = (int) fromIndex;
    int to   = (int) (fromIndex + size);
    return Arrays.copyOfRange(data, from, to);
}
```

---

### 🆕 Java 19 – `Objects.toIdentityString(Object o)`

Mit **Java 19** kam eine neue Methode hinzu:

```java
public static String toIdentityString(Object o)
```

Sie liefert eine **Identitätsdarstellung** eines Objekts, also:

- den **konkreten Klassennamen** und
- den **identity hash code** in Hexdarstellung,

ähnlich dem Standard-`toString()` von `Object`, aber **unabhängig** davon, ob `toString()` oder `hashCode()` überschrieben wurden.

**Beispiel:**

```java
record Person(String name) {}

public static void main(String[] args) {
    Person p = new Person("Alice");

    System.out.println(p.toString());
    // Person[name=Alice]

    System.out.println(Objects.toIdentityString(p));
    // z.B. my.pkg.Person@5e2de80c
}
```

---

### Kurzfazit

Zwischen **Java 11 und Java 21** sind in `java.util.Objects` im Wesentlichen hinzugekommen:

- **Java 16**:  
  `checkIndex(long, long)`,  
  `checkFromToIndex(long, long, long)`,  
  `checkFromIndexSize(long, long, long)`

- **Java 19**:  
  `toIdentityString(Object o)`
