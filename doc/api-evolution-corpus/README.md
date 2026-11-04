# japicmp und der API-Evolution-Datenkorpus von Jezek & Dietrich

Dieses Dokument prüft japicmp gegen den Korpus aus

> K. Jezek and J. Dietrich, "API evolution and compatibility: A data corpus and tool evaluation,"
> *Journal of Object Technology*, vol. 16, no. 4, pp. 2:1–23, 2017.
> Korpus: <https://github.com/kjezek/api-evolution-data-corpus> (Commit `407398e`)

## 1. Wie das Paper die Testfälle systematisch aufbaut

Das Paper legt die Testfälle entlang von drei Dimensionen an:

| Dimension | Ausprägungen |
|---|---|
| **Was** ändert sich (Kategorie) | Access Modifiers, Data Types, Exceptions, Generics (Typ-Parameter und Wildcards), Inheritance, Members, Other (non-access) Modifiers, Miscellaneous |
| **Wo** wird geändert (Element) | Klasse, innere (nested) Klasse, Interface, Methode, Konstruktor, Feld, generischer Typ |
| **Wie** wird geändert (Änderung) | hinzufügen, entfernen, verstärken, abschwächen (bei Datentypen auch Boxing/Unboxing, Widening/Narrowing, Generalization/Specialization; bei Methoden getrennt für Parameter und Rückgabetyp) |

Das ergibt rechnerisch 7 × 8 × 4 = 224 Kombinationen. Kombinationen, die nicht kompilieren (z. B. ein Access-Modifier an einer Interface-Methode in Java 7), fallen weg. Bei Methoden, Datentypen und Generics kommen zusätzliche Fälle hinzu. So entstehen **251 Szenarien**. Jedes Szenario ist ein Java-Package mit dem Namen `<category><element><change>`. Es enthält eine Klasse in `lib-v1`, die geänderte Klasse in `lib-v2` und einen Client in `client`.

Das **Oracle** (`compatibility.csv`) wird dynamisch erzeugt:

* **source-inkompatibel:** Der Client, der gegen `lib-v1` kompiliert, lässt sich gegen `lib-v2` nicht mehr kompilieren.
* **binär-inkompatibel:** Der gegen `lib-v1` kompilierte Client bricht mit `lib-v2` zur Laufzeit ab (Linkage-Fehler).

Wichtig für die Auswertung: Das Oracle beschreibt, was mit dem *konkreten Client* des Szenarios passiert. Ein statisches Werkzeug wie japicmp kennt den Client nicht. Es muss daher annehmen, dass jeder mögliche Client betroffen sein kann. Meldet japicmp eine Inkompatibilität, die der Beispiel-Client nicht auslöst, ist das deshalb meist **konservativ** und kein Fehler.

## 2. Vorgehen

1. `lib-v1` und `lib-v2` des Korpus werden mit `javac --release 8` zu `lib-v1.jar` und `lib-v2.jar` gebaut.
2. Diese Jars werden mit der japicmp-API (`JarArchiveComparator`, Standardoptionen, also `protected` und höher) verglichen ([`CorpusEvaluation.java`](CorpusEvaluation.java)).
3. Pro Szenario (Package unter `testing_lib`) wird gesammelt:
   * `isBinaryCompatible()` und `isSourceCompatible()` aller Klassen des Packages,
   * alle gemeldeten `JApiCompatibilityChangeType`s (Klasse, Superklasse, Interfaces, Methoden, Konstruktoren, Parameter, Rückgabetypen, Felder, generische Templates).
4. Das Ergebnis wird mit dem Oracle des Korpus verglichen ([`compare.py`](compare.py)).

Reproduzieren lässt sich das so (japicmp vorher bauen):

```bash
mvn -pl japicmp -am install -DskipTests
doc/api-evolution-corpus/run.sh
```

Geprüft wurde japicmp `0.26.6-SNAPSHOT`.

### Bewertungsschlüssel

| Symbol | Bedeutung |
|---|---|
| ✅ korrekt | Die binäre und die source-Einstufung von japicmp stimmen mit dem Oracle überein. |
| ⚠️ konservativ | japicmp meldet eine Inkompatibilität, die der Client im Korpus nicht auslöst, ein anderer Client aber auslösen kann (siehe K1–K6). |
| ❌ Lücke | Das Oracle meldet eine Inkompatibilität, die japicmp **nicht** erkennt. Diese Prüfung fehlt in japicmp (siehe G1–G3). |
| ℹ️ nicht statisch prüfbar / Oracle | Die Abweichung kommt aus einem Fehler im Oracle oder aus reinem Laufzeitverhalten (siehe O1, O2). |

✔ = kompatibel, ✘ = inkompatibel.

## 3. Ergebnis im Überblick

