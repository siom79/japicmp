
# Compatibility Report

![semver MAJOR](https://img.shields.io/badge/semver-MAJOR-red?logo=semver "semver MAJOR")

## Summary

> [!CAUTION]
>
> Incompatible changes found while checking backward compatibility of version `19.0` with the previous version `18.0`.

<details markdown="1">
<summary>Expand to see options used.</summary>

- **Report only summary**: No
- **Report only changes**: Yes
- **Report only binary-incompatible changes**: No
- **Access modifier filter**: `PROTECTED`
- **Old archives**:
  - ![guava 18.0](https://img.shields.io/badge/guava-18.0-blue "guava 18.0")
- **New archives**:
  - ![guava 19.0](https://img.shields.io/badge/guava-19.0-blue "guava 19.0")
- **Evaluate annotations**: Yes
- **Include synthetic classes and class members**: No
- **Include specific elements**: No
- **Exclude specific elements**: No
- **Ignore all missing classes**: No
- **Ignore specific missing classes**: No
- **Treat changes as errors**:
  - Any changes: No
  - Binary incompatible changes: No
  - Source incompatible changes: No
  - Incompatible changes caused by excluded classes: Yes
  - Semantically incompatible changes: No
  - Semantically incompatible changes, including development versions: No
- **Classpath mode**: `ONE_COMMON_CLASSPATH`
- **Old classpath**:
```

```
- **New classpath**:
```

```

</details>


## Results

| Status                     | Type                                                      | Serialization                       | Compatibility Changes |
|----------------------------|-----------------------------------------------------------|-------------------------------------|-----------------------|
| Unchanged                  | [com.google.common.base.Ascii]                            | ![Not serializable]                 | ![Annotation added] ![Annotation removed] |
| Unchanged                  | [com.google.common.base.CaseFormat]                       | ![Compatible]                       | ![Annotation added]   |
| Modified                   | [com.google.common.base.CharMatcher]                      | ![Not serializable]                 | ![Method added to public class] |
| Unchanged                  | [com.google.common.base.Defaults]                         | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Enums]                            | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Equivalence]                      | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Functions]                        | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Joiner]                           | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Joiner$MapJoiner]                 | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.MoreObjects]                      | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.MoreObjects$ToStringHelper]       | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Objects]                          | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Optional]                         | ![Compatible]                       | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Predicates]                       | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Splitter]                         | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Splitter$MapSplitter]             | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.StandardSystemProperty]           | ![Compatible]                       | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Stopwatch]                        | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Strings]                          | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Suppliers]                        | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.base.Throwables]                       | ![Not serializable]                 | ![Annotation added] ![Method added to public class] |
| Unchanged                  | [com.google.common.base.Ticker]                           | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.base.Utf8]                             | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.base.VerifyException]                  | ![Default serialversionuid changed] | ![No changes]         |
| Modified                   | [com.google.common.cache.CacheLoader$UnsupportedLoadingOperationException] | ![Default serialversionuid changed] | ![No changes] |
| Modified                   | [com.google.common.cache.RemovalNotification]             | ![Not serializable]                 | ![Method added to public class] |
| Unchanged                  | [com.google.common.collect.BiMap]                         | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.collect.Collections2]                  | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.collect.ComparisonChain]               | ![Not serializable]                 | ![Annotation added] ![Annotation deprecated added] ![Method added to public class] |
| Modified                   | [com.google.common.collect.FluentIterable]                | ![Not serializable]                 | ![Annotation added] ![Method added to public class] |
| Modified                   | [com.google.common.collect.HashBiMap]                     | ![Superclass modified]              | ![Superclass added] ![Method return type generics changed] |
| Modified                   | [com.google.common.collect.ImmutableBiMap]                | ![Default serialversionuid changed] | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableBiMap$Builder]        | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableClassToInstanceMap]   | ![Default serialversionuid changed] | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableCollection]           | ![Default serialversionuid changed] | ![Method now abstract] |
| Serialization-incompatible | [com.google.common.collect.ImmutableList]                 | ![Default serialversionuid changed] | ![No changes]         |
| Modified                   | [com.google.common.collect.ImmutableListMultimap]         | ![Compatible]                       | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableListMultimap$Builder] | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableMap]                  | ![Default serialversionuid changed] | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableMap$Builder]          | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableMultimap]             | ![Compatible]                       | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableMultimap$Builder]     | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableMultiset]             | ![Default serialversionuid changed] | ![No changes]         |
| Modified                   | [com.google.common.collect.ImmutableRangeMap]             | ![Compatible]                       | ![Interface added] ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableRangeSet]             | ![Default serialversionuid changed] | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableSetMultimap]          | ![Compatible]                       | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableSetMultimap$Builder]  | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableSortedMap]            | ![Compatible]                       | ![Class now final] ![Method added to public class] |
| Modified                   | [com.google.common.collect.ImmutableSortedMap$Builder]    | ![Not serializable]                 | ![Annotation deprecated added] ![Method added to public class] |
| Source-incompatible        | [com.google.common.collect.ImmutableSortedMultiset]       | ![Compatible]                       | ![Class generic template generics changed] |
| Serialization-incompatible | [com.google.common.collect.ImmutableSortedSet]            | ![Default serialversionuid changed] | ![No changes]         |
| Unchanged                  | [com.google.common.collect.Iterables]                     | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.collect.Iterators]                     | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.collect.Lists]                         | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.collect.MapConstraint]                 | ![Not serializable]                 | ![Annotation deprecated added] |
| Unchanged                  | [com.google.common.collect.MapConstraints]                | ![Not serializable]                 | ![Annotation deprecated added] |
| Modified                   | [com.google.common.collect.MapMaker]                      | ![Not serializable]                 | ![Method less accessible] |
| Modified                   | [com.google.common.collect.Maps]                          | ![Not serializable]                 | ![Annotation added] ![Method added to public class] |
| Unchanged                  | [com.google.common.collect.MultimapBuilder]               | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.collect.Multimaps]                     | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.collect.Multisets]                     | ![Not serializable]                 | ![Annotation added] ![Method added to public class] |
| Modified                   | [com.google.common.collect.RangeMap]                      | ![Not serializable]                 | ![Method added to interface] |
| Modified                   | [com.google.common.collect.RangeSet]                      | ![Not serializable]                 | ![Method added to interface] |
| Unchanged                  | [com.google.common.collect.Sets]                          | ![Not serializable]                 | ![Annotation added] ![Annotation deprecated added] |
| Unchanged                  | [com.google.common.collect.Table]                         | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.collect.Table$Cell]                    | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.collect.TreeRangeMap]                  | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.collect.TreeRangeSet]                  | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.eventbus.AsyncEventBus]                | ![Not serializable]                 | ![Method removed]     |
| Modified                   | [com.google.common.eventbus.DeadEvent]                    | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.eventbus.EventBus]                     | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.hash.BloomFilter]                      | ![Default serialversionuid changed] | ![Annotation added] ![Method added to public class] |
| Unchanged                  | [com.google.common.hash.Funnels]                          | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.hash.HashCode]                         | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.hash.Hasher]                           | ![Not serializable]                 | ![Annotation added] ![Annotation deprecated added] ![Method added to interface] |
| Modified                   | [com.google.common.hash.Hashing]                          | ![Not serializable]                 | ![Annotation added] ![Method added to public class] |
| Unchanged                  | [com.google.common.hash.HashingInputStream]               | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.hash.HashingOutputStream]              | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.io.BaseEncoding]                       | ![Not serializable]                 | ![Method now abstract] |
| Modified                   | [com.google.common.io.ByteSource]                         | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.io.CharSource]                         | ![Not serializable]                 | ![Method added to public class] |
| Modified                   | [com.google.common.net.HttpHeaders]                       | ![Not serializable]                 | ![No changes]         |
| Modified                   | [com.google.common.net.MediaType]                         | ![Not serializable]                 | ![No changes]         |
| Unchanged                  | [com.google.common.primitives.Booleans]                   | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.Bytes]                      | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.Chars]                      | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.Doubles]                    | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.Floats]                     | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.primitives.Ints]                       | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.primitives.Longs]                      | ![Not serializable]                 | ![Annotation added] ![Method added to public class] |
| Unchanged                  | [com.google.common.primitives.Primitives]                 | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.Shorts]                     | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.SignedBytes]                | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.UnsignedBytes]              | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.UnsignedInteger]            | ![Compatible]                       | ![Annotation added] ![Annotation removed] |
| Unchanged                  | [com.google.common.primitives.UnsignedInts]               | ![Not serializable]                 | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.UnsignedLong]               | ![Compatible]                       | ![Annotation added]   |
| Unchanged                  | [com.google.common.primitives.UnsignedLongs]              | ![Not serializable]                 | ![Annotation added]   |
| Modified                   | [com.google.common.reflect.ClassPath$ResourceInfo]        | ![Not serializable]                 | ![No changes]         |
| Modified                   | [com.google.common.reflect.TypeToken]                     | ![Default serialversionuid changed] | ![Annotation deprecated added] ![Method added to public class] |
| Modified                   | [com.google.common.util.concurrent.AbstractFuture]        | ![Not serializable]                 | ![No changes]         |
| Modified                   | [com.google.common.util.concurrent.AbstractListeningExecutorService] | ![Not serializable]      | ![Method return type changed] |
| Unchanged                  | [com.google.common.util.concurrent.FutureFallback]        | ![Not serializable]                 | ![Annotation deprecated added] |
| Modified                   | [com.google.common.util.concurrent.Futures]               | ![Not serializable]                 | ![Annotation added] ![Annotation deprecated added] ![Superclass added] ![Method added to public class] |
| Modified                   | [com.google.common.util.concurrent.SettableFuture]        | ![Not serializable]                 | ![Superclass added] ![Method added to public class] |

<details markdown="1">
<summary>Expand for details.</summary>

___

<a id="user-content-com.google.common.base.ascii"></a>
### `com.google.common.base.Ascii`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name    | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|---------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Ascii` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type       | Method                                          | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|----------|------------|-------------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | [`String`] | `truncate`([`CharSequence`], `int`, [`String`]) | ~~[`CheckReturnValue`]~~ |        | ![Annotation removed] |

___

