# japicmp and the API evolution data corpus by Jezek & Dietrich

This document evaluates japicmp against the corpus from

> K. Jezek and J. Dietrich, "API evolution and compatibility: A data corpus and tool evaluation,"
> *Journal of Object Technology*, vol. 16, no. 4, pp. 2:1–23, 2017.
> Corpus: <https://github.com/kjezek/api-evolution-data-corpus> (commit `407398e`)

## 1. How the paper systematically builds the test cases

The paper organizes the test cases along three dimensions:

| Dimension | Values |
|---|---|
| **What** changes (category) | Access Modifiers, Data Types, Exceptions, Generics (type parameters and wildcards), Inheritance, Members, Other (non-access) Modifiers, Miscellaneous |
| **Where** the change happens (element) | class, inner (nested) class, interface, method, constructor, field, generic type |
| **How** it changes (change) | add, delete, strengthen, weaken (for data types also boxing/unboxing, widening/narrowing, generalization/specialization; for methods separately for parameters and return type) |

This yields 7 × 8 × 4 = 224 combinations in theory. Combinations that do not compile (e.g. an access modifier on an interface method in Java 7) are dropped. Methods, data types and generics add further cases. The result is **251 scenarios**. Each scenario is a Java package named `<category><element><change>`. It contains a class in `lib-v1`, the changed class in `lib-v2` and a client in `client`.

The **oracle** (`compatibility.csv`) is generated dynamically:

* **source incompatible:** The client that compiles against `lib-v1` no longer compiles against `lib-v2`.
* **binary incompatible:** The client compiled against `lib-v1` fails at runtime with `lib-v2` (linkage error).

Important for the evaluation: the oracle describes what happens to the *specific client* of the scenario. A static tool like japicmp does not know the client. It therefore has to assume that any possible client may be affected. If japicmp reports an incompatibility that the example client does not trigger, this is usually **conservative** and not an error.

## 2. Approach

1. `lib-v1` and `lib-v2` of the corpus are built with `javac --release 8` into `lib-v1.jar` and `lib-v2.jar`.
2. These jars are compared using the japicmp API (`JarArchiveComparator`, default options, i.e. `protected` and above) ([`CorpusEvaluation.java`](CorpusEvaluation.java)).
3. For each scenario (package below `testing_lib`) the following is collected:
   * `isBinaryCompatible()` and `isSourceCompatible()` of all classes in the package,
   * all reported `JApiCompatibilityChangeType`s (class, superclass, interfaces, methods, constructors, parameters, return types, fields, generic templates).
4. The result is compared with the oracle of the corpus ([`compare.py`](compare.py)).

To reproduce (build japicmp first):

```bash
mvn -pl japicmp -am install -DskipTests
doc/api-evolution-corpus/run.sh
```

The evaluated version is japicmp `0.26.6-SNAPSHOT` including the extensions from section 4. Without these extensions, japicmp detected 144 of 154 incompatible scenarios (93.51%), with 9 gaps.

### Legend

| Symbol | Meaning |
|---|---|
| ✅ correct | japicmp's binary and source classification match the oracle. |
| ⚠️ conservative | japicmp reports an incompatibility that the client in the corpus does not trigger, but another client can (see C1–C6). |
| ❌ gap | The oracle reports an incompatibility that japicmp does **not** detect. After extensions G1–G3 there are no such cases left. |
| ℹ️ not statically checkable / oracle | The deviation stems from an error in the oracle or from pure runtime behavior (see O1, O2). |

✔ = compatible, ✘ = incompatible.

## 3. Summary of results

| Category | Scenarios | ✅ correct | ⚠️ conservative | ❌ gap | ℹ️ oracle | Incompatible scenarios | Detected by japicmp | Detection rate today | japicmp 0.7.2 in the paper |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Access Modifiers | 18 | 18 | 0 | 0 | 0 | 6 | 6 | 100.00% | 100.00% |
| Data Types | 49 | 29 | 20 | 0 | 0 | 47 | 47 | 100.00% | 100.00% |
| Exceptions | 16 | 14 | 1 | 0 | 1 | 7 | 7 | 100.00% | 100.00% |
| Generics (Wildcards) | 28 | 17 | 11 | 0 | 0 | 17 | 17 | 100.00% | 0.00% ¹ |
| Generics | 60 | 39 | 21 | 0 | 0 | 34 | 34 | 100.00% | 0.00% ¹ |
| Inheritance | 16 | 14 | 2 | 0 | 0 | 7 | 7 | 100.00% | 100.00% |
| Members | 28 | 22 | 6 | 0 | 0 | 19 | 19 | 100.00% | 100.00% |
| Other Modifiers | 30 | 27 | 2 | 0 | 1 | 13 | 12 | 92.31% | 84.62% |
| Miscellaneous | 6 | 6 | 0 | 0 | 0 | 4 | 4 | 100.00% | 100.00% |
| **Total** | **251** | **186** | **63** | **0** | **2** | **154** | **153** | **99.35%** | **65.58%** |