| Kategorie | Szenarien | ✅ korrekt | ⚠️ konservativ | ❌ Lücke | ℹ️ Oracle | inkompatible Szenarien | davon von japicmp erkannt | Erkennungsrate heute | japicmp 0.7.2 im Paper |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Access Modifiers | 18 | 18 | 0 | 0 | 0 | 6 | 6 | 100,00 % | 100,00 % |
| Data Types | 49 | 29 | 20 | 0 | 0 | 47 | 47 | 100,00 % | 100,00 % |
| Exceptions | 16 | 14 | 1 | 0 | 1 | 7 | 7 | 100,00 % | 100,00 % |
| Generics (Wildcards) | 28 | 17 | 11 | 0 | 0 | 17 | 17 | 100,00 % | 0,00 % ¹ |
| Generics | 60 | 37 | 17 | 6 | 0 | 34 | 28 | 82,35 % | 0,00 % ¹ |
| Inheritance | 16 | 12 | 2 | 2 | 0 | 7 | 5 | 71,43 % | 100,00 % |
| Members | 28 | 22 | 6 | 0 | 0 | 19 | 19 | 100,00 % | 100,00 % |
| Other Modifiers | 30 | 26 | 2 | 1 | 1 | 13 | 11 | 84,62 % | 84,62 % |
| Miscellaneous | 6 | 6 | 0 | 0 | 0 | 4 | 4 | 100,00 % | 100,00 % |
| **Summe** | **251** | **181** | **59** | **9** | **2** | **154** | **144** | **93,51 %** | **65,58 %** |

¹ Das Paper führt Generics als eine Kategorie (88 Szenarien). Hier ist sie in Typ-Parameter (60) und Wildcards (28) geteilt.

„Erkennungsrate“ ist wie im Paper (Tabelle „Correctly Detected Incompatibilities in Each Scenario“) der Anteil der inkompatiblen Szenarien, die japicmp als inkompatibel meldet. Aufgeteilt wie in der Tabelle „Correctly Detected Scenarios Divided into Source and Binary Incompatibilities“:

| Typ | Szenarien | erkannt heute | Rate heute | japicmp 0.7.2 im Paper |
|---|---:|---:|---:|---:|
| nur source-inkompatibel | 68 | 60 | 88,24 % | 25,00 % |
| binär-inkompatibel | 86 | 84 | 97,67 % | 97,67 % |

Strenger gezählt, also für jede Art getrennt: Von 132 source-inkompatiblen Szenarien stuft japicmp 124 als source-inkompatibel ein. Von 86 binär-inkompatiblen Szenarien stuft es 83 als binär-inkompatibel ein.

Seit Version 0.7.2 aus dem Paper hat sich japicmp vor allem bei **Generics** (0 % → 82–100 %) und bei **source-Inkompatibilitäten** (25 % → 88 %) verbessert. Bei *Inheritance* liegt der heutige Wert unter dem des Papers. Das Paper wertete die Textausgabe per String-Matching aus und zählte deshalb auch jede gemeldete Änderung wie `INTERFACE_ADDED` als Treffer. Diese Auswertung zählt nur, was japicmp tatsächlich als inkompatibel einstuft.

## 4. Lücken in japicmp (❌)

| ID | Szenarien | Was fehlt |
|---|---|---|
| **G1** | `genericsClazzTypeBoundsAddSecond`, `genericsClazzConstructorTypeBoundsAddSecond`, `genericsClazzMethodTypeBoundsAddSecond`, `genericsIfazeTypeBoundsAddSecond`, `genericsIfazeMethodTypeBoundsAddSecond`, `genericsIfazeMethodTypeBoundsDeleteSecond` | Es wird ein **zusätzlicher Bound** (Intersection Type) eines Typ-Parameters hinzugefügt oder entfernt, z. B. `<T extends Number>` → `<T extends Number & Comparable<T>>`. Eine Änderung des *ersten* Bounds erkennt japicmp (`CLASS_GENERIC_TEMPLATE_CHANGED`). Die weiteren Interface-Bounds vergleicht es nicht. In allen 10 `*TypeBounds(Add|Delete)Second`-Szenarien meldet japicmp deshalb gar keine Änderung. Als Lücke gezählt sind nur die 6, in denen das Oracle eine Inkompatibilität zeigt. |
| **G2** | `inheritanceIfazeStartInherite`, `inheritanceIfazeExpandSuperinterfaceSet` | Ein **Interface** bekommt ein neues Super-Interface mit abstrakten Methoden. Klassen, die das Interface implementieren, lassen sich dann nicht mehr kompilieren. japicmp meldet nur das kompatible `INTERFACE_ADDED`. Für Klassen gibt es `METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE`, für Interfaces fehlt eine entsprechende Prüfung (vergleichbar mit `METHOD_ADDED_TO_INTERFACE`). |
| **G3** | `modifierMethodNonNativeToNative` | Wird eine Methode `native`, endet der Aufruf ohne native Implementierung mit `UnsatisfiedLinkError`. japicmp wertet den Modifier `native` nicht aus. Streng genommen ist das Laufzeitverhalten. Eine eigene Meldung (z. B. `METHOD_NOW_NATIVE`) wäre trotzdem möglich. |

## 5. Abweichungen, die nicht auf japicmp zurückgehen (ℹ️)

