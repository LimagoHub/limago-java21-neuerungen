## 1. Was bedeutet „geordnet“ bei einer `List`?

Eine `List` besitzt **seit Java 1.2** immer:

- eine **stabile Einfügereihenfolge**
- Index-Zugriff:
  - `list.get(0)`
  - `list.get(list.size() - 1)`
- deterministische Iterationsreihenfolge

Beispiele:  
`ArrayList`, `LinkedList`, `Vector` (historisch)

**Das Verhalten war schon immer so und hat sich nicht geändert.**

---

## 2. Warum wurde `List` erst in Java 21 zu einer `SequencedCollection`?

Obwohl `List` geordnet ist, fehlte Java lange Zeit eine **einheitliche API** für alle Collections mit Encounter-Order.

Vor Java 21:

- `List` hatte **Indexzugriff**, aber keine `getFirst()` / `getLast()` Methoden.
- `Deque` hatte `getFirst()` / `getLast()`, aber keinen Indexzugriff.
- `SortedSet` hatte `first()` / `last()`, aber keine „Add at front/back“-Operationen.
- `LinkedHashSet` war geordnet, aber ohne direkte Möglichkeit, das erste oder letzte Element zu holen.

Java hatte also viele **geordneten Collection-Typen**, aber **keine gemeinsame Schnittstelle**.

---

## 3. Java 21: Die neue, einheitliche API (JEP 431)

Java 21 führt folgende Interfaces ein:

- `SequencedCollection<E>`
- `SequencedSet<E>`
- `SequencedMap<K, V>`

Alle Collections mit definierter Encounter-Order implementieren jetzt eines dieser Interfaces, z. B.:

- `List`
- `Deque`
- `LinkedHashSet`
- `TreeSet`
- `LinkedHashMap` (als SequencedMap)
- `TreeMap`

Diese Interfaces führen folgende neue Methoden ein:

```java
addFirst(E e)
addLast(E e)

getFirst()
getLast()

removeFirst()
removeLast()

reversed()


# Neuerungen in Java Collections von Java 17 bis Java 21  
## Schwerpunkt: SequencedCollection, SequencedSet und SequencedMap (Java 21)

Dieses Dokument fasst die wichtigsten Entwicklungen im Java-Collections-Framework seit **Java 17** zusammen.  
Der größte Fortschritt kommt mit **Java 21 (JEP 431: Sequenced Collections)**.

---

# 1. Änderungen in Java 18–20 (kurz)

## Java 18
- Keine wesentlichen Änderungen im Collections-Framework.

## Java 19 – Neue Factory-Methoden für vorinitialisierte Maps/Sets
Java 19 führt optimierte Factory-Methoden ein, die besser berechnete Anfangskapazitäten verwenden:

```java
Map<String, Integer> m1 = HashMap.newHashMap(120);
Map<String, Integer> m2 = LinkedHashMap.newLinkedHashMap(120);
Set<String> s1 = HashSet.newHashSet(120);
Set<String> s2 = LinkedHashSet.newLinkedHashSet(120);
Map<String, Integer> m3 = WeakHashMap.newWeakHashMap(120);
```

Zweck:
- weniger Rehashing  
- effizienter für große erwartete Collection-Größen  

## Java 20
- Keine relevanten Collection-Neuerungen.

---

# 2. Java 21 – Sequenced Collections (JEP 431)

Java 21 führt drei neue Interfaces ein:

- **SequencedCollection<E>**
- **SequencedSet<E>**
- **SequencedMap<K, V>**

Ziel:  
Eine einheitliche API für alle Collections, die eine **definierte Encounter-Order** besitzen.

---

# 3. `SequencedCollection<E>`

Signatur (vereinfacht):

```java
public interface SequencedCollection<E> extends Collection<E> {

    SequencedCollection<E> reversed();

    void addFirst(E e);
    void addLast(E e);

    E getFirst();
    E getLast();

    E removeFirst();
    E removeLast();
}
```

### Eigenschaften
- garantiert eine **Encounter Order**
- unterstützt Operationen am **Anfang** und **Ende**
- `reversed()` liefert eine **View**, keine Kopie

### Implementierungen ab Java 21
- ArrayList  
- LinkedList  
- ArrayDeque  
- TreeSet  
- LinkedHashSet  

---

# 4. `SequencedSet<E>`

```java
public interface SequencedSet<E>
        extends Set<E>, SequencedCollection<E> {

    @Override
    SequencedSet<E> reversed();
}
```

### Beispiel

```java
SequencedSet<String> set = new LinkedHashSet<>();
set.add("A");
set.add("B");
set.add("C");

System.out.println(set.getFirst()); // "A"
System.out.println(set.getLast());  // "C"
```

---

# 5. `SequencedMap<K, V>`

```java
public interface SequencedMap<K,V> extends Map<K,V> {

    SequencedMap<K,V> reversed();

    SequencedSet<K>          sequencedKeySet();
    SequencedCollection<V>   sequencedValues();
    SequencedSet<Entry<K,V>> sequencedEntrySet();

    V putFirst(K key, V value);
    V putLast(K key, V value);

    Entry<K,V> firstEntry();
    Entry<K,V> lastEntry();
}
```

### Beispiel

```java
SequencedMap<Integer, String> map = new LinkedHashMap<>();
map.putLast(1, "eins");
map.putLast(2, "zwei");
map.putFirst(0, "null");

System.out.println(map.firstEntry()); // 0=null
```

---

# 6. Zusammenfassung

| Java-Version | Collections-Neuerungen |
|--------------|------------------------|
| **17** | stabile Basis |
| **18** | keine Änderungen |
| **19** | *newHashMap*, *newHashSet* usw. |
| **20** | keine Änderungen |
| **21** | **SequencedCollection**, **SequencedSet**, **SequencedMap** |

---

# 7. Moderne API-Empfehlung

```java
void printBounds(SequencedCollection<?> col) {
    System.out.println("first = " + col.getFirst());
    System.out.println("last  = " + col.getLast());
}
```