¹ The paper treats generics as a single category (88 scenarios). Here it is split into type parameters (60) and wildcards (28).

As in the paper (table "Correctly Detected Incompatibilities in Each Scenario"), the "detection rate" is the share of incompatible scenarios that japicmp reports as incompatible. Split as in the table "Correctly Detected Scenarios Divided into Source and Binary Incompatibilities":

| Type | Scenarios | Detected today | Rate today | japicmp 0.7.2 in the paper |
|---|---:|---:|---:|---:|
| source incompatible only | 68 | 68 | 100.00% | 25.00% |
| binary incompatible | 86 | 85 | 98.84% | 97.67% |

Counted more strictly, i.e. separately for each kind: of 132 source-incompatible scenarios, japicmp classifies all 132 as source incompatible (124 before the extensions). Of 86 binary-incompatible scenarios, it classifies 84 as binary incompatible (83 before). The two remaining binary deviations are O1 and O2.

Since version 0.7.2 evaluated in the paper, japicmp has improved mainly for **generics** (0% → 100%) and for **source incompatibilities** (25% → 100%). A note on comparing the numbers: the paper evaluated the text output by string matching and therefore counted every reported change as a hit, even a compatible one such as `INTERFACE_ADDED`. This evaluation only counts what japicmp actually classifies as incompatible. The only scenario left is `modifierMethodStrictfpToNonStrictfp`, and there the oracle is wrong (O1).

## 4. Closed gaps (G1–G3)

The first evaluation revealed three checks that were missing in japicmp. They are now implemented. In the matrix, the affected scenarios are marked G1, G2 or G3.

| ID | Scenarios | Before | Extension |
|---|---|---|---|
| **G1** | `generics*TypeBounds(Add\|Delete)Second` (10 scenarios, 6 of them incompatible according to the oracle) | An additional bound of a type parameter (intersection type), e.g. `<T extends Number>` → `<T extends Number & Comparable<T>>`, was not compared. japicmp reported no change. | `GenericTemplateHelper` now also compares the additional (interface) bounds. If they change, the template is `MODIFIED` and japicmp reports `CLASS_GENERIC_TEMPLATE_CHANGED` (source incompatible). This applies to classes, interfaces, methods and constructors. |
| **G2** | `inheritanceIfazeStartInherite`, `inheritanceIfazeExpandSuperinterfaceSet` | When an **interface** got a new super interface with abstract methods, japicmp only reported the compatible `INTERFACE_ADDED`. Classes implementing the interface no longer compile, however. | For interfaces, `CompatibilityChanges` now checks whether a new super interface brings abstract methods that the interface neither declared nor inherited before. In that case japicmp reports `METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE` (binary compatible, source incompatible), as it already did for classes. Marker interfaces and methods that already exist do not trigger a report. |
| **G3** | `modifierMethodNonNativeToNative` | The `native` modifier was not evaluated. | New modifier `NativeModifier` on `JApiBehavior` (`getNativeModifier()`) and new change type **`METHOD_NOW_NATIVE`**: binary incompatible, source compatible, `MAJOR`. Existing clients still link, but a call fails with `UnsatisfiedLinkError` if no native library is loaded. According to JLS §13.4.25, adding `native` is binary compatible. The classification as incompatible follows the oracle and can be overridden with `overrideCompatibilityChangeParameters`. The reverse case (`native` → Java) remains compatible and is not reported. |

The extensions are covered by unit tests in `GenericTemplateTest`, `InterfaceCompatibilityTest` and `MethodCompatibilityTest`.

G1 adds 4 new conservative reports: `*TypeBoundsDeleteSecond` on classes, constructors and interfaces. Removing a bound relaxes the type parameters. This is compatible for callers, but japicmp treats it like any other template change (see C3).

## 5. Deviations not caused by japicmp (ℹ️)

| ID | Scenario | Explanation |
|---|---|---|
| **O1** | `modifierMethodStrictfpToNonStrictfp` | Error in the oracle: the client of this scenario uses the class `ModifierMethodNonNativeToNative` instead of `ModifierMethodStrictfpToNonStrictfp` (copy & paste). The runtime error is therefore caused by the `native` method. `strictfp` has no effect on compatibility, so japicmp is right here. |
| **O2** | `exceptionClazzMethodTryCatchToThrowChecked` | japicmp correctly reports `METHOD_NOW_THROWS_CHECKED_EXCEPTION` (source incompatible). The "binary" incompatibility in the oracle is a `FileNotFoundException` that the old client no longer catches at runtime. This is behavior, not a linkage error, and static analysis cannot detect it. |