| ID | Szenario | Erklärung |
|---|---|---|
| **O1** | `modifierMethodStrictfpToNonStrictfp` | Fehler im Oracle: Der Client dieses Szenarios benutzt die Klasse `ModifierMethodNonNativeToNative` statt `ModifierMethodStrictfpToNonStrictfp` (Copy & Paste). Den Laufzeitfehler löst also die `native`-Methode aus. `strictfp` hat keinen Einfluss auf die Kompatibilität, japicmp liegt hier richtig. |
| **O2** | `exceptionClazzMethodTryCatchToThrowChecked` | japicmp meldet korrekt `METHOD_NOW_THROWS_CHECKED_EXCEPTION` (source-inkompatibel). Die „binäre“ Inkompatibilität im Oracle ist eine `FileNotFoundException`, die der alte Client zur Laufzeit nicht mehr abfängt. Das ist Verhalten und kein Linkage-Fehler, eine statische Analyse kann es nicht erkennen. |

## 6. Konservative Meldungen (⚠️)

In diesen Fällen meldet japicmp eine Inkompatibilität, die der konkrete Client im Korpus nicht auslöst:

| ID | Szenarien | Begründung |
|---|---|---|
| **K1** | `dataTypeIfazeConstant*`, `members*ConstantDelete` | **Compile-Time-Konstanten** werden in den Client einkompiliert. Deshalb gibt es beim Entfernen oder beim Ändern des Typs keinen Linkage-Fehler, der Client arbeitet aber still mit dem alten Wert weiter (JLS §13.4.9). japicmp meldet `FIELD_REMOVED` bzw. `FIELD_TYPE_CHANGED` als binär-inkompatibel. Das ist gewollt und sinnvoll. |
| **K2** | `dataType*Boxing/Unboxing/Widening/Narrowing/Generalization/Specialization` | Durch Boxing und Widening kompiliert der Aufruf im Client weiter. Ein Client, der die Methode überschreibt oder den Wert zuweist, bricht dagegen schon. japicmp stuft jede Typänderung als source-inkompatibel ein. Eine feinere Analyse für Aufrufer wäre möglich, wäre für Unterklassen aber falsch. |
| **K3** | `generics*`, `genericsWildcards*` | japicmp stuft **jede** Änderung an Typ-Parametern und Generics von Parametern als source-inkompatibel ein (`CLASS_GENERIC_TEMPLATE_CHANGED`, `METHOD_PARAMETER_GENERICS_CHANGED`). Es gibt keine Varianzanalyse, die ein Lockern (Bound entfernen oder verallgemeinern, Wildcard aufweiten) von einem Verschärfen unterscheidet. Hinweis: Auch Änderungen an Typ-Parametern von *Methoden* und *Konstruktoren* werden als `CLASS_GENERIC_TEMPLATE_CHANGED` gemeldet. |
| **K4** | `inheritanceIfazeMethodMoved*`, `members*Delete`, `membersIfazeMethodParam*`, `membersClazzMethodAbstractDelete` | Der Client implementiert das Interface nur oder ruft die Methode nur über einen anderen Typ auf. Für andere Clients (Aufruf über das Interface bzw. über das Super-Interface) gelten die gemeldeten Änderungen `METHOD_REMOVED` und `METHOD_ADDED_TO_INTERFACE`. |
| **K5** | `exceptionClazzMethodThrowCheckedSpecialization` | Für Aufrufer ist eine speziellere Checked Exception unkritisch. Eine Unterklasse, die die Methode überschreibt und noch die allgemeinere Exception wirft, kompiliert aber nicht mehr. |
| **K6** | `modifierFieldNonStaticToStatic`, `modifierMethodNonStaticToStatic` | Ein statisches Member lässt sich weiterhin über eine Instanz ansprechen, der Client kompiliert also. Für überschreibende Unterklassen und für Zugriffe über `super` ist die Änderung trotzdem inkompatibel. |

## 7. Vollständige Matrix aller 251 Szenarien