<a id="user-content-com.google.common.base.caseformat"></a>
### `com.google.common.base.CaseFormat`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type | Name         | Extends     | JDK   | Serialization | Compatibility Changes |
|-----------|---------------------|------|--------------|-------------|-------|---------------|-----------------------|
| Unchanged | `public` `abstract` | Enum | `CaseFormat` | [`Enum<E>`] | JDK 6 | ![Compatible] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.charmatcher"></a>
### `com.google.common.base.CharMatcher`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name          | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-------|---------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Class | `CharMatcher` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics | Type                | Method                     | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|----------|---------------------|----------------------------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`any`**()                |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`ascii`**()              |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`breakingWhitespace`**() |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`digit`**()              |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`invisible`**()          |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`javaDigit`**()          |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`javaIsoControl`**()     |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`javaLetter`**()         |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`javaLetterOrDigit`**()  |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`javaLowerCase`**()      |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`javaUpperCase`**()      |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`none`**()               |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`singleWidth`**()        |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`CharMatcher`]** | **`whitespace`**()         |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.base.defaults"></a>
### `com.google.common.base.Defaults`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name       | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Defaults` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.enums"></a>
### `com.google.common.base.Enums`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name    | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|---------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Enums` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.equivalence"></a>
### `com.google.common.base.Equivalence`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type  | Name          | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------|-------|---------------|------------|-------|---------------------|-----------------------|
| Unchanged | `public` `abstract` | Class | `Equivalence` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `T`  | [`Object`] | ![No changes]         |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.functions"></a>
### `com.google.common.base.Functions`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Functions` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.joiner"></a>
### `com.google.common.base.Joiner`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers | Type  | Name     | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|-----------|-------|----------|------------|-------|---------------------|-----------------------|
| Unchanged | `public`  | Class | `Joiner` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type       | Method                                        | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|----------|------------|-----------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `final` `public`  |          | [`String`] | `join`([`Iterable<?>`])                       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`  |          | [`String`] | `join`([`Iterator<?>`])                       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`  |          | [`String`] | `join`([`Object[]`][1])                       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`  |          | [`String`] | `join`([`Object`], [`Object`], [`Object...`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Joiner`] | `on`([`String`])                              | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Joiner`] | `on`(`char`)                                  | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.joiner$mapjoiner"></a>
### `com.google.common.base.Joiner$MapJoiner`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers                 | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------------|-------|-------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `static` `public` | Class | `MapJoiner` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers | Generics | Type       | Method                                                          | Annotations              | Throws | Compatibility Changes |
|-----------|-----------|----------|------------|-----------------------------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `public`  |          | [`String`] | `join`([`Map<?, ?>`])                                           | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`  |          | [`String`] | `join`([`Iterable<? extends Entry<? extends ?, ? extends ?>>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`  |          | [`String`] | `join`([`Iterator<? extends Entry<? extends ?, ? extends ?>>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.moreobjects"></a>
### `com.google.common.base.MoreObjects`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name          | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|---------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `MoreObjects` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics                 | Type               | Method                         | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|--------------------------|--------------------|--------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` | \<[`T extends Object`]\> | [`Object`]         | `firstNonNull`(`T`, `T`)       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |                          | [`ToStringHelper`] | `toStringHelper`([`Object`])   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |                          | [`ToStringHelper`] | `toStringHelper`([`Class<?>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |                          | [`ToStringHelper`] | `toStringHelper`([`String`])   | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.moreobjects$tostringhelper"></a>
### `com.google.common.base.MoreObjects$ToStringHelper`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers                 | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------------|-------|------------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `static` `public` | Class | `ToStringHelper` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers | Generics | Type       | Method       | Annotations              | Throws | Compatibility Changes |
|-----------|-----------|----------|------------|--------------|--------------------------|--------|-----------------------|
| Unchanged | `public`  |          | [`String`] | `toString`() | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.objects"></a>
### `com.google.common.base.Objects`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-----------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Objects` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics                 | Type                  | Method                         | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|--------------------------|-----------------------|--------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` | \<[`T extends Object`]\> | [`Object`]            | `firstNonNull`(`T`, `T`)       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |                          | `int`                 | `hashCode`([`Object...`])      | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |                          | [`ToStringHelper`][2] | `toStringHelper`([`Object`])   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |                          | [`ToStringHelper`][2] | `toStringHelper`([`Class<?>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |                          | [`ToStringHelper`][2] | `toStringHelper`([`String`])   | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.optional"></a>
### `com.google.common.base.Optional`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type  | Name       | Extends    | JDK   | Serialization | Compatibility Changes |
|-----------|---------------------|-------|------------|------------|-------|---------------|-----------------------|
| Unchanged | `public` `abstract` | Class | `Optional` | [`Object`] | JDK 6 | ![Compatible] | ![Annotation added]   |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `T`  | [`Object`] | ![No changes]         |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.predicates"></a>
### `com.google.common.base.Predicates`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name         | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|--------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Predicates` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.splitter"></a>
### `com.google.common.base.Splitter`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name       | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Splitter` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type                 | Method                          | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|----------|----------------------|---------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | [`Splitter`]         | `fixedLength`(`int`)            | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Splitter`]         | `on`(`char`)                    | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Splitter`]         | `on`([`CharMatcher`])           | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Splitter`]         | `on`([`String`])                | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Splitter`]         | `on`([`Pattern`])               | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Splitter`]         | `onPattern`([`String`])         | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`          |          | [`Iterable<String>`] | `split`([`CharSequence`])       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`          |          | [`List<String>`]     | `splitToList`([`CharSequence`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.splitter$mapsplitter"></a>
### `com.google.common.base.Splitter$MapSplitter`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers                 | Type  | Name          | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------------|-------|---------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `static` `public` | Class | `MapSplitter` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers | Generics | Type                    | Method                    | Annotations              | Throws | Compatibility Changes |
|-----------|-----------|----------|-------------------------|---------------------------|--------------------------|--------|-----------------------|
| Unchanged | `public`  |          | [`Map<String, String>`] | `split`([`CharSequence`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.standardsystemproperty"></a>
### `com.google.common.base.StandardSystemProperty`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type | Name                     | Extends     | JDK   | Serialization | Compatibility Changes |
|-----------|------------------|------|--------------------------|-------------|-------|---------------|-----------------------|
| Unchanged | `final` `public` | Enum | `StandardSystemProperty` | [`Enum<E>`] | JDK 6 | ![Compatible] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.stopwatch"></a>
### `com.google.common.base.Stopwatch`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Stopwatch` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type          | Method                        | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|----------|---------------|-------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | [`Stopwatch`] | `createStarted`()             | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Stopwatch`] | `createStarted`([`Ticker`])   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Stopwatch`] | `createUnstarted`()           | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Stopwatch`] | `createUnstarted`([`Ticker`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`          |          | `long`        | `elapsed`([`TimeUnit`])       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`          |          | `boolean`     | `isRunning`()                 | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.strings"></a>
### `com.google.common.base.Strings`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-----------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Strings` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.suppliers"></a>
### `com.google.common.base.Suppliers`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Suppliers` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.base.throwables"></a>
### `com.google.common.base.Throwables`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name         | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|--------------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `Throwables` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers                 | Generics | Type                            | Method                                 | Annotations              | Throws | Compatibility Changes |
|-----------|---------------------------|----------|---------------------------------|----------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public`         |          | [`List<Throwable>`]             | `getCausalChain`([`Throwable`])        | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`         |          | [`Throwable`]                   | `getRootCause`([`Throwable`])          | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`         |          | [`String`]                      | `getStackTraceAsString`([`Throwable`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Added     | **`static`** **`public`** |          | **[`List<StackTraceElement>`]** | **`lazyStackTrace`**([`Throwable`])    | **[`CheckReturnValue`]** |        | ![Method added to public class] ![Annotation added] |
| Added     | **`static`** **`public`** |          | **`boolean`**                   | **`lazyStackTraceIsLazy`**()           | **[`CheckReturnValue`]** |        | ![Method added to public class] ![Annotation added] |

___

<a id="user-content-com.google.common.base.ticker"></a>
### `com.google.common.base.Ticker`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type  | Name     | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------|-------|----------|------------|-------|---------------------|-----------------------|
| Unchanged | `public` `abstract` | Class | `Ticker` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type       | Method           | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|----------|------------|------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | [`Ticker`] | `systemTicker`() | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.utf8"></a>
### `com.google.common.base.Utf8`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name   | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|--------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Utf8` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type      | Method                                 | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|----------|-----------|----------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | `int`     | `encodedLength`([`CharSequence`])      | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `boolean` | `isWellFormed`(`byte[]`)               | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `boolean` | `isWellFormed`(`byte[]`, `int`, `int`) | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.base.verifyexception"></a>
### `com.google.common.base.VerifyException`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers | Type  | Name              | Extends              | JDK   | Serialization                       | Compatibility Changes |
|----------|-----------|-------|-------------------|----------------------|-------|-------------------------------------|-----------------------|
| Modified | `public`  | Class | `VerifyException` | [`RuntimeException`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Constructors

| Status | Modifiers    | Generics | Constructor                                      | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|--------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`VerifyException`**([`Throwable`])             |             |        | ![No changes]         |
| Added  | **`public`** |          | **`VerifyException`**([`String`], [`Throwable`]) |             |        | ![No changes]         |

___

<a id="user-content-com.google.common.cache.cacheloader$unsupportedloadingoperationexception"></a>
### `com.google.common.cache.CacheLoader$UnsupportedLoadingOperationException`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers                     | Type  | Name                               | Extends                           | JDK   | Serialization                       | Compatibility Changes |
|----------|-------------------------------|-------|------------------------------------|-----------------------------------|-------|-------------------------------------|-----------------------|
| Modified | `final` `static` **`public`** | Class | `UnsupportedLoad…erationException` | [`UnsupportedOperationException`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |

___

<a id="user-content-com.google.common.cache.removalnotification"></a>
### `com.google.common.cache.RemovalNotification`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name                  | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|-----------------------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `RemovalNotification` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics                                               | Type                              | Method                                   | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|--------------------------------------------------------|-----------------------------------|------------------------------------------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** | \<**[`K extends Object`]**, **[`V extends Object`]**\> | **[`RemovalNotification<K, V>`]** | **`create`**(`K`, `V`, [`RemovalCause`]) |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.bimap"></a>
### `com.google.common.collect.BiMap`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type      | Name    | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------|-----------|---------|------------|-------|---------------------|-----------------------|
| Unchanged | `public` `abstract` | Interface | `BiMap` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status    | Modifiers           | Generics | Type       | Method               | Annotations      | Throws | Compatibility Changes |
|-----------|---------------------|----------|------------|----------------------|------------------|--------|-----------------------|
| Unchanged | `public` `abstract` |          | [`Object`] | `forcePut`(`K`, `V`) | **[`Nullable`]** |        | ![Annotation added]   |
| Unchanged | `public` `abstract` |          | [`Object`] | `put`(`K`, `V`)      | **[`Nullable`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.collect.collections2"></a>
### `com.google.common.collect.Collections2`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name           | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|----------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Collections2` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics                 | Type              | Method                                                | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|--------------------------|-------------------|-------------------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` | \<[`E extends Object`]\> | [`Collection<E>`] | `filter`([`Collection<E>`], [`Predicate<? super E>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.collect.comparisonchain"></a>
### `com.google.common.collect.ComparisonChain`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name              | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-------|-------------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Class | `ComparisonChain` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |


#### Methods

| Status | Modifiers                | Generics | Type                    | Method                                  | Annotations        | Throws | Compatibility Changes |
|--------|--------------------------|----------|-------------------------|-----------------------------------------|--------------------|--------|-----------------------|
| Added  | **`final`** **`public`** |          | **[`ComparisonChain`]** | **`compare`**([`Boolean`], [`Boolean`]) | **[`Deprecated`]** |        | ![Method added to public class] ![Annotation deprecated added] |

___

<a id="user-content-com.google.common.collect.fluentiterable"></a>
### `com.google.common.collect.FluentIterable`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Class | `FluentIterable` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `E`  | [`Object`] | ![No changes]         |


#### Methods

| Status    | Modifiers                | Generics                 | Type                            | Method                                    | Annotations              | Throws | Compatibility Changes |
|-----------|--------------------------|--------------------------|---------------------------------|-------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `final` `public`         |                          | `boolean`                       | `allMatch`([`Predicate<? super E>`])      | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | `boolean`                       | `anyMatch`([`Predicate<? super E>`])      | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | `boolean`                       | `contains`([`Object`])                    | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | [`Optional<E>`]                 | `first`()                                 | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | [`Optional<E>`]                 | `firstMatch`([`Predicate<? super E>`])    | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`        | \<[`E extends Object`]\> | [`FluentIterable<E>`]           | `from`([`Iterable<E>`])                   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`        | \<[`E extends Object`]\> | [`FluentIterable<E>`]           | `from`([`FluentIterable<E>`])             | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | [`Object`]                      | `get`(`int`)                              | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         | \<[`K extends Object`]\> | [`ImmutableListMultimap<K, E>`] | `index`([`Function<? super E, K>`])       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | `boolean`                       | `isEmpty`()                               | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | [`String`]                      | `join`([`Joiner`])                        | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | [`Optional<E>`]                 | `last`()                                  | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`        | \<[`E extends Object`]\> | [`FluentIterable<E>`]           | `of`([`Object[]`][1])                     | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | `int`                           | `size`()                                  | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | [`Object[]`][1]                 | `toArray`([`Class<E>`])                   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | [`ImmutableList<E>`]            | `toList`()                                | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         | \<[`V extends Object`]\> | [`ImmutableMap<E, V>`]          | `toMap`([`Function<? super E, V>`])       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Added     | **`final`** **`public`** |                          | **[`ImmutableMultiset<E>`]**    | **`toMultiset`**()                        | **[`CheckReturnValue`]** |        | ![Method added to public class] ![Annotation added] |
| Unchanged | `final` `public`         |                          | [`ImmutableSet<E>`]             | `toSet`()                                 | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | [`ImmutableList<E>`]            | `toSortedList`([`Comparator<? super E>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         |                          | [`ImmutableSortedSet<E>`]       | `toSortedSet`([`Comparator<? super E>`])  | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`                 |                          | [`String`]                      | `toString`()                              | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `final` `public`         | \<[`T extends Object`]\> | [`FluentIterable<T>`]           | `transform`([`Function<? super E, T>`])   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`                 | \<[`T extends Object`]\> | [`FluentIterable<T>`]           | `transformAndConcat`([`Function<? super E, ? extends Iterable<? extends T>>`]) | **[`CheckReturnValue`]** |  | ![Annotation added] |
| Unchanged | `final` `public`         | \<[`K extends Object`]\> | [`ImmutableMap<K, E>`]          | `uniqueIndex`([`Function<? super E, K>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.collect.hashbimap"></a>
### `com.google.common.collect.HashBiMap`

- [X] Binary-compatible
- [ ] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers        | Type  | Name        | Extends | JDK   | Serialization          | Compatibility Changes |
|----------|------------------|-------|-------------|---------|-------|------------------------|-----------------------|
| Modified | `final` `public` | Class | `HashBiMap` | ~~[`AbstractMap<K, V>`]~~ &rarr; **[`IteratorBasedAbstractMap<K, V>`]** | JDK 6 | ![Superclass modified] | ![Superclass added] |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status              | Modifiers                             | Generics | Type                                        | Method       | Annotations | Throws | Compatibility Changes |
|---------------------|---------------------------------------|----------|---------------------------------------------|--------------|-------------|--------|-----------------------|
| Source-incompatible | `public` **`bridge`** **`synthetic`** |          | ~~[`Set<Entry<K, V>>`]~~ &rarr; **[`Set`]** | `entrySet`() |             |        | ![Method return type generics changed] |

___

<a id="user-content-com.google.common.collect.immutablebimap"></a>
### `com.google.common.collect.ImmutableBiMap`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers           | Type  | Name             | Extends                | JDK   | Serialization                       | Compatibility Changes |
|----------|---------------------|-------|------------------|------------------------|-------|-------------------------------------|-----------------------|
| Modified | `public` `abstract` | Class | `ImmutableBiMap` | [`ImmutableMap<K, V>`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics                                               | Type                         | Method | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|--------------------------------------------------------|------------------------------|--------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** | \<**[`K extends Object`]**, **[`V extends Object`]**\> | **[`ImmutableBiMap<K, V>`]** | **`copyOf`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablebimap$builder"></a>
### `com.google.common.collect.ImmutableBiMap$Builder`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers                 | Type  | Name      | Extends           | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------------|-------|-----------|-------------------|-------|---------------------|-----------------------|
| Modified | `final` `static` `public` | Class | `Builder` | [`Builder<K, V>`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                     | Method                                               | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|--------------------------|------------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`Builder<K, V>`][3]** | **`orderEntriesByValue`**([`Comparator<? super V>`]) |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`Builder<K, V>`][3]** | **`put`**([`Entry<? extends K, ? extends V>`])       |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`Builder<K, V>`][3]** | **`putAll`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutableclasstoinstancemap"></a>
### `com.google.common.collect.ImmutableClassToInstanceMap`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers        | Type  | Name                          | Extends                 | JDK   | Serialization                       | Compatibility Changes |
|----------|------------------|-------|-------------------------------|-------------------------|-------|-------------------------------------|-----------------------|
| Modified | `final` `public` | Class | `ImmutableClassToInstanceMap` | [`ForwardingMap<K, V>`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `B`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics                                        | Type                                   | Method                      | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|-------------------------------------------------|----------------------------------------|-----------------------------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** | \<**[`B extends Object`]**\>                    | **[`ImmutableClassToInstanceMap<B>`]** | **`of`**()                  |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** | \<**[`B extends Object`]**, **`T extends B`**\> | **[`ImmutableClassToInstanceMap<B>`]** | **`of`**([`Class<T>`], `T`) |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablecollection"></a>
### `com.google.common.collect.ImmutableCollection`

- [ ] Binary-compatible
- [ ] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers           | Type  | Name                  | Extends                   | JDK   | Serialization                       | Compatibility Changes |
|----------|---------------------|-------|-----------------------|---------------------------|-------|-------------------------------------|-----------------------|
| Modified | `public` `abstract` | Class | `ImmutableCollection` | [`AbstractCollection<E>`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `E`  | [`Object`] | ![No changes]         |


#### Methods

| Status   | Modifiers               | Generics | Type      | Method                 | Annotations | Throws | Compatibility Changes |
|----------|-------------------------|----------|-----------|------------------------|-------------|--------|-----------------------|
| Modified | `public` **`abstract`** |          | `boolean` | `contains`([`Object`]) |             |        | ![Method now abstract] |

___

<a id="user-content-com.google.common.collect.immutablelist"></a>
### `com.google.common.collect.ImmutableList`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status                     | Modifiers           | Type  | Name            | Extends                    | JDK   | Serialization                       | Compatibility Changes |
|----------------------------|---------------------|-------|-----------------|----------------------------|-------|-------------------------------------|-----------------------|
| Serialization-incompatible | `public` `abstract` | Class | `ImmutableList` | [`ImmutableCollection<E>`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `E`  | [`Object`] | ![No changes]         |

___

<a id="user-content-com.google.common.collect.immutablelistmultimap"></a>
### `com.google.common.collect.ImmutableListMultimap`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                    | Extends                     | JDK   | Serialization | Compatibility Changes |
|----------|-----------|-------|-------------------------|-----------------------------|-------|---------------|-----------------------|
| Modified | `public`  | Class | `ImmutableListMultimap` | [`ImmutableMultimap<K, V>`] | JDK 6 | ![Compatible] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics                                               | Type                                | Method | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|--------------------------------------------------------|-------------------------------------|--------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** | \<**[`K extends Object`]**, **[`V extends Object`]**\> | **[`ImmutableListMultimap<K, V>`]** | **`copyOf`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablelistmultimap$builder"></a>
### `com.google.common.collect.ImmutableListMultimap$Builder`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers                 | Type  | Name      | Extends              | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------------|-------|-----------|----------------------|-------|---------------------|-----------------------|
| Modified | `final` `static` `public` | Class | `Builder` | [`Builder<K, V>`][4] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                     | Method | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|--------------------------|--------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`Builder<K, V>`][5]** | **`putAll`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablemap"></a>
### `com.google.common.collect.ImmutableMap`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers           | Type  | Name           | Extends    | JDK   | Serialization                       | Compatibility Changes |
|----------|---------------------|-------|----------------|------------|-------|-------------------------------------|-----------------------|
| Modified | `public` `abstract` | Class | `ImmutableMap` | [`Object`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics                                               | Type                          | Method | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|--------------------------------------------------------|-------------------------------|--------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** | \<**[`K extends Object`]**, **[`V extends Object`]**\> | **[`ImmutableMap<K, V>`][6]** | **`copyOf`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablemap$builder"></a>
### `com.google.common.collect.ImmutableMap$Builder`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers         | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-------------------|-------|-----------|------------|-------|---------------------|-----------------------|
| Modified | `static` `public` | Class | `Builder` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                     | Method                                               | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|--------------------------|------------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`Builder<K, V>`][7]** | **`orderEntriesByValue`**([`Comparator<? super V>`]) |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`Builder<K, V>`][7]** | **`putAll`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablemultimap"></a>
### `com.google.common.collect.ImmutableMultimap`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name                | Extends                    | JDK   | Serialization | Compatibility Changes |
|----------|---------------------|-------|---------------------|----------------------------|-------|---------------|-----------------------|
| Modified | `public` `abstract` | Class | `ImmutableMultimap` | [`AbstractMultimap<K, V>`] | JDK 6 | ![Compatible] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics                                               | Type                               | Method | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|--------------------------------------------------------|------------------------------------|--------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** | \<**[`K extends Object`]**, **[`V extends Object`]**\> | **[`ImmutableMultimap<K, V>`][8]** | **`copyOf`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablemultimap$builder"></a>
### `com.google.common.collect.ImmutableMultimap$Builder`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers         | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-------------------|-------|-----------|------------|-------|---------------------|-----------------------|
| Modified | `static` `public` | Class | `Builder` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                     | Method | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|--------------------------|--------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`Builder<K, V>`][9]** | **`putAll`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablemultiset"></a>
### `com.google.common.collect.ImmutableMultiset`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers           | Type  | Name                | Extends                    | JDK   | Serialization                       | Compatibility Changes |
|----------|---------------------|-------|---------------------|----------------------------|-------|-------------------------------------|-----------------------|
| Modified | `public` `abstract` | Class | `ImmutableMultiset` | [`ImmutableCollection<E>`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `E`  | [`Object`] | ![No changes]         |


#### Methods

| Status  | Modifiers    | Generics | Type          | Method                               | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|---------------|--------------------------------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`boolean`~~ | ~~`containsAll`~~([`Collection<?>`]) |             |        | ![No changes]         |

___

<a id="user-content-com.google.common.collect.immutablerangemap"></a>
### `com.google.common.collect.ImmutableRangeMap`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                | Extends    | JDK   | Serialization | Compatibility Changes |
|----------|-----------|-------|---------------------|------------|-------|---------------|-----------------------|
| Modified | `public`  | Class | `ImmutableRangeMap` | [`Object`] | JDK 6 | ![Compatible] | ![Interface added]    |


#### Generics

| Status    | Name | Extends           | Compatibility Changes |
|-----------|------|-------------------|-----------------------|
| Unchanged | `K`  | [`Comparable<?>`] | ![No changes]         |
| Unchanged | `V`  | [`Object`]        | ![No changes]         |


#### Implemented Interfaces

| Status              | Interface            | Compatibility Changes |
|---------------------|----------------------|-----------------------|
| Source-incompatible | [`RangeMap<K, V>`]   | ![No changes]         |
| Added               | **[`Serializable`]** | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                              | Method                          | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|-----------------------------------|---------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`ImmutableMap<Range<K>, V>`]** | **`asDescendingMapOfRanges`**() |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablerangeset"></a>
### `com.google.common.collect.ImmutableRangeSet`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers        | Type  | Name                | Extends                 | JDK   | Serialization                       | Compatibility Changes |
|----------|------------------|-------|---------------------|-------------------------|-------|-------------------------------------|-----------------------|
| Modified | `final` `public` | Class | `ImmutableRangeSet` | [`AbstractRangeSet<C>`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends        | Compatibility Changes |
|-----------|------|----------------|-----------------------|
| Unchanged | `C`  | [`Comparable`] | ![No changes]         |


#### Implemented Interfaces

| Status              | Interface       | Compatibility Changes |
|---------------------|-----------------|-----------------------|
| Source-incompatible | [`RangeSet<C>`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                           | Method                          | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|--------------------------------|---------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`ImmutableSet<Range<C>>`]** | **`asDescendingSetOfRanges`**() |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablesetmultimap"></a>
### `com.google.common.collect.ImmutableSetMultimap`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                   | Extends                     | JDK   | Serialization | Compatibility Changes |
|----------|-----------|-------|------------------------|-----------------------------|-------|---------------|-----------------------|
| Modified | `public`  | Class | `ImmutableSetMultimap` | [`ImmutableMultimap<K, V>`] | JDK 6 | ![Compatible] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics                                               | Type                               | Method | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|--------------------------------------------------------|------------------------------------|--------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** | \<**[`K extends Object`]**, **[`V extends Object`]**\> | **[`ImmutableSetMultimap<K, V>`]** | **`copyOf`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablesetmultimap$builder"></a>
### `com.google.common.collect.ImmutableSetMultimap$Builder`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers                 | Type  | Name      | Extends              | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------------|-------|-----------|----------------------|-------|---------------------|-----------------------|
| Modified | `final` `static` `public` | Class | `Builder` | [`Builder<K, V>`][4] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                      | Method | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|---------------------------|--------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`Builder<K, V>`][10]** | **`putAll`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablesortedmap"></a>
### `com.google.common.collect.ImmutableSortedMap`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers                           | Type  | Name                 | Extends                                    | JDK   | Serialization | Compatibility Changes |
|----------|-------------------------------------|-------|----------------------|--------------------------------------------|-------|---------------|-----------------------|
| Modified | **`final`** `public` ~~`abstract`~~ | Class | `ImmutableSortedMap` | [`ImmutableSortedMapFauxverideShim<K, V>`] | JDK 6 | ![Compatible] | ![Class now final]    |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status   | Modifiers                 | Generics                                               | Type                             | Method                          | Annotations | Throws | Compatibility Changes |
|----------|---------------------------|--------------------------------------------------------|----------------------------------|---------------------------------|-------------|--------|-----------------------|
| Removed  | ~~`public`~~              |                                                        | ~~`boolean`~~                    | ~~`containsValue`~~([`Object`]) |             |        | ![No changes]         |
| Added    | **`static`** **`public`** | \<**[`K extends Object`]**, **[`V extends Object`]**\> | **[`ImmutableSortedMap<K, V>`]** | **`copyOf`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |  |  | ![Method added to public class] |
| Added    | **`static`** **`public`** | \<**[`K extends Object`]**, **[`V extends Object`]**\> | **[`ImmutableSortedMap<K, V>`]** | **`copyOf`**([`Iterable<? extends Entry<? extends K, ? extends V>>`], [`Comparator<? super K>`]) |  |  | ![Method added to public class] |
| Added    | **`public`**              |                                                        | **[`Object`]**                   | **`get`**([`Object`])           |             |        | ![Method added to public class] |
| Modified | `public` ~~`abstract`~~   |                                                        | [`ImmutableSortedMap<K, V>`]     | `headMap`(`K`, `boolean`)       |             |        | ![No changes]         |
| Modified | `public` ~~`abstract`~~   |                                                        | [`ImmutableSortedSet<K>`]        | `keySet`()                      |             |        | ![No changes]         |
| Modified | `public` ~~`abstract`~~   |                                                        | [`ImmutableSortedMap<K, V>`]     | `tailMap`(`K`, `boolean`)       |             |        | ![No changes]         |
| Modified | `public` ~~`abstract`~~   |                                                        | [`ImmutableCollection<V>`]       | `values`()                      |             |        | ![No changes]         |

___

<a id="user-content-com.google.common.collect.immutablesortedmap$builder"></a>
### `com.google.common.collect.ImmutableSortedMap$Builder`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers         | Type  | Name      | Extends           | JDK   | Serialization       | Compatibility Changes |
|----------|-------------------|-------|-----------|-------------------|-------|---------------------|-----------------------|
| Modified | `static` `public` | Class | `Builder` | [`Builder<K, V>`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                      | Method                                               | Annotations        | Throws | Compatibility Changes |
|--------|--------------|----------|---------------------------|------------------------------------------------------|--------------------|--------|-----------------------|
| Added  | **`public`** |          | **[`Builder<K, V>`][11]** | **`orderEntriesByValue`**([`Comparator<? super V>`]) | **[`Deprecated`]** |        | ![Method added to public class] ![Annotation deprecated added] |
| Added  | **`public`** |          | **[`Builder<K, V>`][11]** | **`putAll`**([`Iterable<? extends Entry<? extends K, ? extends V>>`]) |   |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.immutablesortedmultiset"></a>
### `com.google.common.collect.ImmutableSortedMultiset`

- [X] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status              | Modifiers           | Type  | Name                      | Extends                                 | JDK   | Serialization | Compatibility Changes |
|---------------------|---------------------|-------|---------------------------|-----------------------------------------|-------|---------------|-----------------------|
| Source-incompatible | `public` `abstract` | Class | `ImmutableSortedMultiset` | [`ImmutableSorted…etFauxverideShim<E>`] | JDK 6 | ![Compatible] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `E`  | [`Object`] | ![No changes]         |


#### Methods

| Status              | Modifiers         | Generics | Type           | Method           | Annotations | Throws | Compatibility Changes |
|---------------------|-------------------|----------|----------------|------------------|-------------|--------|-----------------------|
| Source-incompatible | `static` `public` | \<~~[`E extends Comparable<E>`]~~ &rarr; **[`E extends Comparable<?>`]**\> | [`Builder<E>`] | `naturalOrder`() |  |  | ![Class generic template generics changed] |
| Source-incompatible | `static` `public` | \<~~[`E extends Comparable<E>`]~~ &rarr; **[`E extends Comparable<?>`]**\> | [`Builder<E>`] | `reverseOrder`() |  |  | ![Class generic template generics changed] |

___

<a id="user-content-com.google.common.collect.immutablesortedset"></a>
### `com.google.common.collect.ImmutableSortedSet`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status                     | Modifiers           | Type  | Name                 | Extends                                 | JDK   | Serialization                       | Compatibility Changes |
|----------------------------|---------------------|-------|----------------------|-----------------------------------------|-------|-------------------------------------|-----------------------|
| Serialization-incompatible | `public` `abstract` | Class | `ImmutableSortedSet` | [`ImmutableSortedSetFauxverideShim<E>`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `E`  | [`Object`] | ![No changes]         |

___

<a id="user-content-com.google.common.collect.iterables"></a>
### `com.google.common.collect.Iterables`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Iterables` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics                                       | Type            | Method                                              | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|------------------------------------------------|-----------------|-----------------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` |                                                | `boolean`       | `elementsEqual`([`Iterable<?>`], [`Iterable<?>`])   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`T extends Object`]\>                       | [`Iterable<T>`] | `filter`([`Iterable<T>`], [`Predicate<? super T>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`T extends Object`]\>                       | [`Iterable<T>`] | `filter`([`Iterable<?>`], [`Class<T>`])             | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`F extends Object`], [`T extends Object`]\> | [`Iterable<T>`] | `transform`([`Iterable<F>`], [`Function<? super F, ? extends T>`]) | **[`CheckReturnValue`]** |  | ![Annotation added] |

___

<a id="user-content-com.google.common.collect.iterators"></a>
### `com.google.common.collect.Iterators`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Iterators` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics                 | Type                        | Method                                              | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|--------------------------|-----------------------------|-----------------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` | \<[`T extends Object`]\> | [`UnmodifiableIterator<T>`] | `filter`([`Iterator<T>`], [`Predicate<? super T>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`T extends Object`]\> | [`UnmodifiableIterator<T>`] | `filter`([`Iterator<?>`], [`Class<T>`])             | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.collect.lists"></a>
### `com.google.common.collect.Lists`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name    | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|---------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `Lists` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers             | Generics                                       | Type              | Method                                                         | Annotations              | Throws | Compatibility Changes |
|-----------|-----------------------|------------------------------------------------|-------------------|----------------------------------------------------------------|--------------------------|--------|-----------------------|
| Modified  | `static` **`public`** | \<[`B extends Object`]\>                       | [`List<List<B>>`] | `cartesianProduct`([`List<? extends List<? extends B>>`])      |                          |        | ![No changes]         |
| Modified  | `static` **`public`** | \<[`B extends Object`]\>                       | [`List<List<B>>`] | `cartesianProduct`([`List...<? extends B>`])                   |                          |        | ![No changes]         |
| Unchanged | `static` `public`     | \<[`T extends Object`]\>                       | [`List<T>`]       | `reverse`([`List<T>`])                                         | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`     | \<[`F extends Object`], [`T extends Object`]\> | [`List<T>`]       | `transform`([`List<F>`], [`Function<? super F, ? extends T>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.collect.mapconstraint"></a>
### `com.google.common.collect.MapConstraint`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type      | Name            | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------|-----------|-----------------|------------|-------|---------------------|-----------------------|
| Unchanged | `public` `abstract` | Interface | `MapConstraint` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation deprecated added] |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Annotations

| Status | Annotation         | Compatibility Changes |
|--------|--------------------|-----------------------|
| Added  | **[`Deprecated`]** | ![No changes]         |

___

<a id="user-content-com.google.common.collect.mapconstraints"></a>
### `com.google.common.collect.MapConstraints`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|------------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `MapConstraints` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation deprecated added] |


#### Annotations

| Status | Annotation         | Compatibility Changes |
|--------|--------------------|-----------------------|
| Added  | **[`Deprecated`]** | ![No changes]         |

___

<a id="user-content-com.google.common.collect.mapmaker"></a>
### `com.google.common.collect.MapMaker`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name       | Extends                     | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|------------|-----------------------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `MapMaker` | [`GenericMapMaker<K0, V0>`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers    | Generics | Type         | Method         | Annotations | Throws | Compatibility Changes |
|----------|--------------|----------|--------------|----------------|-------------|--------|-----------------------|
| Modified | ~~`public`~~ |          | [`MapMaker`] | `softValues`() |             |        | ![Method less accessible] |

___

<a id="user-content-com.google.common.collect.maps"></a>
### `com.google.common.collect.Maps`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name   | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|--------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `Maps` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers                 | Generics                                               | Type                        | Method                                                           | Annotations              | Throws | Compatibility Changes |
|-----------|---------------------------|--------------------------------------------------------|-----------------------------|------------------------------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`Map<K, V>`]               | `filterEntries`([`Map<K, V>`], [`Predicate<? super Entry<? super K, ? super V>>`]) | **[`CheckReturnValue`]** |  | ![Annotation added] |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`SortedMap<K, V>`]         | `filterEntries`([`SortedMap<K, V>`], [`Predicate<? super Entry<? super K, ? super V>>`]) | **[`CheckReturnValue`]** |  | ![Annotation added] |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`NavigableMap<K, V>`]      | `filterEntries`([`NavigableMap<K, V>`], [`Predicate<? super Entry<? super K, ? super V>>`]) | **[`CheckReturnValue`]** |  | ![Annotation added] |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`BiMap<K, V>`]             | `filterEntries`([`BiMap<K, V>`], [`Predicate<? super Entry<? super K, ? super V>>`]) | **[`CheckReturnValue`]** |  | ![Annotation added] |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`Map<K, V>`]               | `filterKeys`([`Map<K, V>`], [`Predicate<? super K>`])            | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`SortedMap<K, V>`]         | `filterKeys`([`SortedMap<K, V>`], [`Predicate<? super K>`])      | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`NavigableMap<K, V>`]      | `filterKeys`([`NavigableMap<K, V>`], [`Predicate<? super K>`])   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`BiMap<K, V>`]             | `filterKeys`([`BiMap<K, V>`], [`Predicate<? super K>`])          | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`Map<K, V>`]               | `filterValues`([`Map<K, V>`], [`Predicate<? super V>`])          | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`SortedMap<K, V>`]         | `filterValues`([`SortedMap<K, V>`], [`Predicate<? super V>`])    | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`NavigableMap<K, V>`]      | `filterValues`([`NavigableMap<K, V>`], [`Predicate<? super V>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`K extends Object`], [`V extends Object`]\>         | [`BiMap<K, V>`]             | `filterValues`([`BiMap<K, V>`], [`Predicate<? super V>`])        | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Added     | **`static`** **`public`** | \<**[`K extends Object`]**, **[`V extends Object`]**\> | **[`LinkedHashMap<K, V>`]** | **`newLinkedHashMapWithExpectedSize`**(`int`)                    |                          |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.multimapbuilder"></a>
### `com.google.common.collect.MultimapBuilder`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type  | Name              | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------|-------|-------------------|------------|-------|---------------------|-----------------------|
| Unchanged | `public` `abstract` | Class | `MultimapBuilder` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `K0` | [`Object`] | ![No changes]         |
| Unchanged | `V0` | [`Object`] | ![No changes]         |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.collect.multimaps"></a>
### `com.google.common.collect.Multimaps`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Multimaps` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics                                       | Type                   | Method                                                          | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|------------------------------------------------|------------------------|-----------------------------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` | \<[`K extends Object`], [`V extends Object`]\> | [`Multimap<K, V>`]     | `filterEntries`([`Multimap<K, V>`], [`Predicate<? super Entry<? super K, ? super V>>`]) | **[`CheckReturnValue`]** |  | ![Annotation added] |
| Unchanged | `static` `public` | \<[`K extends Object`], [`V extends Object`]\> | [`SetMultimap<K, V>`]  | `filterEntries`([`SetMultimap<K, V>`], [`Predicate<? super Entry<? super K, ? super V>>`]) | **[`CheckReturnValue`]** |  | ![Annotation added] |
| Unchanged | `static` `public` | \<[`K extends Object`], [`V extends Object`]\> | [`Multimap<K, V>`]     | `filterKeys`([`Multimap<K, V>`], [`Predicate<? super K>`])      | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`K extends Object`], [`V extends Object`]\> | [`SetMultimap<K, V>`]  | `filterKeys`([`SetMultimap<K, V>`], [`Predicate<? super K>`])   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`K extends Object`], [`V extends Object`]\> | [`ListMultimap<K, V>`] | `filterKeys`([`ListMultimap<K, V>`], [`Predicate<? super K>`])  | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`K extends Object`], [`V extends Object`]\> | [`Multimap<K, V>`]     | `filterValues`([`Multimap<K, V>`], [`Predicate<? super V>`])    | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`K extends Object`], [`V extends Object`]\> | [`SetMultimap<K, V>`]  | `filterValues`([`SetMultimap<K, V>`], [`Predicate<? super V>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.collect.multisets"></a>
### `com.google.common.collect.Multisets`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|-------------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `Multisets` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers                 | Generics                 | Type            | Method                                                    | Annotations              | Throws | Compatibility Changes |
|-----------|---------------------------|--------------------------|-----------------|-----------------------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public`         | \<[`E extends Object`]\> | [`Multiset<E>`] | `filter`([`Multiset<E>`], [`Predicate<? super E>`])       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Added     | **`static`** **`public`** |                          | **`boolean`**   | **`removeOccurrences`**([`Multiset<?>`], [`Multiset<?>`]) |                          |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.rangemap"></a>
### `com.google.common.collect.RangeMap`

- [X] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type      | Name       | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-----------|------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Interface | `RangeMap` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends        | Compatibility Changes |
|-----------|------|----------------|-----------------------|
| Unchanged | `K`  | [`Comparable`] | ![No changes]         |
| Unchanged | `V`  | [`Object`]     | ![No changes]         |


#### Methods

| Status | Modifiers                   | Generics | Type                     | Method                          | Annotations | Throws | Compatibility Changes |
|--------|-----------------------------|----------|--------------------------|---------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** **`abstract`** |          | **[`Map<Range<K>, V>`]** | **`asDescendingMapOfRanges`**() |             |        | ![Method added to interface] |

___

<a id="user-content-com.google.common.collect.rangeset"></a>
### `com.google.common.collect.RangeSet`

- [X] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type      | Name       | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-----------|------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Interface | `RangeSet` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends        | Compatibility Changes |
|-----------|------|----------------|-----------------------|
| Unchanged | `C`  | [`Comparable`] | ![No changes]         |


#### Methods

| Status | Modifiers                   | Generics | Type                  | Method                          | Annotations | Throws | Compatibility Changes |
|--------|-----------------------------|----------|-----------------------|---------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** **`abstract`** |          | **[`Set<Range<C>>`]** | **`asDescendingSetOfRanges`**() |             |        | ![Method added to interface] |

___

<a id="user-content-com.google.common.collect.sets"></a>
### `com.google.common.collect.Sets`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name   | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|--------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Sets` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics                 | Type                | Method                                                  | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|--------------------------|---------------------|---------------------------------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` | \<[`E extends Object`]\> | [`Set<E>`]          | `filter`([`Set<E>`], [`Predicate<? super E>`])          | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`E extends Object`]\> | [`SortedSet<E>`]    | `filter`([`SortedSet<E>`], [`Predicate<? super E>`])    | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`E extends Object`]\> | [`NavigableSet<E>`] | `filter`([`NavigableSet<E>`], [`Predicate<? super E>`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` | \<[`E extends Object`]\> | [`Set<E>`]          | `newSetFromMap`([`Map<E, Boolean>`])                    | **[`Deprecated`]**       |        | ![Annotation deprecated added] |

___

<a id="user-content-com.google.common.collect.table"></a>
### `com.google.common.collect.Table`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type      | Name    | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------|-----------|---------|------------|-------|---------------------|-----------------------|
| Unchanged | `public` `abstract` | Interface | `Table` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `C`  | [`Object`] | ![No changes]         |
| Unchanged | `R`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status    | Modifiers           | Generics | Type       | Method                           | Annotations      | Throws | Compatibility Changes |
|-----------|---------------------|----------|------------|----------------------------------|------------------|--------|-----------------------|
| Unchanged | `public` `abstract` |          | [`Object`] | `put`(`R`, `C`, `V`)             | **[`Nullable`]** |        | ![Annotation added]   |
| Unchanged | `public` `abstract` |          | [`Object`] | `remove`([`Object`], [`Object`]) | **[`Nullable`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.collect.table$cell"></a>
### `com.google.common.collect.Table$Cell`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers                    | Type      | Name   | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------------------|-----------|--------|------------|-------|---------------------|-----------------------|
| Unchanged | `static` `public` `abstract` | Interface | `Cell` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `C`  | [`Object`] | ![No changes]         |
| Unchanged | `R`  | [`Object`] | ![No changes]         |
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status    | Modifiers           | Generics | Type       | Method           | Annotations      | Throws | Compatibility Changes |
|-----------|---------------------|----------|------------|------------------|------------------|--------|-----------------------|
| Unchanged | `public` `abstract` |          | [`Object`] | `getColumnKey`() | **[`Nullable`]** |        | ![Annotation added]   |
| Unchanged | `public` `abstract` |          | [`Object`] | `getRowKey`()    | **[`Nullable`]** |        | ![Annotation added]   |
| Unchanged | `public` `abstract` |          | [`Object`] | `getValue`()     | **[`Nullable`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.collect.treerangemap"></a>
### `com.google.common.collect.TreeRangeMap`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name           | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|----------------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `TreeRangeMap` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends        | Compatibility Changes |
|-----------|------|----------------|-----------------------|
| Unchanged | `K`  | [`Comparable`] | ![No changes]         |
| Unchanged | `V`  | [`Object`]     | ![No changes]         |


#### Implemented Interfaces

| Status              | Interface          | Compatibility Changes |
|---------------------|--------------------|-----------------------|
| Source-incompatible | [`RangeMap<K, V>`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                     | Method                          | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|--------------------------|---------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`Map<Range<K>, V>`]** | **`asDescendingMapOfRanges`**() |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.collect.treerangeset"></a>
### `com.google.common.collect.TreeRangeSet`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name           | Extends                 | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|----------------|-------------------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `TreeRangeSet` | [`AbstractRangeSet<C>`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends           | Compatibility Changes |
|-----------|------|-------------------|-----------------------|
| Unchanged | `C`  | [`Comparable<?>`] | ![No changes]         |


#### Implemented Interfaces

| Status              | Interface       | Compatibility Changes |
|---------------------|-----------------|-----------------------|
| Source-incompatible | [`RangeSet<C>`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                  | Method                          | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|-----------------------|---------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`Set<Range<C>>`]** | **`asDescendingSetOfRanges`**() |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.eventbus.asynceventbus"></a>
### `com.google.common.eventbus.AsyncEventBus`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name            | Extends      | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------------|--------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `AsyncEventBus` | [`EventBus`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status  | Modifiers       | Generics | Type       | Method                       | Annotations | Throws | Compatibility Changes |
|---------|-----------------|----------|------------|------------------------------|-------------|--------|-----------------------|
| Removed | ~~`protected`~~ |          | ~~`void`~~ | ~~`dispatchQueuedEvents`~~() |             |        | ![Method removed]     |

___

<a id="user-content-com.google.common.eventbus.deadevent"></a>
### `com.google.common.eventbus.DeadEvent`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `DeadEvent` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type           | Method           | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|----------------|------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`String`]** | **`toString`**() |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.eventbus.eventbus"></a>
### `com.google.common.eventbus.EventBus`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name       | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `EventBus` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers                | Generics | Type           | Method             | Annotations | Throws | Compatibility Changes |
|--------|--------------------------|----------|----------------|--------------------|-------------|--------|-----------------------|
| Added  | **`final`** **`public`** |          | **[`String`]** | **`identifier`**() |             |        | ![Method added to public class] |
| Added  | **`public`**             |          | **[`String`]** | **`toString`**()   |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.hash.bloomfilter"></a>
### `com.google.common.hash.BloomFilter`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers        | Type  | Name          | Extends    | JDK   | Serialization                       | Compatibility Changes |
|----------|------------------|-------|---------------|------------|-------|-------------------------------------|-----------------------|
| Modified | `final` `public` | Class | `BloomFilter` | [`Object`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `T`  | [`Object`] | ![No changes]         |


#### Methods

| Status    | Modifiers                 | Generics                     | Type                   | Method                                                | Annotations              | Throws          | Compatibility Changes |
|-----------|---------------------------|------------------------------|------------------------|-------------------------------------------------------|--------------------------|-----------------|-----------------------|
| Unchanged | `public`                  |                              | `boolean`              | `apply`(`T`)                                          | **[`CheckReturnValue`]** |                 | ![Annotation added]   |
| Unchanged | `public`                  |                              | [`BloomFilter<T>`]     | `copy`()                                              | **[`CheckReturnValue`]** |                 | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`T extends Object`]\>     | [`BloomFilter<T>`]     | `create`([`Funnel<? super T>`], `int`, `double`)      | **[`CheckReturnValue`]** |                 | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`T extends Object`]\>     | [`BloomFilter<T>`]     | `create`([`Funnel<? super T>`], `int`)                | **[`CheckReturnValue`]** |                 | ![Annotation added]   |
| Added     | **`static`** **`public`** | \<**[`T extends Object`]**\> | **[`BloomFilter<T>`]** | **`create`**([`Funnel<? super T>`], `long`, `double`) | **[`CheckReturnValue`]** |                 | ![Method added to public class] ![Annotation added] |
| Added     | **`static`** **`public`** | \<**[`T extends Object`]**\> | **[`BloomFilter<T>`]** | **`create`**([`Funnel<? super T>`], `long`)           | **[`CheckReturnValue`]** |                 | ![Method added to public class] ![Annotation added] |
| Unchanged | `public`                  |                              | `double`               | `expectedFpp`()                                       | **[`CheckReturnValue`]** |                 | ![Annotation added]   |
| Unchanged | `public`                  |                              | `boolean`              | `isCompatible`([`BloomFilter<T>`])                    | **[`CheckReturnValue`]** |                 | ![Annotation added]   |
| Unchanged | `public`                  |                              | `boolean`              | `mightContain`(`T`)                                   | **[`CheckReturnValue`]** |                 | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`T extends Object`]\>     | [`BloomFilter<T>`]     | `readFrom`([`InputStream`], [`Funnel<T>`])            | **[`CheckReturnValue`]** | [`IOException`] | ![Annotation added]   |

___

<a id="user-content-com.google.common.hash.funnels"></a>
### `com.google.common.hash.Funnels`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-----------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Funnels` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.hash.hashcode"></a>
### `com.google.common.hash.HashCode`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type  | Name       | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------|-------|------------|------------|-------|---------------------|-----------------------|
| Unchanged | `public` `abstract` | Class | `HashCode` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers           | Generics | Type         | Method                   | Annotations              | Throws | Compatibility Changes |
|-----------|---------------------|----------|--------------|--------------------------|--------------------------|--------|-----------------------|
| Unchanged | `public` `abstract` |          | `byte[]`     | `asBytes`()              | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public` `abstract` |          | `int`        | `asInt`()                | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public` `abstract` |          | `long`       | `asLong`()               | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public` `abstract` |          | `int`        | `bits`()                 | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`   |          | [`HashCode`] | `fromBytes`(`byte[]`)    | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`   |          | [`HashCode`] | `fromInt`(`int`)         | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`   |          | [`HashCode`] | `fromLong`(`long`)       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public`   |          | [`HashCode`] | `fromString`([`String`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public` `abstract` |          | `long`       | `padToLong`()            | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.hash.hasher"></a>
### `com.google.common.hash.Hasher`

- [X] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type      | Name     | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-----------|----------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Interface | `Hasher` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers                   | Generics | Type         | Method           | Annotations              | Throws | Compatibility Changes |
|-----------|-----------------------------|----------|--------------|------------------|--------------------------|--------|-----------------------|
| Unchanged | `public` `abstract`         |          | [`HashCode`] | `hash`()         | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Added     | **`public`** **`abstract`** |          | **`int`**    | **`hashCode`**() | **[`Deprecated`]**       |        | ![Method added to interface] ![Annotation deprecated added] |

___

<a id="user-content-com.google.common.hash.hashing"></a>
### `com.google.common.hash.Hashing`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|-----------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `Hashing` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics | Type                 | Method                                          | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|----------|----------------------|-------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** |          | **[`HashFunction`]** | **`concatenating`**([`HashFunction`], [`HashFunction`], [`HashFunction...`]) |  |  | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`HashFunction`]** | **`concatenating`**([`Iterable<HashFunction>`]) |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`HashFunction`]** | **`sha384`**()                                  |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.hash.hashinginputstream"></a>
### `com.google.common.hash.HashingInputStream`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name                 | Extends               | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|----------------------|-----------------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `HashingInputStream` | [`FilterInputStream`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers | Generics | Type         | Method            | Annotations              | Throws | Compatibility Changes |
|-----------|-----------|----------|--------------|-------------------|--------------------------|--------|-----------------------|
| Unchanged | `public`  |          | [`HashCode`] | `hash`()          | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`  |          | `boolean`    | `markSupported`() | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.hash.hashingoutputstream"></a>
### `com.google.common.hash.HashingOutputStream`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name                  | Extends                | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-----------------------|------------------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `HashingOutputStream` | [`FilterOutputStream`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers | Generics | Type         | Method   | Annotations              | Throws | Compatibility Changes |
|-----------|-----------|----------|--------------|----------|--------------------------|--------|-----------------------|
| Unchanged | `public`  |          | [`HashCode`] | `hash`() | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.io.baseencoding"></a>
### `com.google.common.io.BaseEncoding`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name           | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-------|----------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Class | `BaseEncoding` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers                           | Generics | Type             | Method                       | Annotations | Throws | Compatibility Changes |
|----------|-------------------------------------|----------|------------------|------------------------------|-------------|--------|-----------------------|
| Modified | ~~`final`~~ `public` **`abstract`** |          | [`InputStream`]  | `decodingStream`([`Reader`]) |             |        | ![Method now abstract] |
| Modified | ~~`final`~~ `public` **`abstract`** |          | [`OutputStream`] | `encodingStream`([`Writer`]) |             |        | ![Method now abstract] |

___

<a id="user-content-com.google.common.io.bytesource"></a>
### `com.google.common.io.ByteSource`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name         | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-------|--------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Class | `ByteSource` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                   | Method              | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|------------------------|---------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`Optional<Long>`]** | **`sizeIfKnown`**() |             |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.io.charsource"></a>
### `com.google.common.io.CharSource`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name         | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-------|--------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Class | `CharSource` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                   | Method                | Annotations | Throws              | Compatibility Changes |
|--------|--------------|----------|------------------------|-----------------------|-------------|---------------------|-----------------------|
| Added  | **`public`** |          | **`long`**             | **`length`**()        |             | **[`IOException`]** | ![Method added to public class] |
| Added  | **`public`** |          | **[`Optional<Long>`]** | **`lengthIfKnown`**() |             |                     | ![Method added to public class] |

___

<a id="user-content-com.google.common.net.httpheaders"></a>
### `com.google.common.net.HttpHeaders`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name          | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|---------------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `HttpHeaders` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Fields

| Status | Modifiers                             | Type           | Name        | Annotations | Compatibility Changes |
|--------|---------------------------------------|----------------|-------------|-------------|-----------------------|
| Added  | **`public`** **`static`** **`final`** | **[`String`]** | `PING_FROM` |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]** | `PING_TO`   |             | ![No changes]         |

___

<a id="user-content-com.google.common.net.mediatype"></a>
### `com.google.common.net.MediaType`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|-------------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `MediaType` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Fields

| Status | Modifiers                             | Type              | Name                  | Annotations | Compatibility Changes |
|--------|---------------------------------------|-------------------|-----------------------|-------------|-----------------------|
| Added  | **`public`** **`static`** **`final`** | **[`MediaType`]** | `APPLE_PASSBOOK`      |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`MediaType`]** | `DART_UTF_8`          |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`MediaType`]** | `MANIFEST_JSON_UTF_8` |             | ![No changes]         |

___

<a id="user-content-com.google.common.primitives.booleans"></a>
### `com.google.common.primitives.Booleans`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name       | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Booleans` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.primitives.bytes"></a>
### `com.google.common.primitives.Bytes`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name    | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|---------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Bytes` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.primitives.chars"></a>
### `com.google.common.primitives.Chars`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name    | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|---------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Chars` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.primitives.doubles"></a>
### `com.google.common.primitives.Doubles`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-----------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Doubles` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type       | Method                 | Annotations          | Throws | Compatibility Changes |
|-----------|-------------------|----------|------------|------------------------|----------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | [`Double`] | `tryParse`([`String`]) | **[`CheckForNull`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.primitives.floats"></a>
### `com.google.common.primitives.Floats`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name     | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|----------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Floats` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type      | Method                 | Annotations          | Throws | Compatibility Changes |
|-----------|-------------------|----------|-----------|------------------------|----------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | [`Float`] | `tryParse`([`String`]) | **[`CheckForNull`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.primitives.ints"></a>
### `com.google.common.primitives.Ints`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name   | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|--------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `Ints` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |


#### Methods

| Status    | Modifiers             | Generics | Type        | Method                        | Annotations      | Throws | Compatibility Changes |
|-----------|-----------------------|----------|-------------|-------------------------------|------------------|--------|-----------------------|
| Unchanged | `static` `public`     |          | [`Integer`] | `tryParse`([`String`])        | **[`Nullable`]** |        | ![Annotation added]   |
| Modified  | `static` **`public`** |          | [`Integer`] | `tryParse`([`String`], `int`) | **[`Nullable`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.primitives.longs"></a>
### `com.google.common.primitives.Longs`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name    | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|---------|------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `Longs` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |


#### Methods

| Status    | Modifiers                 | Generics | Type         | Method                            | Annotations                              | Throws | Compatibility Changes |
|-----------|---------------------------|----------|--------------|-----------------------------------|------------------------------------------|--------|-----------------------|
| Unchanged | `static` `public`         |          | [`Long`]     | `tryParse`([`String`])            | **[`CheckForNull`]**<br>**[`Nullable`]** |        | ![Annotation added]   |
| Added     | **`static`** **`public`** |          | **[`Long`]** | **`tryParse`**([`String`], `int`) | **[`CheckForNull`]**<br>**[`Nullable`]** |        | ![Method added to public class] ![Annotation added] |

___

<a id="user-content-com.google.common.primitives.primitives"></a>
### `com.google.common.primitives.Primitives`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name         | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|--------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Primitives` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.primitives.shorts"></a>
### `com.google.common.primitives.Shorts`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name     | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|----------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `Shorts` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.primitives.signedbytes"></a>
### `com.google.common.primitives.SignedBytes`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name          | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|---------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `SignedBytes` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |

___

<a id="user-content-com.google.common.primitives.unsignedbytes"></a>
### `com.google.common.primitives.UnsignedBytes`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name            | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-----------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `UnsignedBytes` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type           | Method                        | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|----------|----------------|-------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | `int`          | `compare`(`byte`, `byte`)     | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`String`]     | `join`([`String`], `byte...`) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Comparator`] | `lexicographicalComparator`() | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `byte`         | `max`(`byte...`)              | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `byte`         | `min`(`byte...`)              | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `int`          | `toInt`(`byte`)               | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`String`]     | `toString`(`byte`)            | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`String`]     | `toString`(`byte`, `int`)     | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.primitives.unsignedinteger"></a>
### `com.google.common.primitives.UnsignedInteger`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name              | Extends    | JDK   | Serialization | Compatibility Changes |
|-----------|------------------|-------|-------------------|------------|-------|---------------|-----------------------|
| Unchanged | `final` `public` | Class | `UnsignedInteger` | [`Number`] | JDK 6 | ![Compatible] | ![Annotation added]   |


#### Annotations

| Status | Annotation               | Compatibility Changes |
|--------|--------------------------|-----------------------|
| Added  | **[`CheckReturnValue`]** | ![No changes]         |


#### Methods

| Status    | Modifiers | Generics | Type                | Method                           | Annotations              | Throws | Compatibility Changes |
|-----------|-----------|----------|---------------------|----------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `public`  |          | [`UnsignedInteger`] | `dividedBy`([`UnsignedInteger`]) | ~~[`CheckReturnValue`]~~ |        | ![Annotation removed] |
| Unchanged | `public`  |          | [`UnsignedInteger`] | `minus`([`UnsignedInteger`])     | ~~[`CheckReturnValue`]~~ |        | ![Annotation removed] |
| Unchanged | `public`  |          | [`UnsignedInteger`] | `mod`([`UnsignedInteger`])       | ~~[`CheckReturnValue`]~~ |        | ![Annotation removed] |
| Unchanged | `public`  |          | [`UnsignedInteger`] | `plus`([`UnsignedInteger`])      | ~~[`CheckReturnValue`]~~ |        | ![Annotation removed] |
| Unchanged | `public`  |          | [`UnsignedInteger`] | `times`([`UnsignedInteger`])     | ~~[`CheckReturnValue`]~~ |        | ![Annotation removed] |

___

<a id="user-content-com.google.common.primitives.unsignedints"></a>
### `com.google.common.primitives.UnsignedInts`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name           | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|----------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `UnsignedInts` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type           | Method                        | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|----------|----------------|-------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | `int`          | `compare`(`int`, `int`)       | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `int`          | `divide`(`int`, `int`)        | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`String`]     | `join`([`String`], `int...`)  | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Comparator`] | `lexicographicalComparator`() | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `int`          | `max`(`int...`)               | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `int`          | `min`(`int...`)               | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `int`          | `remainder`(`int`, `int`)     | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `long`         | `toLong`(`int`)               | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`String`]     | `toString`(`int`)             | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`String`]     | `toString`(`int`, `int`)      | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.primitives.unsignedlong"></a>
### `com.google.common.primitives.UnsignedLong`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name           | Extends    | JDK   | Serialization | Compatibility Changes |
|-----------|------------------|-------|----------------|------------|-------|---------------|-----------------------|
| Unchanged | `final` `public` | Class | `UnsignedLong` | [`Number`] | JDK 6 | ![Compatible] | ![No changes]         |


#### Methods

| Status    | Modifiers | Generics | Type             | Method                    | Annotations              | Throws | Compatibility Changes |
|-----------|-----------|----------|------------------|---------------------------|--------------------------|--------|-----------------------|
| Unchanged | `public`  |          | [`UnsignedLong`] | `minus`([`UnsignedLong`]) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `public`  |          | [`UnsignedLong`] | `plus`([`UnsignedLong`])  | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.primitives.unsignedlongs"></a>
### `com.google.common.primitives.UnsignedLongs`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers        | Type  | Name            | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|------------------|-------|-----------------|------------|-------|---------------------|-----------------------|
| Unchanged | `final` `public` | Class | `UnsignedLongs` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status    | Modifiers         | Generics | Type           | Method                        | Annotations              | Throws | Compatibility Changes |
|-----------|-------------------|----------|----------------|-------------------------------|--------------------------|--------|-----------------------|
| Unchanged | `static` `public` |          | `int`          | `compare`(`long`, `long`)     | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `long`         | `divide`(`long`, `long`)      | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`String`]     | `join`([`String`], `long...`) | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`Comparator`] | `lexicographicalComparator`() | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `long`         | `max`(`long...`)              | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `long`         | `min`(`long...`)              | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | `long`         | `remainder`(`long`, `long`)   | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`String`]     | `toString`(`long`)            | **[`CheckReturnValue`]** |        | ![Annotation added]   |
| Unchanged | `static` `public` |          | [`String`]     | `toString`(`long`, `int`)     | **[`CheckReturnValue`]** |        | ![Annotation added]   |

___

<a id="user-content-com.google.common.reflect.classpath$resourceinfo"></a>
### `com.google.common.reflect.ClassPath$ResourceInfo`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers         | Type  | Name           | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-------------------|-------|----------------|------------|-------|---------------------|-----------------------|
| Modified | `static` `public` | Class | `ResourceInfo` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers        | Generics | Type    | Method  | Annotations | Throws                         | Compatibility Changes |
|----------|------------------|----------|---------|---------|-------------|--------------------------------|-----------------------|
| Modified | `final` `public` |          | [`URL`] | `url`() |             | **[`NoSuchElementException`]** | ![No changes]         |

___

<a id="user-content-com.google.common.reflect.typetoken"></a>
### `com.google.common.reflect.TypeToken`

- [X] Binary-compatible
- [X] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers           | Type  | Name        | Extends            | JDK   | Serialization                       | Compatibility Changes |
|----------|---------------------|-------|-------------|--------------------|-------|-------------------------------------|-----------------------|
| Modified | `public` `abstract` | Class | `TypeToken` | [`TypeCapture<T>`] | JDK 6 | ![Default serialversionuid changed] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `T`  | [`Object`] | ![No changes]         |


#### Methods

| Status    | Modifiers                | Generics | Type          | Method                                | Annotations        | Throws | Compatibility Changes |
|-----------|--------------------------|----------|---------------|---------------------------------------|--------------------|--------|-----------------------|
| Unchanged | `final` `public`         |          | `boolean`     | `isAssignableFrom`([`TypeToken<?>`])  | **[`Deprecated`]** |        | ![Annotation deprecated added] |
| Unchanged | `final` `public`         |          | `boolean`     | `isAssignableFrom`([`Type`])          | **[`Deprecated`]** |        | ![Annotation deprecated added] |
| Added     | **`final`** **`public`** |          | **`boolean`** | **`isSubtypeOf`**([`TypeToken<?>`])   |                    |        | ![Method added to public class] |
| Added     | **`final`** **`public`** |          | **`boolean`** | **`isSubtypeOf`**([`Type`])           |                    |        | ![Method added to public class] |
| Added     | **`final`** **`public`** |          | **`boolean`** | **`isSupertypeOf`**([`TypeToken<?>`]) |                    |        | ![Method added to public class] |
| Added     | **`final`** **`public`** |          | **`boolean`** | **`isSupertypeOf`**([`Type`])         |                    |        | ![Method added to public class] |

___

<a id="user-content-com.google.common.util.concurrent.abstractfuture"></a>
### `com.google.common.util.concurrent.AbstractFuture`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Class | `AbstractFuture` | [`Object`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers       | Generics | Type          | Method                                             | Annotations | Throws | Compatibility Changes |
|--------|-----------------|----------|---------------|----------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`protected`** |          | **`boolean`** | **`setFuture`**([`ListenableFuture<? extends V>`]) |             |        | ![No changes]         |

___

<a id="user-content-com.google.common.util.concurrent.abstractlisteningexecutorservice"></a>
### `com.google.common.util.concurrent.AbstractListeningExecutorService`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name                               | Extends                     | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-------|------------------------------------|-----------------------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Class | `AbstractListeningExecutorService` | [`AbstractExecutorService`] | JDK 6 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers           | Generics                 | Type                                                             | Method                          | Annotations | Throws | Compatibility Changes |
|----------|---------------------|--------------------------|------------------------------------------------------------------|---------------------------------|-------------|--------|-----------------------|
| Modified | `final` `protected` | \<[`T extends Object`]\> | ~~[`ListenableFutureTask<T>`]~~ &rarr; **[`RunnableFuture<T>`]** | `newTaskFor`([`Runnable`], `T`) |             |        | ![Method return type changed] |
| Modified | `final` `protected` | \<[`T extends Object`]\> | ~~[`ListenableFutureTask<T>`]~~ &rarr; **[`RunnableFuture<T>`]** | `newTaskFor`([`Callable<T>`])   |             |        | ![Method return type changed] |

___

<a id="user-content-com.google.common.util.concurrent.futurefallback"></a>
### `com.google.common.util.concurrent.FutureFallback`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status    | Modifiers           | Type      | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|-----------|---------------------|-----------|------------------|------------|-------|---------------------|-----------------------|
| Unchanged | `public` `abstract` | Interface | `FutureFallback` | [`Object`] | JDK 6 | ![Not serializable] | ![Annotation deprecated added] |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Annotations

| Status | Annotation         | Compatibility Changes |
|--------|--------------------|-----------------------|
| Added  | **[`Deprecated`]** | ![No changes]         |

___

<a id="user-content-com.google.common.util.concurrent.futures"></a>
### `com.google.common.util.concurrent.Futures`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name      | Extends                                                        | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|-----------|----------------------------------------------------------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `Futures` | ~~[`Object`]~~ &rarr; **[`GwtFuturesCatchingSpecialization`]** | JDK 6 | ![Not serializable] | ![Superclass added]   |


#### Methods

| Status    | Modifiers                 | Generics                                                  | Type                                   | Method                                                   | Annotations                                     | Throws            | Compatibility Changes |
|-----------|---------------------------|-----------------------------------------------------------|----------------------------------------|----------------------------------------------------------|-------------------------------------------------|-------------------|-----------------------|
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<List<V>>`]          | `allAsList`([`ListenableFuture...<? extends V>`])        | **[`CheckReturnValue`]**<br>**[`SafeVarargs`]** |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<List<V>>`]          | `allAsList`([`Iterable<? extends ListenableFuture<? extends V>>`]) | **[`CheckReturnValue`]**              |                   | ![Annotation added]   |
| Added     | **`static`** **`public`** | \<**[`V extends Object`]**, **[`X extends Throwable`]**\> | **[`ListenableFuture<V>`]**            | **`catching`**([`ListenableFuture<? extends V>`], [`Class<X>`], [`Function<? super X, ? extends V>`]) | **[`CheckReturnValue`]** |  | ![Method added to public class] ![Annotation added] |
| Added     | **`static`** **`public`** | \<**[`V extends Object`]**, **[`X extends Throwable`]**\> | **[`ListenableFuture<V>`]**            | **`catching`**([`ListenableFuture<? extends V>`], [`Class<X>`], [`Function<? super X, ? extends V>`], [`Executor`]) | **[`CheckReturnValue`]** |  | ![Method added to public class] ![Annotation added] |
| Added     | **`static`** **`public`** | \<**[`V extends Object`]**, **[`X extends Throwable`]**\> | **[`ListenableFuture<V>`]**            | **`catchingAsync`**([`ListenableFuture<? extends V>`], [`Class<X>`], [`AsyncFunction<? super X, ? extends V>`]) |  |           | ![Method added to public class] |
| Added     | **`static`** **`public`** | \<**[`V extends Object`]**, **[`X extends Throwable`]**\> | **[`ListenableFuture<V>`]**            | **`catchingAsync`**([`ListenableFuture<? extends V>`], [`Class<X>`], [`AsyncFunction<? super X, ? extends V>`], [`Executor`]) |  |  | ![Method added to public class] |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<V>`]                | `dereference`([`ListenableFuture<? extends ListenableFuture<? extends V>>`]) | **[`CheckReturnValue`]**    |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`], [`X extends Exception`]\>         | [`Object`]                             | `get`([`Future<V>`], [`Class<X>`])                       | **[`Deprecated`]**                              | [`Exception`]     | ![Annotation deprecated added] |
| Unchanged | `static` `public`         | \<[`V extends Object`], [`X extends Exception`]\>         | [`Object`]                             | `get`([`Future<V>`], `long`, [`TimeUnit`], [`Class<X>`]) | **[`Deprecated`]**                              | [`Exception`]     | ![Annotation deprecated added] |
| Added     | **`static`** **`public`** | \<**[`V extends Object`]**, **[`X extends Exception`]**\> | **[`Object`]**                         | **`getChecked`**([`Future<V>`], [`Class<X>`])            |                                                 | **[`Exception`]** | ![Method added to public class] |
| Added     | **`static`** **`public`** | \<**[`V extends Object`]**, **[`X extends Exception`]**\> | **[`Object`]**                         | **`getChecked`**([`Future<V>`], [`Class<X>`], `long`, [`TimeUnit`]) |                                      | **[`Exception`]** | ![Method added to public class] |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<V>`]                | `immediateCancelledFuture`()                             | **[`CheckReturnValue`]**                        |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`], [`X extends Exception`]\>         | [`CheckedFuture<V, X>`]                | `immediateCheckedFuture`(`V`)                            | **[`CheckReturnValue`]**                        |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`], [`X extends Exception`]\>         | [`CheckedFuture<V, X>`]                | `immediateFailedCheckedFuture`(`X`)                      | **[`CheckReturnValue`]**                        |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<V>`]                | `immediateFailedFuture`([`Throwable`])                   | **[`CheckReturnValue`]**                        |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<V>`]                | `immediateFuture`(`V`)                                   | **[`CheckReturnValue`]**                        |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`T extends Object`]\>                                  | [`ImmutableList<ListenableFuture<T>>`] | `inCompletionOrder`([`Iterable<? extends ListenableFuture<? extends T>>`]) | **[`CheckReturnValue`]**      |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`I extends Object`], [`O extends Object`]\>            | [`Future<O>`]                          | `lazyTransform`([`Future<I>`], [`Function<? super I, ? extends O>`]) | **[`CheckReturnValue`]**            |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`], [`X extends Exception`]\>         | [`CheckedFuture<V, X>`]                | `makeChecked`([`ListenableFuture<V>`], [`Function<? super Exception, X>`]) | **[`CheckReturnValue`]**      |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<V>`]                | `nonCancellationPropagating`([`ListenableFuture<V>`])    | **[`CheckReturnValue`]**                        |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<List<V>>`]          | `successfulAsList`([`ListenableFuture...<? extends V>`]) | **[`CheckReturnValue`]**<br>**[`SafeVarargs`]** |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<List<V>>`]          | `successfulAsList`([`Iterable<? extends ListenableFuture<? extends V>>`]) | **[`CheckReturnValue`]**       |                   | ![Annotation added]   |
| Unchanged | `static` `public`         | \<[`I extends Object`], [`O extends Object`]\>            | [`ListenableFuture<O>`]                | `transform`([`ListenableFuture<I>`], [`AsyncFunction<? super I, ? extends O>`]) | **[`Deprecated`]**       |                   | ![Annotation deprecated added] |
| Unchanged | `static` `public`         | \<[`I extends Object`], [`O extends Object`]\>            | [`ListenableFuture<O>`]                | `transform`([`ListenableFuture<I>`], [`AsyncFunction<? super I, ? extends O>`], [`Executor`]) | **[`Deprecated`]** |           | ![Annotation deprecated added] |
| Added     | **`static`** **`public`** | \<**[`I extends Object`]**, **[`O extends Object`]**\>    | **[`ListenableFuture<O>`]**            | **`transformAsync`**([`ListenableFuture<I>`], [`AsyncFunction<? super I, ? extends O>`]) |                 |                   | ![Method added to public class] |
| Added     | **`static`** **`public`** | \<**[`I extends Object`]**, **[`O extends Object`]**\>    | **[`ListenableFuture<O>`]**            | **`transformAsync`**([`ListenableFuture<I>`], [`AsyncFunction<? super I, ? extends O>`], [`Executor`]) |   |                   | ![Method added to public class] |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<V>`]                | `withFallback`([`ListenableFuture<? extends V>`], [`FutureFallback<? extends V>`]) | **[`CheckReturnValue`]**<br>**[`Deprecated`]** |  | ![Annotation added] ![Annotation deprecated added] |
| Unchanged | `static` `public`         | \<[`V extends Object`]\>                                  | [`ListenableFuture<V>`]                | `withFallback`([`ListenableFuture<? extends V>`], [`FutureFallback<? extends V>`], [`Executor`]) | **[`CheckReturnValue`]**<br>**[`Deprecated`]** |  | ![Annotation added] ![Annotation deprecated added] |
| Added     | **`static`** **`public`** | \<**[`V extends Object`]**\>                              | **[`ListenableFuture<V>`]**            | **`withTimeout`**([`ListenableFuture<V>`], `long`, [`TimeUnit`], [`ScheduledExecutorService`]) | **[`CheckReturnValue`]** |    | ![Method added to public class] ![Annotation added] |