## 6. Conservative reports (⚠️)

In these cases japicmp reports an incompatibility that the specific client in the corpus does not trigger:

| ID | Scenarios | Rationale |
|---|---|---|
| **C1** | `dataTypeIfazeConstant*`, `members*ConstantDelete` | **Compile-time constants** are inlined into the client. Removing them or changing their type therefore causes no linkage error, but the client silently keeps working with the old value (JLS §13.4.9). japicmp reports `FIELD_REMOVED` or `FIELD_TYPE_CHANGED` as binary incompatible. This is intended and sensible. |
| **C2** | `dataType*Boxing/Unboxing/Widening/Narrowing/Generalization/Specialization` | Thanks to boxing and widening, the call in the client still compiles. A client that overrides the method or assigns the value, however, does break. japicmp classifies every type change as source incompatible. A finer analysis would be possible for callers, but would be wrong for subclasses. |
| **C3** | `generics*`, `genericsWildcards*` | japicmp classifies **every** change to type parameters and to generics of parameters as source incompatible (`CLASS_GENERIC_TEMPLATE_CHANGED`, `METHOD_PARAMETER_GENERICS_CHANGED`). There is no variance analysis that distinguishes relaxing (removing or generalizing a bound, widening a wildcard) from tightening. Note: changes to type parameters of *methods* and *constructors* are also reported as `CLASS_GENERIC_TEMPLATE_CHANGED`. |
| **C4** | `inheritanceIfazeMethodMoved*`, `members*Delete`, `membersIfazeMethodParam*`, `membersClazzMethodAbstractDelete` | The client only implements the interface or only calls the method through a different type. For other clients (calling through the interface or the super interface), the reported changes `METHOD_REMOVED` and `METHOD_ADDED_TO_INTERFACE` do apply. |
| **C5** | `exceptionClazzMethodThrowCheckedSpecialization` | For callers, a more specific checked exception is harmless. A subclass that overrides the method and still throws the more general exception no longer compiles, however. |
| **C6** | `modifierFieldNonStaticToStatic`, `modifierMethodNonStaticToStatic` | A static member can still be accessed through an instance, so the client compiles. For overriding subclasses and for access via `super`, the change is still incompatible. |

## 7. Complete matrix of all 251 scenarios