| # | Kategorie | Szenario (`<category><element><change>`) | Oracle binär | Oracle source | japicmp binär | japicmp source | gemeldete japicmp-Änderungen | Bewertung |
|---:|---|---|:-:|:-:|:-:|:-:|---|---|
| 1 | Access Modifiers | `accessModifierClazzAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `CLASS_LESS_ACCESSIBLE`, `CLASS_NO_LONGER_PUBLIC`, `CONSTRUCTOR_LESS_ACCESSIBLE` | ✅ korrekt |
| 2 | Access Modifiers | `accessModifierClazzAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 3 | Access Modifiers | `accessModifierClazzConstructorAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_LESS_ACCESSIBLE` | ✅ korrekt |
| 4 | Access Modifiers | `accessModifierClazzConstructorAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 5 | Access Modifiers | `accessModifierClazzFieldAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `FIELD_LESS_ACCESSIBLE` | ✅ korrekt |
| 6 | Access Modifiers | `accessModifierClazzFieldAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 7 | Access Modifiers | `accessModifierClazzMethodAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `METHOD_LESS_ACCESSIBLE` | ✅ korrekt |
| 8 | Access Modifiers | `accessModifierClazzMethodAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 9 | Access Modifiers | `accessModifierClazzNestedClazzAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `CLASS_LESS_ACCESSIBLE`, `CLASS_NOW_NOT_EXTENDABLE`, `CLASS_NO_LONGER_PUBLIC`, `CONSTRUCTOR_LESS_ACCESSIBLE` | ✅ korrekt |
| 10 | Access Modifiers | `accessModifierClazzNestedClazzAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 11 | Access Modifiers | `accessModifierClazzNestedIfazeAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `CLASS_LESS_ACCESSIBLE`, `CLASS_NO_LONGER_PUBLIC` | ✅ korrekt |
| 12 | Access Modifiers | `accessModifierClazzNestedIfazeAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 13 | Access Modifiers | `accessModifierIfazeFieldAccessDecrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 14 | Access Modifiers | `accessModifierIfazeFieldAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 15 | Access Modifiers | `accessModifierIfazeMethodAccessDecrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 16 | Access Modifiers | `accessModifierIfazeMethodAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 17 | Access Modifiers | `accessModifierIfazeNestedIfazeAccessDecrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 18 | Access Modifiers | `accessModifierIfazeNestedIfazeAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 19 | Data Types | `dataTypeClazzConstructorParamBoxing` | ✘ | ✔ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ⚠️ konservativ (K2) |
| 20 | Data Types | `dataTypeClazzConstructorParamGeneralization` | ✘ | ✔ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ⚠️ konservativ (K2) |
| 21 | Data Types | `dataTypeClazzConstructorParamMutation` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ korrekt |
| 22 | Data Types | `dataTypeClazzConstructorParamNarrowing` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ korrekt |
| 23 | Data Types | `dataTypeClazzConstructorParamSpecialization` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ korrekt |
| 24 | Data Types | `dataTypeClazzConstructorParamUnboxing` | ✘ | ✔ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ⚠️ konservativ (K2) |
| 25 | Data Types | `dataTypeClazzConstructorParamWidening` | ✘ | ✔ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ⚠️ konservativ (K2) |
| 26 | Data Types | `dataTypeClazzFieldBoxing` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ konservativ (K2) |
| 27 | Data Types | `dataTypeClazzFieldGeneralization` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ korrekt |
| 28 | Data Types | `dataTypeClazzFieldMutation` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ korrekt |
| 29 | Data Types | `dataTypeClazzFieldNarrowing` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ konservativ (K2) |
| 30 | Data Types | `dataTypeClazzFieldSpecialization` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ konservativ (K2) |
| 31 | Data Types | `dataTypeClazzFieldUnboxing` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ konservativ (K2) |
| 32 | Data Types | `dataTypeClazzFieldWidening` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ korrekt |
| 33 | Data Types | `dataTypeClazzMethodParamBoxing` | ✘ | ✔ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ⚠️ konservativ (K2) |
| 34 | Data Types | `dataTypeClazzMethodParamGeneralization` | ✘ | ✔ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ⚠️ konservativ (K2) |
| 35 | Data Types | `dataTypeClazzMethodParamMutation` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ korrekt |
| 36 | Data Types | `dataTypeClazzMethodParamNarrowing` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ korrekt |
| 37 | Data Types | `dataTypeClazzMethodParamSpecialization` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ korrekt |
| 38 | Data Types | `dataTypeClazzMethodParamUnboxing` | ✘ | ✔ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ⚠️ konservativ (K2) |
| 39 | Data Types | `dataTypeClazzMethodParamWidening` | ✘ | ✔ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ⚠️ konservativ (K2) |
| 40 | Data Types | `dataTypeClazzMethodReturnTypeBoxing` | ✘ | ✔ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ⚠️ konservativ (K2) |
| 41 | Data Types | `dataTypeClazzMethodReturnTypeGeneralization` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 42 | Data Types | `dataTypeClazzMethodReturnTypeMutation` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 43 | Data Types | `dataTypeClazzMethodReturnTypeNarrowing` | ✘ | ✔ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ⚠️ konservativ (K2) |
| 44 | Data Types | `dataTypeClazzMethodReturnTypeSpecialization` | ✘ | ✔ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ⚠️ konservativ (K2) |
| 45 | Data Types | `dataTypeClazzMethodReturnTypeUnboxing` | ✘ | ✔ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ⚠️ konservativ (K2) |
| 46 | Data Types | `dataTypeClazzMethodReturnTypeWidening` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 47 | Data Types | `dataTypeIfazeConstantBoxing` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 48 | Data Types | `dataTypeIfazeConstantGeneralization` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ korrekt |
| 49 | Data Types | `dataTypeIfazeConstantMutation` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ korrekt |
| 50 | Data Types | `dataTypeIfazeConstantNarrowing` | ✔ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ konservativ (K1) |
| 51 | Data Types | `dataTypeIfazeConstantSpecialization` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ konservativ (K1) |
| 52 | Data Types | `dataTypeIfazeConstantUnboxing` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ konservativ (K1) |
| 53 | Data Types | `dataTypeIfazeConstantWidening` | ✔ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ konservativ (K1) |
| 54 | Data Types | `dataTypeIfazeMethodParamBoxing` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ korrekt |
| 55 | Data Types | `dataTypeIfazeMethodParamGeneralization` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ korrekt |
| 56 | Data Types | `dataTypeIfazeMethodParamMutation` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ korrekt |
| 57 | Data Types | `dataTypeIfazeMethodParamNarrowing` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ korrekt |
| 58 | Data Types | `dataTypeIfazeMethodParamSpecialization` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ korrekt |
| 59 | Data Types | `dataTypeIfazeMethodParamUnboxing` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ korrekt |
| 60 | Data Types | `dataTypeIfazeMethodParamWidening` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ korrekt |
| 61 | Data Types | `dataTypeIfazeMethodReturnTypeBoxing` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 62 | Data Types | `dataTypeIfazeMethodReturnTypeGeneralization` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 63 | Data Types | `dataTypeIfazeMethodReturnTypeMutation` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 64 | Data Types | `dataTypeIfazeMethodReturnTypeNarrowing` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 65 | Data Types | `dataTypeIfazeMethodReturnTypeSpecialization` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 66 | Data Types | `dataTypeIfazeMethodReturnTypeUnboxing` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 67 | Data Types | `dataTypeIfazeMethodReturnTypeWidening` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ korrekt |
| 68 | Exceptions | `exceptionClazzMethodCatchBlockAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 69 | Exceptions | `exceptionClazzMethodCatchBlockDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 70 | Exceptions | `exceptionClazzMethodFinallyBlockAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 71 | Exceptions | `exceptionClazzMethodFinallyBlockDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 72 | Exceptions | `exceptionClazzMethodThrowCheckedAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION` | ✅ korrekt |
| 73 | Exceptions | `exceptionClazzMethodThrowCheckedDelete` | ✔ | ✘ | ✔ | ✘ | `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ✅ korrekt |
| 74 | Exceptions | `exceptionClazzMethodThrowCheckedGeneralization` | ✔ | ✘ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION`, `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ✅ korrekt |
| 75 | Exceptions | `exceptionClazzMethodThrowCheckedMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ✅ korrekt |
| 76 | Exceptions | `exceptionClazzMethodThrowCheckedSpecialization` | ✔ | ✔ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION`, `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ⚠️ konservativ (K5) |
| 77 | Exceptions | `exceptionClazzMethodThrowCheckedToTryCatch` | ✔ | ✘ | ✔ | ✘ | `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ✅ korrekt |
| 78 | Exceptions | `exceptionClazzMethodThrowUncheckedAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 79 | Exceptions | `exceptionClazzMethodThrowUncheckedDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 80 | Exceptions | `exceptionClazzMethodThrowUncheckedGeneralization` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 81 | Exceptions | `exceptionClazzMethodThrowUncheckedMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION` | ✅ korrekt |
| 82 | Exceptions | `exceptionClazzMethodThrowUncheckedSpecialization` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 83 | Exceptions | `exceptionClazzMethodTryCatchToThrowChecked` | ✘ | ✘ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION` | ℹ️ nicht statisch prüfbar / Oracle (O2) |
| 84 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamAdd` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 85 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamDelete` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 86 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 87 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 88 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsGeneralization` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 89 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 90 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsSpecialization` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 91 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsToUpperBounds` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 92 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 93 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 94 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 95 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 96 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 97 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsToLowerBounds` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 98 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamAdd` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 99 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamDelete` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 100 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 101 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 102 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsGeneralization` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 103 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 104 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsSpecialization` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 105 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsToUpperBounds` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 106 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsAdd` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 107 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 108 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ konservativ (K3) |
| 109 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 110 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 111 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsToLowerBounds` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ korrekt |
| 112 | Generics | `genericsClazzConstructorTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 113 | Generics | `genericsClazzConstructorTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 114 | Generics | `genericsClazzConstructorTypeBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 115 | Generics | `genericsClazzConstructorTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✔ | – | ❌ Lücke (G1) |
| 116 | Generics | `genericsClazzConstructorTypeBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 117 | Generics | `genericsClazzConstructorTypeBoundsDeleteSecond` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 118 | Generics | `genericsClazzConstructorTypeBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 119 | Generics | `genericsClazzConstructorTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 120 | Generics | `genericsClazzConstructorTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 121 | Generics | `genericsClazzConstructorTypeDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 122 | Generics | `genericsClazzConstructorTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 123 | Generics | `genericsClazzConstructorTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 124 | Generics | `genericsClazzMethodTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 125 | Generics | `genericsClazzMethodTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 126 | Generics | `genericsClazzMethodTypeBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 127 | Generics | `genericsClazzMethodTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✔ | – | ❌ Lücke (G1) |
| 128 | Generics | `genericsClazzMethodTypeBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 129 | Generics | `genericsClazzMethodTypeBoundsDeleteSecond` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 130 | Generics | `genericsClazzMethodTypeBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 131 | Generics | `genericsClazzMethodTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 132 | Generics | `genericsClazzMethodTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 133 | Generics | `genericsClazzMethodTypeDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 134 | Generics | `genericsClazzMethodTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 135 | Generics | `genericsClazzMethodTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 136 | Generics | `genericsClazzTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 137 | Generics | `genericsClazzTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 138 | Generics | `genericsClazzTypeBoundsAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 139 | Generics | `genericsClazzTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✔ | – | ❌ Lücke (G1) |
| 140 | Generics | `genericsClazzTypeBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 141 | Generics | `genericsClazzTypeBoundsDeleteSecond` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 142 | Generics | `genericsClazzTypeBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 143 | Generics | `genericsClazzTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 144 | Generics | `genericsClazzTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 145 | Generics | `genericsClazzTypeDelete` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 146 | Generics | `genericsClazzTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 147 | Generics | `genericsClazzTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 148 | Generics | `genericsIfazeMethodTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 149 | Generics | `genericsIfazeMethodTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 150 | Generics | `genericsIfazeMethodTypeBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 151 | Generics | `genericsIfazeMethodTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✔ | – | ❌ Lücke (G1) |
| 152 | Generics | `genericsIfazeMethodTypeBoundsDelete` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 153 | Generics | `genericsIfazeMethodTypeBoundsDeleteSecond` | ✔ | ✘ | ✔ | ✔ | – | ❌ Lücke (G1) |
| 154 | Generics | `genericsIfazeMethodTypeBoundsGeneralization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 155 | Generics | `genericsIfazeMethodTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 156 | Generics | `genericsIfazeMethodTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 157 | Generics | `genericsIfazeMethodTypeDelete` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 158 | Generics | `genericsIfazeMethodTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 159 | Generics | `genericsIfazeMethodTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 160 | Generics | `genericsIfazeTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 161 | Generics | `genericsIfazeTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 162 | Generics | `genericsIfazeTypeBoundsAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 163 | Generics | `genericsIfazeTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✔ | – | ❌ Lücke (G1) |
| 164 | Generics | `genericsIfazeTypeBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 165 | Generics | `genericsIfazeTypeBoundsDeleteSecond` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 166 | Generics | `genericsIfazeTypeBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ konservativ (K3) |
| 167 | Generics | `genericsIfazeTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 168 | Generics | `genericsIfazeTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 169 | Generics | `genericsIfazeTypeDelete` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 170 | Generics | `genericsIfazeTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ korrekt |
| 171 | Generics | `genericsIfazeTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 172 | Inheritance | `inheritanceClazzContractSuperClassSet` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `CONSTRUCTOR_REMOVED`, `METHOD_REMOVED`, `SUPERCLASS_REMOVED` | ✅ korrekt |
| 173 | Inheritance | `inheritanceClazzExpandSuperClassSet` | ✔ | ✔ | ✔ | ✔ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `SUPERCLASS_ADDED` | ✅ korrekt |
| 174 | Inheritance | `inheritanceClazzMethodMovedFromSuperClass` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ korrekt |
| 175 | Inheritance | `inheritanceClazzMethodMovedToSuperClass` | ✔ | ✔ | ✔ | ✔ | `METHOD_ADDED_TO_PUBLIC_CLASS` | ✅ korrekt |
| 176 | Inheritance | `inheritanceClazzMethodOverrideAdd` | ✔ | ✔ | ✔ | ✔ | `METHOD_ADDED_TO_PUBLIC_CLASS` | ✅ korrekt |
| 177 | Inheritance | `inheritanceClazzMethodOverrideDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 178 | Inheritance | `inheritanceClazzStartInherite` | ✔ | ✔ | ✔ | ✔ | `SUPERCLASS_ADDED` | ✅ korrekt |
| 179 | Inheritance | `inheritanceClazzStopInherite` | ✘ | ✘ | ✘ | ✘ | `SUPERCLASS_REMOVED` | ✅ korrekt |
| 180 | Inheritance | `inheritanceIfazeContractSuperinterfaceSet` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `INTERFACE_REMOVED`, `METHOD_REMOVED`, `SUPERCLASS_REMOVED` | ✅ korrekt |
| 181 | Inheritance | `inheritanceIfazeDefaultMethodOverrideAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 182 | Inheritance | `inheritanceIfazeDefaultMethodOverrideDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 183 | Inheritance | `inheritanceIfazeExpandSuperinterfaceSet` | ✔ | ✘ | ✔ | ✔ | `INTERFACE_ADDED` | ❌ Lücke (G2) |
| 184 | Inheritance | `inheritanceIfazeMethodMovedFromSuperInterface` | ✔ | ✔ | ✘ | ✘ | `METHOD_REMOVED` | ⚠️ konservativ (K4) |
| 185 | Inheritance | `inheritanceIfazeMethodMovedToSuperInterface` | ✔ | ✔ | ✔ | ✘ | `METHOD_ADDED_TO_INTERFACE` | ⚠️ konservativ (K4) |
| 186 | Inheritance | `inheritanceIfazeStartInherite` | ✔ | ✘ | ✔ | ✔ | `INTERFACE_ADDED` | ❌ Lücke (G2) |
| 187 | Inheritance | `inheritanceIfazeStopInherite` | ✘ | ✘ | ✘ | ✘ | `INTERFACE_REMOVED` | ✅ korrekt |
| 188 | Members | `membersClazzConstructorAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 189 | Members | `membersClazzConstructorDelete` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ korrekt |
| 190 | Members | `membersClazzConstructorParamAdd` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ korrekt |
| 191 | Members | `membersClazzConstructorParamDelete` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ korrekt |
| 192 | Members | `membersClazzFieldAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 193 | Members | `membersClazzFieldConstantAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 194 | Members | `membersClazzFieldConstantDelete` | ✔ | ✘ | ✘ | ✘ | `FIELD_REMOVED` | ⚠️ konservativ (K1) |
| 195 | Members | `membersClazzFieldDelete` | ✘ | ✘ | ✘ | ✘ | `FIELD_REMOVED` | ✅ korrekt |
| 196 | Members | `membersClazzMethodAbstractAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_ABSTRACT_ADDED_TO_CLASS`, `METHOD_ADDED_TO_PUBLIC_CLASS` | ✅ korrekt |
| 197 | Members | `membersClazzMethodAbstractDelete` | ✔ | ✘ | ✘ | ✘ | `METHOD_REMOVED` | ⚠️ konservativ (K4) |
| 198 | Members | `membersClazzMethodAdd` | ✔ | ✔ | ✔ | ✔ | `METHOD_ADDED_TO_PUBLIC_CLASS` | ✅ korrekt |
| 199 | Members | `membersClazzMethodDelete` | ✘ | ✘ | ✘ | ✘ | `METHOD_REMOVED` | ✅ korrekt |
| 200 | Members | `membersClazzMethodParamAdd` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ korrekt |
| 201 | Members | `membersClazzMethodParamDelete` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ korrekt |
| 202 | Members | `membersClazzNestedClazzAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 203 | Members | `membersClazzNestedClazzDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `CONSTRUCTOR_REMOVED`, `SUPERCLASS_REMOVED` | ✅ korrekt |
| 204 | Members | `membersClazzNestedIfazeAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 205 | Members | `membersClazzNestedIfazeDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `SUPERCLASS_REMOVED` | ✅ korrekt |
| 206 | Members | `membersIfazeConstantAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 207 | Members | `membersIfazeConstantDelete` | ✔ | ✘ | ✘ | ✘ | `FIELD_REMOVED` | ⚠️ konservativ (K1) |
| 208 | Members | `membersIfazeMethodAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_ADDED_TO_INTERFACE` | ✅ korrekt |
| 209 | Members | `membersIfazeMethodDefaultAdd` | ✔ | ✔ | ✔ | ✔ | `METHOD_NEW_DEFAULT` | ✅ korrekt |
| 210 | Members | `membersIfazeMethodDefaultDelete` | ✘ | ✘ | ✘ | ✘ | `METHOD_REMOVED` | ✅ korrekt |
| 211 | Members | `membersIfazeMethodDelete` | ✔ | ✘ | ✘ | ✘ | `METHOD_REMOVED` | ⚠️ konservativ (K4) |
| 212 | Members | `membersIfazeMethodParamAdd` | ✔ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ⚠️ konservativ (K4) |
| 213 | Members | `membersIfazeMethodParamDelete` | ✔ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ⚠️ konservativ (K4) |
| 214 | Members | `membersIfazeNestedIfazeAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 215 | Members | `membersIfazeNestedIfazeDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `SUPERCLASS_REMOVED` | ✅ korrekt |
| 216 | Other Modifiers | `modifierClazzAbstractToNonAbstract` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 217 | Other Modifiers | `modifierClazzEffectivelyFinalToFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 218 | Other Modifiers | `modifierClazzFinalToEffectivelyFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 219 | Other Modifiers | `modifierClazzFinalToNonFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 220 | Other Modifiers | `modifierClazzNonAbstractToAbstract` | ✘ | ✘ | ✘ | ✘ | `CLASS_NOW_ABSTRACT` | ✅ korrekt |
| 221 | Other Modifiers | `modifierClazzNonFinalToFinal` | ✘ | ✘ | ✘ | ✘ | `CLASS_NOW_NOT_EXTENDABLE` | ✅ korrekt |
| 222 | Other Modifiers | `modifierClazzNonStrictfpToStrictfp` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 223 | Other Modifiers | `modifierClazzStrictfpToNonStrictfp` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 224 | Other Modifiers | `modifierFieldFinalToNonFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 225 | Other Modifiers | `modifierFieldNonFinalToFinal` | ✘ | ✘ | ✘ | ✘ | `FIELD_NOW_FINAL` | ✅ korrekt |
| 226 | Other Modifiers | `modifierFieldNonStaticToStatic` | ✘ | ✔ | ✘ | ✘ | `FIELD_NOW_STATIC` | ⚠️ konservativ (K6) |
| 227 | Other Modifiers | `modifierFieldNonTransientToTransient` | ✔ | ✔ | ✔ | ✔ | `FIELD_NOW_TRANSIENT` | ✅ korrekt |
| 228 | Other Modifiers | `modifierFieldNonVolatileToVolatile` | ✔ | ✔ | ✔ | ✔ | `FIELD_NOW_VOLATILE` | ✅ korrekt |
| 229 | Other Modifiers | `modifierFieldStaticToNonStatic` | ✘ | ✘ | ✘ | ✘ | `FIELD_NO_LONGER_STATIC` | ✅ korrekt |
| 230 | Other Modifiers | `modifierFieldTransientToNonTransient` | ✔ | ✔ | ✔ | ✔ | `FIELD_NO_LONGER_TRANSIENT` | ✅ korrekt |
| 231 | Other Modifiers | `modifierFieldVolatileToNonVolatile` | ✔ | ✔ | ✔ | ✔ | `FIELD_NO_LONGER_VOLATILE` | ✅ korrekt |
| 232 | Other Modifiers | `modifierMethodAbstractToNonAbstract` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 233 | Other Modifiers | `modifierMethodFinalToNonFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 234 | Other Modifiers | `modifierMethodNativeToNonNative` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 235 | Other Modifiers | `modifierMethodNonAbstractToAbstract` | ✘ | ✘ | ✘ | ✘ | `METHOD_NOW_ABSTRACT` | ✅ korrekt |
| 236 | Other Modifiers | `modifierMethodNonFinalToFinal` | ✘ | ✘ | ✘ | ✘ | `METHOD_NOW_FINAL` | ✅ korrekt |
| 237 | Other Modifiers | `modifierMethodNonNativeToNative` | ✘ | ✔ | ✔ | ✔ | – | ❌ Lücke (G3) |
| 238 | Other Modifiers | `modifierMethodNonStaticToStatic` | ✘ | ✔ | ✘ | ✘ | `METHOD_NOW_STATIC` | ⚠️ konservativ (K6) |
| 239 | Other Modifiers | `modifierMethodNonStrictfpToStrictfp` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 240 | Other Modifiers | `modifierMethodNonSynchronizedToSynchronized` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 241 | Other Modifiers | `modifierMethodStaticToNonStatic` | ✘ | ✘ | ✘ | ✘ | `METHOD_NO_LONGER_STATIC` | ✅ korrekt |
| 242 | Other Modifiers | `modifierMethodStrictfpToNonStrictfp` | ✘ | ✔ | ✔ | ✔ | – | ℹ️ nicht statisch prüfbar / Oracle (O1) |
| 243 | Other Modifiers | `modifierMethodSynchronizedToNonSynchronized` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 244 | Other Modifiers | `modifierNestedClazzNonStaticToStatic` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ korrekt |
| 245 | Other Modifiers | `modifierNestedClazzStaticToNonStatic` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ korrekt |
| 246 | Miscellaneous | `otherClazzAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 247 | Miscellaneous | `otherClazzDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `CONSTRUCTOR_REMOVED`, `SUPERCLASS_REMOVED` | ✅ korrekt |
| 248 | Miscellaneous | `otherClazzToIfaze` | ✘ | ✘ | ✘ | ✘ | `CLASS_NOW_ABSTRACT`, `CLASS_TYPE_CHANGED`, `CONSTRUCTOR_REMOVED` | ✅ korrekt |
| 249 | Miscellaneous | `otherIfazeAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ korrekt |
| 250 | Miscellaneous | `otherIfazeDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `SUPERCLASS_REMOVED` | ✅ korrekt |
| 251 | Miscellaneous | `otherIfazeToClass` | ✘ | ✘ | ✘ | ✘ | `CLASS_TYPE_CHANGED` | ✅ korrekt |

## 8. Zusätzliche Szenarien ohne Oracle-Eintrag

`lib-v1` und `lib-v2` des Korpus enthalten 10 weitere Exception-Szenarien für Interface-Methoden. Für sie gibt es keinen Client und keinen Eintrag in `compatibility.csv`. Mit ihnen kommt die Kategorie auf die 26 Exception-Szenarien aus Tabelle 1 des Papers. Die japicmp-Ergebnisse dafür sind plausibel: Checked Exceptions werden als source-inkompatibel gemeldet, Unchecked Exceptions werden ignoriert.

| Szenario | japicmp binär | japicmp source | gemeldete japicmp-Änderungen |
|---|:-:|:-:|---|
| `exceptionIfazeMethodThrowCheckedAdd` | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION` |
| `exceptionIfazeMethodThrowCheckedDelete` | ✔ | ✘ | `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` |
| `exceptionIfazeMethodThrowCheckedGeneralization` | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION`, `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` |
| `exceptionIfazeMethodThrowCheckedMutation` | ✔ | ✘ | `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` |
| `exceptionIfazeMethodThrowCheckedSpecialization` | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION`, `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` |
| `exceptionIfazeMethodThrowUncheckedAdd` | ✔ | ✔ | – |
| `exceptionIfazeMethodThrowUncheckedDelete` | ✔ | ✔ | – |
| `exceptionIfazeMethodThrowUncheckedGeneralization` | ✔ | ✔ | – |
| `exceptionIfazeMethodThrowUncheckedMutation` | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION` |
| `exceptionIfazeMethodThrowUncheckedSpecialization` | ✔ | ✔ | – |