___

<a id="user-content-com.google.common.util.concurrent.settablefuture"></a>
### `com.google.common.util.concurrent.SettableFuture`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type  | Name             | Extends                                                   | JDK   | Serialization       | Compatibility Changes |
|----------|------------------|-------|------------------|-----------------------------------------------------------|-------|---------------------|-----------------------|
| Modified | `final` `public` | Class | `SettableFuture` | ~~[`AbstractFuture<V>`]~~ &rarr; **[`TrustedFuture<V>`]** | JDK 6 | ![Not serializable] | ![Superclass added]   |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `V`  | [`Object`] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type          | Method                                             | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|---------------|----------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`boolean`** | **`setFuture`**([`ListenableFuture<? extends V>`]) |             |        | ![Method added to public class] |


</details>


___

*Generated on: 2024-07-29 17:08:40.539+0000*.

[1]: # "java.lang.Object[]"
[2]: # "com.google.common.base.Objects$ToStringHelper"
[3]: # "com.google.common.collect.ImmutableBiMap$Builder<K, V>"
[4]: # "com.google.common.collect.ImmutableMultimap$Builder<K extends java.lang.Object, V extends java.lang.Object>"
[5]: # "com.google.common.collect.ImmutableListMultimap$Builder<K, V>"
[6]: # "com.google.common.collect.ImmutableMap<K, V>"
[7]: # "com.google.common.collect.ImmutableMap$Builder<K, V>"
[8]: # "com.google.common.collect.ImmutableMultimap<K, V>"
[9]: # "com.google.common.collect.ImmutableMultimap$Builder<K, V>"
[10]: # "com.google.common.collect.ImmutableSetMultimap$Builder<K, V>"
[11]: # "com.google.common.collect.ImmutableSortedMap$Builder<K, V>"
[Annotation added]: https://img.shields.io/badge/Annotation_added-yellow "Annotation added"
[Annotation deprecated added]: https://img.shields.io/badge/Annotation_deprecated_added-orange "Annotation deprecated added"
[Annotation removed]: https://img.shields.io/badge/Annotation_removed-yellow "Annotation removed"
[Class generic template generics changed]: https://img.shields.io/badge/Class_generic_template_generics_changed-orange "Class generic template generics changed"
[Class now final]: https://img.shields.io/badge/Class_now_final-red "Class now final"
[Compatible]: https://img.shields.io/badge/Compatible-green "Compatible"
[Default serialversionuid changed]: https://img.shields.io/badge/Incompatible-red "Default serialversionuid changed"
[Interface added]: https://img.shields.io/badge/Interface_added-orange "Interface added"
[Method added to interface]: https://img.shields.io/badge/Method_added_to_interface-orange "Method added to interface"
[Method added to public class]: https://img.shields.io/badge/Method_added_to_public_class-yellow "Method added to public class"
[Method less accessible]: https://img.shields.io/badge/Method_less_accessible-red "Method less accessible"
[Method now abstract]: https://img.shields.io/badge/Method_now_abstract-red "Method now abstract"
[Method removed]: https://img.shields.io/badge/Method_removed-red "Method removed"
[Method return type changed]: https://img.shields.io/badge/Method_return_type_changed-red "Method return type changed"
[Method return type generics changed]: https://img.shields.io/badge/Method_return_type_generics_changed-orange "Method return type generics changed"
[No changes]: https://img.shields.io/badge/No_changes-green "No changes"
[Not serializable]: https://img.shields.io/badge/Not_serializable-green "Not serializable"
[Superclass added]: https://img.shields.io/badge/Superclass_added-orange "Superclass added"
[Superclass modified]: https://img.shields.io/badge/Incompatible-red "Superclass modified"
[`AbstractCollection<E>`]: # "java.util.AbstractCollection<E extends java.lang.Object>"
[`AbstractExecutorService`]: # "java.util.concurrent.AbstractExecutorService"
[`AbstractFuture<V>`]: # "com.google.common.util.concurrent.AbstractFuture<V extends java.lang.Object>"
[`AbstractMap<K, V>`]: # "java.util.AbstractMap<K extends java.lang.Object, V extends java.lang.Object>"
[`AbstractMultimap<K, V>`]: # "com.google.common.collect.AbstractMultimap<K extends java.lang.Object, V extends java.lang.Object>"
[`AbstractRangeSet<C>`]: # "com.google.common.collect.AbstractRangeSet<C extends java.lang.Comparable>"
[`AsyncFunction<? super I, ? extends O>`]: # "com.google.common.util.concurrent.AsyncFunction<? super I, ? extends O>"
[`AsyncFunction<? super X, ? extends V>`]: # "com.google.common.util.concurrent.AsyncFunction<? super X, ? extends V>"
[`B extends Object`]: # "B extends java.lang.Object"
[`BiMap<K, V>`]: # "com.google.common.collect.BiMap<K, V>"
[`BloomFilter<T>`]: # "com.google.common.hash.BloomFilter<T>"
[`Boolean`]: # "java.lang.Boolean"
[`Builder<E>`]: # "com.google.common.collect.ImmutableSortedMultiset$Builder<E>"
[`Builder<K, V>`]: # "com.google.common.collect.ImmutableMap$Builder<K extends java.lang.Object, V extends java.lang.Object>"
[`Callable<T>`]: # "java.util.concurrent.Callable<T>"
[`CharMatcher`]: # "com.google.common.base.CharMatcher"
[`CharSequence`]: # "java.lang.CharSequence"
[`CheckForNull`]: # "javax.annotation.CheckForNull"
[`CheckReturnValue`]: # "javax.annotation.CheckReturnValue"
[`CheckedFuture<V, X>`]: # "com.google.common.util.concurrent.CheckedFuture<V, X>"
[`Class<?>`]: # "java.lang.Class<?>"
[`Class<E>`]: # "java.lang.Class<E>"
[`Class<T>`]: # "java.lang.Class<T>"
[`Class<X>`]: # "java.lang.Class<X>"
[`Collection<?>`]: # "java.util.Collection<?>"
[`Collection<E>`]: # "java.util.Collection<E>"
[`Comparable<?>`]: # "java.lang.Comparable<?>"
[`Comparable`]: # "java.lang.Comparable"
[`Comparator<? super E>`]: # "java.util.Comparator<? super E>"
[`Comparator<? super K>`]: # "java.util.Comparator<? super K>"
[`Comparator<? super V>`]: # "java.util.Comparator<? super V>"
[`Comparator`]: # "java.util.Comparator"
[`ComparisonChain`]: # "com.google.common.collect.ComparisonChain"
[`Deprecated`]: # "java.lang.Deprecated"
[`Double`]: # "java.lang.Double"
[`E extends Comparable<?>`]: # "E extends java.lang.Comparable<?>"
[`E extends Comparable<E>`]: # "E extends java.lang.Comparable<E>"
[`E extends Object`]: # "E extends java.lang.Object"
[`Entry<? extends K, ? extends V>`]: # "java.util.Map$Entry<? extends K, ? extends V>"
[`Enum<E>`]: # "java.lang.Enum<E extends java.lang.Enum<E>>"
[`EventBus`]: # "com.google.common.eventbus.EventBus"
[`Exception`]: # "java.lang.Exception"
[`Executor`]: # "java.util.concurrent.Executor"
[`F extends Object`]: # "F extends java.lang.Object"
[`FilterInputStream`]: # "java.io.FilterInputStream"
[`FilterOutputStream`]: # "java.io.FilterOutputStream"
[`Float`]: # "java.lang.Float"
[`FluentIterable<E>`]: # "com.google.common.collect.FluentIterable<E>"
[`FluentIterable<T>`]: # "com.google.common.collect.FluentIterable<T>"
[`ForwardingMap<K, V>`]: # "com.google.common.collect.ForwardingMap<K extends java.lang.Object, V extends java.lang.Object>"
[`Function<? super E, ? extends Iterable<? extends T>>`]: # "com.google.common.base.Function<? super E, ? extends java.lang.Iterable<? extends T>>"
[`Function<? super E, K>`]: # "com.google.common.base.Function<? super E, K>"
[`Function<? super E, T>`]: # "com.google.common.base.Function<? super E, T>"
[`Function<? super E, V>`]: # "com.google.common.base.Function<? super E, V>"
[`Function<? super Exception, X>`]: # "com.google.common.base.Function<? super java.lang.Exception, X>"
[`Function<? super F, ? extends T>`]: # "com.google.common.base.Function<? super F, ? extends T>"
[`Function<? super I, ? extends O>`]: # "com.google.common.base.Function<? super I, ? extends O>"
[`Function<? super X, ? extends V>`]: # "com.google.common.base.Function<? super X, ? extends V>"
[`Funnel<? super T>`]: # "com.google.common.hash.Funnel<? super T>"
[`Funnel<T>`]: # "com.google.common.hash.Funnel<T>"
[`Future<I>`]: # "java.util.concurrent.Future<I>"
[`Future<O>`]: # "java.util.concurrent.Future<O>"
[`Future<V>`]: # "java.util.concurrent.Future<V>"
[`FutureFallback<? extends V>`]: # "com.google.common.util.concurrent.FutureFallback<? extends V>"
[`GenericMapMaker<K0, V0>`]: # "com.google.common.collect.GenericMapMaker<K0 extends java.lang.Object, V0 extends java.lang.Object>"
[`GwtFuturesCatchingSpecialization`]: # "com.google.common.util.concurrent.GwtFuturesCatchingSpecialization"
[`HashCode`]: # "com.google.common.hash.HashCode"
[`HashFunction...`]: # "com.google.common.hash.HashFunction..."
[`HashFunction`]: # "com.google.common.hash.HashFunction"
[`I extends Object`]: # "I extends java.lang.Object"
[`IOException`]: # "java.io.IOException"
[`ImmutableBiMap<K, V>`]: # "com.google.common.collect.ImmutableBiMap<K, V>"
[`ImmutableClassToInstanceMap<B>`]: # "com.google.common.collect.ImmutableClassToInstanceMap<B>"
[`ImmutableCollection<E>`]: # "com.google.common.collect.ImmutableCollection<E extends java.lang.Object>"
[`ImmutableCollection<V>`]: # "com.google.common.collect.ImmutableCollection<V>"
[`ImmutableList<E>`]: # "com.google.common.collect.ImmutableList<E>"
[`ImmutableList<ListenableFuture<T>>`]: # "com.google.common.collect.ImmutableList<com.google.common.util.concurrent.ListenableFuture<T>>"
[`ImmutableListMultimap<K, E>`]: # "com.google.common.collect.ImmutableListMultimap<K, E>"
[`ImmutableListMultimap<K, V>`]: # "com.google.common.collect.ImmutableListMultimap<K, V>"
[`ImmutableMap<E, V>`]: # "com.google.common.collect.ImmutableMap<E, V>"
[`ImmutableMap<K, E>`]: # "com.google.common.collect.ImmutableMap<K, E>"
[`ImmutableMap<K, V>`]: # "com.google.common.collect.ImmutableMap<K extends java.lang.Object, V extends java.lang.Object>"
[`ImmutableMap<Range<K>, V>`]: # "com.google.common.collect.ImmutableMap<com.google.common.collect.Range<K>, V>"
[`ImmutableMultimap<K, V>`]: # "com.google.common.collect.ImmutableMultimap<K extends java.lang.Object, V extends java.lang.Object>"
[`ImmutableMultiset<E>`]: # "com.google.common.collect.ImmutableMultiset<E>"
[`ImmutableSet<E>`]: # "com.google.common.collect.ImmutableSet<E>"
[`ImmutableSet<Range<C>>`]: # "com.google.common.collect.ImmutableSet<com.google.common.collect.Range<C>>"
[`ImmutableSetMultimap<K, V>`]: # "com.google.common.collect.ImmutableSetMultimap<K, V>"
[`ImmutableSortedMap<K, V>`]: # "com.google.common.collect.ImmutableSortedMap<K, V>"
[`ImmutableSortedMapFauxverideShim<K, V>`]: # "com.google.common.collect.ImmutableSortedMapFauxverideShim<K extends java.lang.Object, V extends java.lang.Object>"
[`ImmutableSortedSet<E>`]: # "com.google.common.collect.ImmutableSortedSet<E>"
[`ImmutableSortedSet<K>`]: # "com.google.common.collect.ImmutableSortedSet<K>"
[`ImmutableSortedSetFauxverideShim<E>`]: # "com.google.common.collect.ImmutableSortedSetFauxverideShim<E extends java.lang.Object>"
[`ImmutableSorted…etFauxverideShim<E>`]: # "com.google.common.collect.ImmutableSortedMultisetFauxverideShim<E extends java.lang.Object>"
[`InputStream`]: # "java.io.InputStream"
[`Integer`]: # "java.lang.Integer"
[`Iterable<? extends Entry<? extends ?, ? extends ?>>`]: # "java.lang.Iterable<? extends java.util.Map$Entry<? extends ?, ? extends ?>>"
[`Iterable<? extends Entry<? extends K, ? extends V>>`]: # "java.lang.Iterable<? extends java.util.Map$Entry<? extends K, ? extends V>>"
[`Iterable<? extends ListenableFuture<? extends T>>`]: # "java.lang.Iterable<? extends com.google.common.util.concurrent.ListenableFuture<? extends T>>"
[`Iterable<? extends ListenableFuture<? extends V>>`]: # "java.lang.Iterable<? extends com.google.common.util.concurrent.ListenableFuture<? extends V>>"
[`Iterable<?>`]: # "java.lang.Iterable<?>"
[`Iterable<E>`]: # "java.lang.Iterable<E>"
[`Iterable<F>`]: # "java.lang.Iterable<F>"
[`Iterable<HashFunction>`]: # "java.lang.Iterable<com.google.common.hash.HashFunction>"
[`Iterable<String>`]: # "java.lang.Iterable<java.lang.String>"
[`Iterable<T>`]: # "java.lang.Iterable<T>"
[`Iterator<? extends Entry<? extends ?, ? extends ?>>`]: # "java.util.Iterator<? extends java.util.Map$Entry<? extends ?, ? extends ?>>"
[`Iterator<?>`]: # "java.util.Iterator<?>"
[`Iterator<T>`]: # "java.util.Iterator<T>"
[`IteratorBasedAbstractMap<K, V>`]: # "com.google.common.collect.Maps$IteratorBasedAbstractMap<K extends java.lang.Object, V extends java.lang.Object>"
[`Joiner`]: # "com.google.common.base.Joiner"
[`K extends Object`]: # "K extends java.lang.Object"
[`LinkedHashMap<K, V>`]: # "java.util.LinkedHashMap<K, V>"
[`List...<? extends B>`]: # "java.util.List...<? extends B>"
[`List<? extends List<? extends B>>`]: # "java.util.List<? extends java.util.List<? extends B>>"
[`List<F>`]: # "java.util.List<F>"
[`List<List<B>>`]: # "java.util.List<java.util.List<B>>"
[`List<StackTraceElement>`]: # "java.util.List<java.lang.StackTraceElement>"
[`List<String>`]: # "java.util.List<java.lang.String>"
[`List<T>`]: # "java.util.List<T>"
[`List<Throwable>`]: # "java.util.List<java.lang.Throwable>"
[`ListMultimap<K, V>`]: # "com.google.common.collect.ListMultimap<K, V>"
[`ListenableFuture...<? extends V>`]: # "com.google.common.util.concurrent.ListenableFuture...<? extends V>"
[`ListenableFuture<? extends ListenableFuture<? extends V>>`]: # "com.google.common.util.concurrent.ListenableFuture<? extends com.google.common.util.concurrent.ListenableFuture<? extends V>>"
[`ListenableFuture<? extends V>`]: # "com.google.common.util.concurrent.ListenableFuture<? extends V>"
[`ListenableFuture<I>`]: # "com.google.common.util.concurrent.ListenableFuture<I>"
[`ListenableFuture<List<V>>`]: # "com.google.common.util.concurrent.ListenableFuture<java.util.List<V>>"
[`ListenableFuture<O>`]: # "com.google.common.util.concurrent.ListenableFuture<O>"
[`ListenableFuture<V>`]: # "com.google.common.util.concurrent.ListenableFuture<V>"
[`ListenableFutureTask<T>`]: # "com.google.common.util.concurrent.ListenableFutureTask<T>"
[`Long`]: # "java.lang.Long"
[`Map<?, ?>`]: # "java.util.Map<?, ?>"
[`Map<E, Boolean>`]: # "java.util.Map<E, java.lang.Boolean>"
[`Map<K, V>`]: # "java.util.Map<K, V>"
[`Map<Range<K>, V>`]: # "java.util.Map<com.google.common.collect.Range<K>, V>"
[`Map<String, String>`]: # "java.util.Map<java.lang.String, java.lang.String>"
[`MapMaker`]: # "com.google.common.collect.MapMaker"
[`MediaType`]: # "com.google.common.net.MediaType"
[`Multimap<K, V>`]: # "com.google.common.collect.Multimap<K, V>"
[`Multiset<?>`]: # "com.google.common.collect.Multiset<?>"
[`Multiset<E>`]: # "com.google.common.collect.Multiset<E>"
[`NavigableMap<K, V>`]: # "java.util.NavigableMap<K, V>"
[`NavigableSet<E>`]: # "java.util.NavigableSet<E>"
[`NoSuchElementException`]: # "java.util.NoSuchElementException"
[`Nullable`]: # "javax.annotation.Nullable"
[`Number`]: # "java.lang.Number"
[`O extends Object`]: # "O extends java.lang.Object"
[`Object...`]: # "java.lang.Object..."
[`Object`]: # "java.lang.Object"
[`Optional<E>`]: # "com.google.common.base.Optional<E>"
[`Optional<Long>`]: # "com.google.common.base.Optional<java.lang.Long>"
[`OutputStream`]: # "java.io.OutputStream"
[`Pattern`]: # "java.util.regex.Pattern"
[`Predicate<? super E>`]: # "com.google.common.base.Predicate<? super E>"
[`Predicate<? super Entry<? super K, ? super V>>`]: # "com.google.common.base.Predicate<? super java.util.Map$Entry<? super K, ? super V>>"
[`Predicate<? super K>`]: # "com.google.common.base.Predicate<? super K>"
[`Predicate<? super T>`]: # "com.google.common.base.Predicate<? super T>"
[`Predicate<? super V>`]: # "com.google.common.base.Predicate<? super V>"
[`RangeMap<K, V>`]: # "com.google.common.collect.RangeMap<K extends java.lang.Comparable, V extends java.lang.Object>"
[`RangeSet<C>`]: # "com.google.common.collect.RangeSet<C extends java.lang.Comparable>"
[`Reader`]: # "java.io.Reader"
[`RemovalCause`]: # "com.google.common.cache.RemovalCause"
[`RemovalNotification<K, V>`]: # "com.google.common.cache.RemovalNotification<K, V>"
[`RunnableFuture<T>`]: # "java.util.concurrent.RunnableFuture<T>"
[`Runnable`]: # "java.lang.Runnable"
[`RuntimeException`]: # "java.lang.RuntimeException"
[`SafeVarargs`]: # "java.lang.SafeVarargs"
[`ScheduledExecutorService`]: # "java.util.concurrent.ScheduledExecutorService"
[`Serializable`]: # "java.io.Serializable"
[`Set<E>`]: # "java.util.Set<E>"
[`Set<Entry<K, V>>`]: # "java.util.Set<java.util.Map$Entry<K, V>>"
[`Set<Range<C>>`]: # "java.util.Set<com.google.common.collect.Range<C>>"
[`SetMultimap<K, V>`]: # "com.google.common.collect.SetMultimap<K, V>"
[`Set`]: # "java.util.Set"
[`SortedMap<K, V>`]: # "java.util.SortedMap<K, V>"
[`SortedSet<E>`]: # "java.util.SortedSet<E>"
[`Splitter`]: # "com.google.common.base.Splitter"
[`Stopwatch`]: # "com.google.common.base.Stopwatch"
[`String`]: # "java.lang.String"
[`T extends Object`]: # "T extends java.lang.Object"
[`Throwable`]: # "java.lang.Throwable"
[`Ticker`]: # "com.google.common.base.Ticker"
[`TimeUnit`]: # "java.util.concurrent.TimeUnit"
[`ToStringHelper`]: # "com.google.common.base.MoreObjects$ToStringHelper"
[`TrustedFuture<V>`]: # "com.google.common.util.concurrent.AbstractFuture$TrustedFuture<V extends java.lang.Object>"
[`TypeCapture<T>`]: # "com.google.common.reflect.TypeCapture<T extends java.lang.Object>"
[`TypeToken<?>`]: # "com.google.common.reflect.TypeToken<?>"
[`Type`]: # "java.lang.reflect.Type"
[`URL`]: # "java.net.URL"
[`UnmodifiableIterator<T>`]: # "com.google.common.collect.UnmodifiableIterator<T>"
[`UnsignedInteger`]: # "com.google.common.primitives.UnsignedInteger"
[`UnsignedLong`]: # "com.google.common.primitives.UnsignedLong"
[`UnsupportedOperationException`]: # "java.lang.UnsupportedOperationException"
[`V extends Object`]: # "V extends java.lang.Object"
[`Writer`]: # "java.io.Writer"
[`X extends Exception`]: # "X extends java.lang.Exception"
[`X extends Throwable`]: # "X extends java.lang.Throwable"
[com.google.common.base.Ascii]: #user-content-com.google.common.base.ascii
[com.google.common.base.CaseFormat]: #user-content-com.google.common.base.caseformat
[com.google.common.base.CharMatcher]: #user-content-com.google.common.base.charmatcher
[com.google.common.base.Defaults]: #user-content-com.google.common.base.defaults
[com.google.common.base.Enums]: #user-content-com.google.common.base.enums
[com.google.common.base.Equivalence]: #user-content-com.google.common.base.equivalence
[com.google.common.base.Functions]: #user-content-com.google.common.base.functions
[com.google.common.base.Joiner]: #user-content-com.google.common.base.joiner
[com.google.common.base.Joiner$MapJoiner]: #user-content-com.google.common.base.joiner$mapjoiner
[com.google.common.base.MoreObjects]: #user-content-com.google.common.base.moreobjects
[com.google.common.base.MoreObjects$ToStringHelper]: #user-content-com.google.common.base.moreobjects$tostringhelper
[com.google.common.base.Objects]: #user-content-com.google.common.base.objects
[com.google.common.base.Optional]: #user-content-com.google.common.base.optional
[com.google.common.base.Predicates]: #user-content-com.google.common.base.predicates
[com.google.common.base.Splitter]: #user-content-com.google.common.base.splitter
[com.google.common.base.Splitter$MapSplitter]: #user-content-com.google.common.base.splitter$mapsplitter
[com.google.common.base.StandardSystemProperty]: #user-content-com.google.common.base.standardsystemproperty
[com.google.common.base.Stopwatch]: #user-content-com.google.common.base.stopwatch
[com.google.common.base.Strings]: #user-content-com.google.common.base.strings
[com.google.common.base.Suppliers]: #user-content-com.google.common.base.suppliers
[com.google.common.base.Throwables]: #user-content-com.google.common.base.throwables
[com.google.common.base.Ticker]: #user-content-com.google.common.base.ticker
[com.google.common.base.Utf8]: #user-content-com.google.common.base.utf8
[com.google.common.base.VerifyException]: #user-content-com.google.common.base.verifyexception
[com.google.common.cache.CacheLoader$UnsupportedLoadingOperationException]: #user-content-com.google.common.cache.cacheloader$unsupportedloadingoperationexception
[com.google.common.cache.RemovalNotification]: #user-content-com.google.common.cache.removalnotification
[com.google.common.collect.BiMap]: #user-content-com.google.common.collect.bimap
[com.google.common.collect.Collections2]: #user-content-com.google.common.collect.collections2
[com.google.common.collect.ComparisonChain]: #user-content-com.google.common.collect.comparisonchain
[com.google.common.collect.FluentIterable]: #user-content-com.google.common.collect.fluentiterable
[com.google.common.collect.HashBiMap]: #user-content-com.google.common.collect.hashbimap
[com.google.common.collect.ImmutableBiMap]: #user-content-com.google.common.collect.immutablebimap
[com.google.common.collect.ImmutableBiMap$Builder]: #user-content-com.google.common.collect.immutablebimap$builder
[com.google.common.collect.ImmutableClassToInstanceMap]: #user-content-com.google.common.collect.immutableclasstoinstancemap
[com.google.common.collect.ImmutableCollection]: #user-content-com.google.common.collect.immutablecollection
[com.google.common.collect.ImmutableList]: #user-content-com.google.common.collect.immutablelist
[com.google.common.collect.ImmutableListMultimap]: #user-content-com.google.common.collect.immutablelistmultimap
[com.google.common.collect.ImmutableListMultimap$Builder]: #user-content-com.google.common.collect.immutablelistmultimap$builder
[com.google.common.collect.ImmutableMap]: #user-content-com.google.common.collect.immutablemap
[com.google.common.collect.ImmutableMap$Builder]: #user-content-com.google.common.collect.immutablemap$builder
[com.google.common.collect.ImmutableMultimap]: #user-content-com.google.common.collect.immutablemultimap
[com.google.common.collect.ImmutableMultimap$Builder]: #user-content-com.google.common.collect.immutablemultimap$builder
[com.google.common.collect.ImmutableMultiset]: #user-content-com.google.common.collect.immutablemultiset
[com.google.common.collect.ImmutableRangeMap]: #user-content-com.google.common.collect.immutablerangemap
[com.google.common.collect.ImmutableRangeSet]: #user-content-com.google.common.collect.immutablerangeset
[com.google.common.collect.ImmutableSetMultimap]: #user-content-com.google.common.collect.immutablesetmultimap
[com.google.common.collect.ImmutableSetMultimap$Builder]: #user-content-com.google.common.collect.immutablesetmultimap$builder
[com.google.common.collect.ImmutableSortedMap]: #user-content-com.google.common.collect.immutablesortedmap
[com.google.common.collect.ImmutableSortedMap$Builder]: #user-content-com.google.common.collect.immutablesortedmap$builder
[com.google.common.collect.ImmutableSortedMultiset]: #user-content-com.google.common.collect.immutablesortedmultiset
[com.google.common.collect.ImmutableSortedSet]: #user-content-com.google.common.collect.immutablesortedset
[com.google.common.collect.Iterables]: #user-content-com.google.common.collect.iterables
[com.google.common.collect.Iterators]: #user-content-com.google.common.collect.iterators
[com.google.common.collect.Lists]: #user-content-com.google.common.collect.lists
[com.google.common.collect.MapConstraint]: #user-content-com.google.common.collect.mapconstraint
[com.google.common.collect.MapConstraints]: #user-content-com.google.common.collect.mapconstraints
[com.google.common.collect.MapMaker]: #user-content-com.google.common.collect.mapmaker
[com.google.common.collect.Maps]: #user-content-com.google.common.collect.maps
[com.google.common.collect.MultimapBuilder]: #user-content-com.google.common.collect.multimapbuilder
[com.google.common.collect.Multimaps]: #user-content-com.google.common.collect.multimaps
[com.google.common.collect.Multisets]: #user-content-com.google.common.collect.multisets
[com.google.common.collect.RangeMap]: #user-content-com.google.common.collect.rangemap
[com.google.common.collect.RangeSet]: #user-content-com.google.common.collect.rangeset
[com.google.common.collect.Sets]: #user-content-com.google.common.collect.sets
[com.google.common.collect.Table]: #user-content-com.google.common.collect.table
[com.google.common.collect.Table$Cell]: #user-content-com.google.common.collect.table$cell
[com.google.common.collect.TreeRangeMap]: #user-content-com.google.common.collect.treerangemap
[com.google.common.collect.TreeRangeSet]: #user-content-com.google.common.collect.treerangeset
[com.google.common.eventbus.AsyncEventBus]: #user-content-com.google.common.eventbus.asynceventbus
[com.google.common.eventbus.DeadEvent]: #user-content-com.google.common.eventbus.deadevent
[com.google.common.eventbus.EventBus]: #user-content-com.google.common.eventbus.eventbus
[com.google.common.hash.BloomFilter]: #user-content-com.google.common.hash.bloomfilter
[com.google.common.hash.Funnels]: #user-content-com.google.common.hash.funnels
[com.google.common.hash.HashCode]: #user-content-com.google.common.hash.hashcode
[com.google.common.hash.Hasher]: #user-content-com.google.common.hash.hasher
[com.google.common.hash.Hashing]: #user-content-com.google.common.hash.hashing
[com.google.common.hash.HashingInputStream]: #user-content-com.google.common.hash.hashinginputstream
[com.google.common.hash.HashingOutputStream]: #user-content-com.google.common.hash.hashingoutputstream
[com.google.common.io.BaseEncoding]: #user-content-com.google.common.io.baseencoding
[com.google.common.io.ByteSource]: #user-content-com.google.common.io.bytesource
[com.google.common.io.CharSource]: #user-content-com.google.common.io.charsource
[com.google.common.net.HttpHeaders]: #user-content-com.google.common.net.httpheaders
[com.google.common.net.MediaType]: #user-content-com.google.common.net.mediatype
[com.google.common.primitives.Booleans]: #user-content-com.google.common.primitives.booleans
[com.google.common.primitives.Bytes]: #user-content-com.google.common.primitives.bytes
[com.google.common.primitives.Chars]: #user-content-com.google.common.primitives.chars
[com.google.common.primitives.Doubles]: #user-content-com.google.common.primitives.doubles
[com.google.common.primitives.Floats]: #user-content-com.google.common.primitives.floats
[com.google.common.primitives.Ints]: #user-content-com.google.common.primitives.ints
[com.google.common.primitives.Longs]: #user-content-com.google.common.primitives.longs
[com.google.common.primitives.Primitives]: #user-content-com.google.common.primitives.primitives
[com.google.common.primitives.Shorts]: #user-content-com.google.common.primitives.shorts
[com.google.common.primitives.SignedBytes]: #user-content-com.google.common.primitives.signedbytes
[com.google.common.primitives.UnsignedBytes]: #user-content-com.google.common.primitives.unsignedbytes
[com.google.common.primitives.UnsignedInteger]: #user-content-com.google.common.primitives.unsignedinteger
[com.google.common.primitives.UnsignedInts]: #user-content-com.google.common.primitives.unsignedints
[com.google.common.primitives.UnsignedLong]: #user-content-com.google.common.primitives.unsignedlong
[com.google.common.primitives.UnsignedLongs]: #user-content-com.google.common.primitives.unsignedlongs
[com.google.common.reflect.ClassPath$ResourceInfo]: #user-content-com.google.common.reflect.classpath$resourceinfo
[com.google.common.reflect.TypeToken]: #user-content-com.google.common.reflect.typetoken
[com.google.common.util.concurrent.AbstractFuture]: #user-content-com.google.common.util.concurrent.abstractfuture
[com.google.common.util.concurrent.AbstractListeningExecutorService]: #user-content-com.google.common.util.concurrent.abstractlisteningexecutorservice
[com.google.common.util.concurrent.FutureFallback]: #user-content-com.google.common.util.concurrent.futurefallback
[com.google.common.util.concurrent.Futures]: #user-content-com.google.common.util.concurrent.futures
[com.google.common.util.concurrent.SettableFuture]: #user-content-com.google.common.util.concurrent.settablefuture