| # | Category | Scenario (`<category><element><change>`) | Oracle binary | Oracle source | japicmp binary | japicmp source | Reported japicmp changes | Rating |
|---:|---|---|:-:|:-:|:-:|:-:|---|---|
| 1 | Access Modifiers | `accessModifierClazzAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `CLASS_LESS_ACCESSIBLE`, `CLASS_NO_LONGER_PUBLIC`, `CONSTRUCTOR_LESS_ACCESSIBLE` | ✅ correct |
| 2 | Access Modifiers | `accessModifierClazzAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 3 | Access Modifiers | `accessModifierClazzConstructorAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_LESS_ACCESSIBLE` | ✅ correct |
| 4 | Access Modifiers | `accessModifierClazzConstructorAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 5 | Access Modifiers | `accessModifierClazzFieldAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `FIELD_LESS_ACCESSIBLE` | ✅ correct |
| 6 | Access Modifiers | `accessModifierClazzFieldAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 7 | Access Modifiers | `accessModifierClazzMethodAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `METHOD_LESS_ACCESSIBLE` | ✅ correct |
| 8 | Access Modifiers | `accessModifierClazzMethodAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 9 | Access Modifiers | `accessModifierClazzNestedClazzAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `CLASS_LESS_ACCESSIBLE`, `CLASS_NOW_NOT_EXTENDABLE`, `CLASS_NO_LONGER_PUBLIC`, `CONSTRUCTOR_LESS_ACCESSIBLE` | ✅ correct |
| 10 | Access Modifiers | `accessModifierClazzNestedClazzAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 11 | Access Modifiers | `accessModifierClazzNestedIfazeAccessDecrease` | ✘ | ✘ | ✘ | ✘ | `CLASS_LESS_ACCESSIBLE`, `CLASS_NO_LONGER_PUBLIC` | ✅ correct |
| 12 | Access Modifiers | `accessModifierClazzNestedIfazeAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 13 | Access Modifiers | `accessModifierIfazeFieldAccessDecrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 14 | Access Modifiers | `accessModifierIfazeFieldAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 15 | Access Modifiers | `accessModifierIfazeMethodAccessDecrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 16 | Access Modifiers | `accessModifierIfazeMethodAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 17 | Access Modifiers | `accessModifierIfazeNestedIfazeAccessDecrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 18 | Access Modifiers | `accessModifierIfazeNestedIfazeAccessIncrease` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 19 | Data Types | `dataTypeClazzConstructorParamBoxing` | ✘ | ✔ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ⚠️ conservative (C2) |
| 20 | Data Types | `dataTypeClazzConstructorParamGeneralization` | ✘ | ✔ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ⚠️ conservative (C2) |
| 21 | Data Types | `dataTypeClazzConstructorParamMutation` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ correct |
| 22 | Data Types | `dataTypeClazzConstructorParamNarrowing` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ correct |
| 23 | Data Types | `dataTypeClazzConstructorParamSpecialization` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ correct |
| 24 | Data Types | `dataTypeClazzConstructorParamUnboxing` | ✘ | ✔ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ⚠️ conservative (C2) |
| 25 | Data Types | `dataTypeClazzConstructorParamWidening` | ✘ | ✔ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ⚠️ conservative (C2) |
| 26 | Data Types | `dataTypeClazzFieldBoxing` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ conservative (C2) |
| 27 | Data Types | `dataTypeClazzFieldGeneralization` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ correct |
| 28 | Data Types | `dataTypeClazzFieldMutation` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ correct |
| 29 | Data Types | `dataTypeClazzFieldNarrowing` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ conservative (C2) |
| 30 | Data Types | `dataTypeClazzFieldSpecialization` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ conservative (C2) |
| 31 | Data Types | `dataTypeClazzFieldUnboxing` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ conservative (C2) |
| 32 | Data Types | `dataTypeClazzFieldWidening` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ correct |
| 33 | Data Types | `dataTypeClazzMethodParamBoxing` | ✘ | ✔ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ⚠️ conservative (C2) |
| 34 | Data Types | `dataTypeClazzMethodParamGeneralization` | ✘ | ✔ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ⚠️ conservative (C2) |
| 35 | Data Types | `dataTypeClazzMethodParamMutation` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ correct |
| 36 | Data Types | `dataTypeClazzMethodParamNarrowing` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ correct |
| 37 | Data Types | `dataTypeClazzMethodParamSpecialization` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ correct |
| 38 | Data Types | `dataTypeClazzMethodParamUnboxing` | ✘ | ✔ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ⚠️ conservative (C2) |
| 39 | Data Types | `dataTypeClazzMethodParamWidening` | ✘ | ✔ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ⚠️ conservative (C2) |
| 40 | Data Types | `dataTypeClazzMethodReturnTypeBoxing` | ✘ | ✔ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ⚠️ conservative (C2) |
| 41 | Data Types | `dataTypeClazzMethodReturnTypeGeneralization` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 42 | Data Types | `dataTypeClazzMethodReturnTypeMutation` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 43 | Data Types | `dataTypeClazzMethodReturnTypeNarrowing` | ✘ | ✔ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ⚠️ conservative (C2) |
| 44 | Data Types | `dataTypeClazzMethodReturnTypeSpecialization` | ✘ | ✔ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ⚠️ conservative (C2) |
| 45 | Data Types | `dataTypeClazzMethodReturnTypeUnboxing` | ✘ | ✔ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ⚠️ conservative (C2) |
| 46 | Data Types | `dataTypeClazzMethodReturnTypeWidening` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 47 | Data Types | `dataTypeIfazeConstantBoxing` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 48 | Data Types | `dataTypeIfazeConstantGeneralization` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ correct |
| 49 | Data Types | `dataTypeIfazeConstantMutation` | ✘ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ✅ correct |
| 50 | Data Types | `dataTypeIfazeConstantNarrowing` | ✔ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ conservative (C1) |
| 51 | Data Types | `dataTypeIfazeConstantSpecialization` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ conservative (C1) |
| 52 | Data Types | `dataTypeIfazeConstantUnboxing` | ✘ | ✔ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ conservative (C1) |
| 53 | Data Types | `dataTypeIfazeConstantWidening` | ✔ | ✘ | ✘ | ✘ | `FIELD_TYPE_CHANGED` | ⚠️ conservative (C1) |
| 54 | Data Types | `dataTypeIfazeMethodParamBoxing` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ correct |
| 55 | Data Types | `dataTypeIfazeMethodParamGeneralization` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ correct |
| 56 | Data Types | `dataTypeIfazeMethodParamMutation` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ correct |
| 57 | Data Types | `dataTypeIfazeMethodParamNarrowing` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ correct |
| 58 | Data Types | `dataTypeIfazeMethodParamSpecialization` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ correct |
| 59 | Data Types | `dataTypeIfazeMethodParamUnboxing` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ correct |
| 60 | Data Types | `dataTypeIfazeMethodParamWidening` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ✅ correct |
| 61 | Data Types | `dataTypeIfazeMethodReturnTypeBoxing` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 62 | Data Types | `dataTypeIfazeMethodReturnTypeGeneralization` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 63 | Data Types | `dataTypeIfazeMethodReturnTypeMutation` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 64 | Data Types | `dataTypeIfazeMethodReturnTypeNarrowing` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 65 | Data Types | `dataTypeIfazeMethodReturnTypeSpecialization` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 66 | Data Types | `dataTypeIfazeMethodReturnTypeUnboxing` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 67 | Data Types | `dataTypeIfazeMethodReturnTypeWidening` | ✘ | ✘ | ✘ | ✘ | `METHOD_RETURN_TYPE_CHANGED` | ✅ correct |
| 68 | Exceptions | `exceptionClazzMethodCatchBlockAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 69 | Exceptions | `exceptionClazzMethodCatchBlockDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 70 | Exceptions | `exceptionClazzMethodFinallyBlockAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 71 | Exceptions | `exceptionClazzMethodFinallyBlockDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 72 | Exceptions | `exceptionClazzMethodThrowCheckedAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION` | ✅ correct |
| 73 | Exceptions | `exceptionClazzMethodThrowCheckedDelete` | ✔ | ✘ | ✔ | ✘ | `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ✅ correct |
| 74 | Exceptions | `exceptionClazzMethodThrowCheckedGeneralization` | ✔ | ✘ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION`, `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ✅ correct |
| 75 | Exceptions | `exceptionClazzMethodThrowCheckedMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ✅ correct |
| 76 | Exceptions | `exceptionClazzMethodThrowCheckedSpecialization` | ✔ | ✔ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION`, `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ⚠️ conservative (C5) |
| 77 | Exceptions | `exceptionClazzMethodThrowCheckedToTryCatch` | ✔ | ✘ | ✔ | ✘ | `METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION` | ✅ correct |
| 78 | Exceptions | `exceptionClazzMethodThrowUncheckedAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 79 | Exceptions | `exceptionClazzMethodThrowUncheckedDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 80 | Exceptions | `exceptionClazzMethodThrowUncheckedGeneralization` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 81 | Exceptions | `exceptionClazzMethodThrowUncheckedMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION` | ✅ correct |
| 82 | Exceptions | `exceptionClazzMethodThrowUncheckedSpecialization` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 83 | Exceptions | `exceptionClazzMethodTryCatchToThrowChecked` | ✘ | ✘ | ✔ | ✘ | `METHOD_NOW_THROWS_CHECKED_EXCEPTION` | ℹ️ not statically checkable / oracle (O2) |
| 84 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamAdd` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 85 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamDelete` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 86 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 87 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 88 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsGeneralization` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 89 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 90 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsSpecialization` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 91 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamLowerBoundsToUpperBounds` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 92 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 93 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 94 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 95 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 96 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 97 | Generics (Wildcards) | `genericsWildcardsClazzConstructorParamUpperBoundsToLowerBounds` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 98 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamAdd` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 99 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamDelete` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 100 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 101 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 102 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsGeneralization` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 103 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 104 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsSpecialization` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 105 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamLowerBoundsToUpperBounds` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 106 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsAdd` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 107 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 108 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ⚠️ conservative (C3) |
| 109 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 110 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 111 | Generics (Wildcards) | `genericsWildcardsClazzMethodParamUpperBoundsToLowerBounds` | ✔ | ✘ | ✔ | ✘ | `METHOD_PARAMETER_GENERICS_CHANGED` | ✅ correct |
| 112 | Generics | `genericsClazzConstructorTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 113 | Generics | `genericsClazzConstructorTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 114 | Generics | `genericsClazzConstructorTypeBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 115 | Generics | `genericsClazzConstructorTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct (G1) |
| 116 | Generics | `genericsClazzConstructorTypeBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 117 | Generics | `genericsClazzConstructorTypeBoundsDeleteSecond` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3, G1) |
| 118 | Generics | `genericsClazzConstructorTypeBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 119 | Generics | `genericsClazzConstructorTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 120 | Generics | `genericsClazzConstructorTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 121 | Generics | `genericsClazzConstructorTypeDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 122 | Generics | `genericsClazzConstructorTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 123 | Generics | `genericsClazzConstructorTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 124 | Generics | `genericsClazzMethodTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 125 | Generics | `genericsClazzMethodTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 126 | Generics | `genericsClazzMethodTypeBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 127 | Generics | `genericsClazzMethodTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct (G1) |
| 128 | Generics | `genericsClazzMethodTypeBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 129 | Generics | `genericsClazzMethodTypeBoundsDeleteSecond` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3, G1) |
| 130 | Generics | `genericsClazzMethodTypeBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 131 | Generics | `genericsClazzMethodTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 132 | Generics | `genericsClazzMethodTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 133 | Generics | `genericsClazzMethodTypeDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 134 | Generics | `genericsClazzMethodTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 135 | Generics | `genericsClazzMethodTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 136 | Generics | `genericsClazzTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 137 | Generics | `genericsClazzTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 138 | Generics | `genericsClazzTypeBoundsAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 139 | Generics | `genericsClazzTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct (G1) |
| 140 | Generics | `genericsClazzTypeBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 141 | Generics | `genericsClazzTypeBoundsDeleteSecond` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3, G1) |
| 142 | Generics | `genericsClazzTypeBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 143 | Generics | `genericsClazzTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 144 | Generics | `genericsClazzTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 145 | Generics | `genericsClazzTypeDelete` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 146 | Generics | `genericsClazzTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 147 | Generics | `genericsClazzTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 148 | Generics | `genericsIfazeMethodTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 149 | Generics | `genericsIfazeMethodTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 150 | Generics | `genericsIfazeMethodTypeBoundsAdd` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 151 | Generics | `genericsIfazeMethodTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct (G1) |
| 152 | Generics | `genericsIfazeMethodTypeBoundsDelete` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 153 | Generics | `genericsIfazeMethodTypeBoundsDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct (G1) |
| 154 | Generics | `genericsIfazeMethodTypeBoundsGeneralization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 155 | Generics | `genericsIfazeMethodTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 156 | Generics | `genericsIfazeMethodTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 157 | Generics | `genericsIfazeMethodTypeDelete` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 158 | Generics | `genericsIfazeMethodTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 159 | Generics | `genericsIfazeMethodTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 160 | Generics | `genericsIfazeTypeAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 161 | Generics | `genericsIfazeTypeAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 162 | Generics | `genericsIfazeTypeBoundsAdd` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 163 | Generics | `genericsIfazeTypeBoundsAddSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct (G1) |
| 164 | Generics | `genericsIfazeTypeBoundsDelete` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 165 | Generics | `genericsIfazeTypeBoundsDeleteSecond` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3, G1) |
| 166 | Generics | `genericsIfazeTypeBoundsGeneralization` | ✔ | ✔ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ⚠️ conservative (C3) |
| 167 | Generics | `genericsIfazeTypeBoundsMutation` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 168 | Generics | `genericsIfazeTypeBoundsSpecialization` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 169 | Generics | `genericsIfazeTypeDelete` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 170 | Generics | `genericsIfazeTypeDeleteSecond` | ✔ | ✘ | ✔ | ✘ | `CLASS_GENERIC_TEMPLATE_CHANGED` | ✅ correct |
| 171 | Generics | `genericsIfazeTypeSwap` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 172 | Inheritance | `inheritanceClazzContractSuperClassSet` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `CONSTRUCTOR_REMOVED`, `METHOD_REMOVED`, `SUPERCLASS_REMOVED` | ✅ correct |
| 173 | Inheritance | `inheritanceClazzExpandSuperClassSet` | ✔ | ✔ | ✔ | ✔ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `SUPERCLASS_ADDED` | ✅ correct |
| 174 | Inheritance | `inheritanceClazzMethodMovedFromSuperClass` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ correct |
| 175 | Inheritance | `inheritanceClazzMethodMovedToSuperClass` | ✔ | ✔ | ✔ | ✔ | `METHOD_ADDED_TO_PUBLIC_CLASS` | ✅ correct |
| 176 | Inheritance | `inheritanceClazzMethodOverrideAdd` | ✔ | ✔ | ✔ | ✔ | `METHOD_ADDED_TO_PUBLIC_CLASS` | ✅ correct |
| 177 | Inheritance | `inheritanceClazzMethodOverrideDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 178 | Inheritance | `inheritanceClazzStartInherite` | ✔ | ✔ | ✔ | ✔ | `SUPERCLASS_ADDED` | ✅ correct |
| 179 | Inheritance | `inheritanceClazzStopInherite` | ✘ | ✘ | ✘ | ✘ | `SUPERCLASS_REMOVED` | ✅ correct |
| 180 | Inheritance | `inheritanceIfazeContractSuperinterfaceSet` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `INTERFACE_REMOVED`, `METHOD_REMOVED`, `SUPERCLASS_REMOVED` | ✅ correct |
| 181 | Inheritance | `inheritanceIfazeDefaultMethodOverrideAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 182 | Inheritance | `inheritanceIfazeDefaultMethodOverrideDelete` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 183 | Inheritance | `inheritanceIfazeExpandSuperinterfaceSet` | ✔ | ✘ | ✔ | ✘ | `INTERFACE_ADDED`, `METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE` | ✅ correct (G2) |
| 184 | Inheritance | `inheritanceIfazeMethodMovedFromSuperInterface` | ✔ | ✔ | ✘ | ✘ | `METHOD_REMOVED` | ⚠️ conservative (C4) |
| 185 | Inheritance | `inheritanceIfazeMethodMovedToSuperInterface` | ✔ | ✔ | ✔ | ✘ | `METHOD_ADDED_TO_INTERFACE` | ⚠️ conservative (C4) |
| 186 | Inheritance | `inheritanceIfazeStartInherite` | ✔ | ✘ | ✔ | ✘ | `INTERFACE_ADDED`, `METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE` | ✅ correct (G2) |
| 187 | Inheritance | `inheritanceIfazeStopInherite` | ✘ | ✘ | ✘ | ✘ | `INTERFACE_REMOVED` | ✅ correct |
| 188 | Members | `membersClazzConstructorAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 189 | Members | `membersClazzConstructorDelete` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ correct |
| 190 | Members | `membersClazzConstructorParamAdd` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ correct |
| 191 | Members | `membersClazzConstructorParamDelete` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ correct |
| 192 | Members | `membersClazzFieldAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 193 | Members | `membersClazzFieldConstantAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 194 | Members | `membersClazzFieldConstantDelete` | ✔ | ✘ | ✘ | ✘ | `FIELD_REMOVED` | ⚠️ conservative (C1) |
| 195 | Members | `membersClazzFieldDelete` | ✘ | ✘ | ✘ | ✘ | `FIELD_REMOVED` | ✅ correct |
| 196 | Members | `membersClazzMethodAbstractAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_ABSTRACT_ADDED_TO_CLASS`, `METHOD_ADDED_TO_PUBLIC_CLASS` | ✅ correct |
| 197 | Members | `membersClazzMethodAbstractDelete` | ✔ | ✘ | ✘ | ✘ | `METHOD_REMOVED` | ⚠️ conservative (C4) |
| 198 | Members | `membersClazzMethodAdd` | ✔ | ✔ | ✔ | ✔ | `METHOD_ADDED_TO_PUBLIC_CLASS` | ✅ correct |
| 199 | Members | `membersClazzMethodDelete` | ✘ | ✘ | ✘ | ✘ | `METHOD_REMOVED` | ✅ correct |
| 200 | Members | `membersClazzMethodParamAdd` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ correct |
| 201 | Members | `membersClazzMethodParamDelete` | ✘ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_PUBLIC_CLASS`, `METHOD_REMOVED` | ✅ correct |
| 202 | Members | `membersClazzNestedClazzAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 203 | Members | `membersClazzNestedClazzDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `CONSTRUCTOR_REMOVED`, `SUPERCLASS_REMOVED` | ✅ correct |
| 204 | Members | `membersClazzNestedIfazeAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 205 | Members | `membersClazzNestedIfazeDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `SUPERCLASS_REMOVED` | ✅ correct |
| 206 | Members | `membersIfazeConstantAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 207 | Members | `membersIfazeConstantDelete` | ✔ | ✘ | ✘ | ✘ | `FIELD_REMOVED` | ⚠️ conservative (C1) |
| 208 | Members | `membersIfazeMethodAdd` | ✔ | ✘ | ✔ | ✘ | `METHOD_ADDED_TO_INTERFACE` | ✅ correct |
| 209 | Members | `membersIfazeMethodDefaultAdd` | ✔ | ✔ | ✔ | ✔ | `METHOD_NEW_DEFAULT` | ✅ correct |
| 210 | Members | `membersIfazeMethodDefaultDelete` | ✘ | ✘ | ✘ | ✘ | `METHOD_REMOVED` | ✅ correct |
| 211 | Members | `membersIfazeMethodDelete` | ✔ | ✘ | ✘ | ✘ | `METHOD_REMOVED` | ⚠️ conservative (C4) |
| 212 | Members | `membersIfazeMethodParamAdd` | ✔ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ⚠️ conservative (C4) |
| 213 | Members | `membersIfazeMethodParamDelete` | ✔ | ✘ | ✘ | ✘ | `METHOD_ADDED_TO_INTERFACE`, `METHOD_REMOVED` | ⚠️ conservative (C4) |
| 214 | Members | `membersIfazeNestedIfazeAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 215 | Members | `membersIfazeNestedIfazeDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `SUPERCLASS_REMOVED` | ✅ correct |
| 216 | Other Modifiers | `modifierClazzAbstractToNonAbstract` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 217 | Other Modifiers | `modifierClazzEffectivelyFinalToFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 218 | Other Modifiers | `modifierClazzFinalToEffectivelyFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 219 | Other Modifiers | `modifierClazzFinalToNonFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 220 | Other Modifiers | `modifierClazzNonAbstractToAbstract` | ✘ | ✘ | ✘ | ✘ | `CLASS_NOW_ABSTRACT` | ✅ correct |
| 221 | Other Modifiers | `modifierClazzNonFinalToFinal` | ✘ | ✘ | ✘ | ✘ | `CLASS_NOW_NOT_EXTENDABLE` | ✅ correct |
| 222 | Other Modifiers | `modifierClazzNonStrictfpToStrictfp` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 223 | Other Modifiers | `modifierClazzStrictfpToNonStrictfp` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 224 | Other Modifiers | `modifierFieldFinalToNonFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 225 | Other Modifiers | `modifierFieldNonFinalToFinal` | ✘ | ✘ | ✘ | ✘ | `FIELD_NOW_FINAL` | ✅ correct |
| 226 | Other Modifiers | `modifierFieldNonStaticToStatic` | ✘ | ✔ | ✘ | ✘ | `FIELD_NOW_STATIC` | ⚠️ conservative (C6) |
| 227 | Other Modifiers | `modifierFieldNonTransientToTransient` | ✔ | ✔ | ✔ | ✔ | `FIELD_NOW_TRANSIENT` | ✅ correct |
| 228 | Other Modifiers | `modifierFieldNonVolatileToVolatile` | ✔ | ✔ | ✔ | ✔ | `FIELD_NOW_VOLATILE` | ✅ correct |
| 229 | Other Modifiers | `modifierFieldStaticToNonStatic` | ✘ | ✘ | ✘ | ✘ | `FIELD_NO_LONGER_STATIC` | ✅ correct |
| 230 | Other Modifiers | `modifierFieldTransientToNonTransient` | ✔ | ✔ | ✔ | ✔ | `FIELD_NO_LONGER_TRANSIENT` | ✅ correct |
| 231 | Other Modifiers | `modifierFieldVolatileToNonVolatile` | ✔ | ✔ | ✔ | ✔ | `FIELD_NO_LONGER_VOLATILE` | ✅ correct |
| 232 | Other Modifiers | `modifierMethodAbstractToNonAbstract` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 233 | Other Modifiers | `modifierMethodFinalToNonFinal` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 234 | Other Modifiers | `modifierMethodNativeToNonNative` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 235 | Other Modifiers | `modifierMethodNonAbstractToAbstract` | ✘ | ✘ | ✘ | ✘ | `METHOD_NOW_ABSTRACT` | ✅ correct |
| 236 | Other Modifiers | `modifierMethodNonFinalToFinal` | ✘ | ✘ | ✘ | ✘ | `METHOD_NOW_FINAL` | ✅ correct |
| 237 | Other Modifiers | `modifierMethodNonNativeToNative` | ✘ | ✔ | ✘ | ✔ | `METHOD_NOW_NATIVE` | ✅ correct (G3) |
| 238 | Other Modifiers | `modifierMethodNonStaticToStatic` | ✘ | ✔ | ✘ | ✘ | `METHOD_NOW_STATIC` | ⚠️ conservative (C6) |
| 239 | Other Modifiers | `modifierMethodNonStrictfpToStrictfp` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 240 | Other Modifiers | `modifierMethodNonSynchronizedToSynchronized` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 241 | Other Modifiers | `modifierMethodStaticToNonStatic` | ✘ | ✘ | ✘ | ✘ | `METHOD_NO_LONGER_STATIC` | ✅ correct |
| 242 | Other Modifiers | `modifierMethodStrictfpToNonStrictfp` | ✘ | ✔ | ✔ | ✔ | – | ℹ️ not statically checkable / oracle (O1) |
| 243 | Other Modifiers | `modifierMethodSynchronizedToNonSynchronized` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 244 | Other Modifiers | `modifierNestedClazzNonStaticToStatic` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ correct |
| 245 | Other Modifiers | `modifierNestedClazzStaticToNonStatic` | ✘ | ✘ | ✘ | ✘ | `CONSTRUCTOR_REMOVED` | ✅ correct |
| 246 | Miscellaneous | `otherClazzAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 247 | Miscellaneous | `otherClazzDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `CONSTRUCTOR_REMOVED`, `SUPERCLASS_REMOVED` | ✅ correct |
| 248 | Miscellaneous | `otherClazzToIfaze` | ✘ | ✘ | ✘ | ✘ | `CLASS_NOW_ABSTRACT`, `CLASS_TYPE_CHANGED`, `CONSTRUCTOR_REMOVED` | ✅ correct |
| 249 | Miscellaneous | `otherIfazeAdd` | ✔ | ✔ | ✔ | ✔ | – | ✅ correct |
| 250 | Miscellaneous | `otherIfazeDelete` | ✘ | ✘ | ✘ | ✘ | `CLASS_REMOVED`, `SUPERCLASS_REMOVED` | ✅ correct |
| 251 | Miscellaneous | `otherIfazeToClass` | ✘ | ✘ | ✘ | ✘ | `CLASS_TYPE_CHANGED` | ✅ correct |

## 8. Additional scenarios without an oracle entry

`lib-v1` and `lib-v2` of the corpus contain 10 further exception scenarios for interface methods. They have no client and no entry in `compatibility.csv`. With them, the category reaches the 26 exception scenarios from Table 1 of the paper. japicmp's results for them are plausible: checked exceptions are reported as source incompatible, unchecked exceptions are ignored.

| Scenario | japicmp binary | japicmp source | Reported japicmp changes |
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
