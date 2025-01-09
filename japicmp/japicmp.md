
# Compatibility Report

![semver MAJOR](https://img.shields.io/badge/semver-MAJOR-red?logo=semver "semver MAJOR")

## Summary

> [!CAUTION]
>
> Incompatible changes found while checking backward compatibility of version `0.23.1` with the previous version `0.23.0`.

<details markdown="1">
<summary>Expand to see options used.</summary>

- **Report only summary**: No
- **Report only changes**: Yes
- **Report only binary-incompatible changes**: No
- **Access modifier filter**: `PROTECTED`
- **Old archives**:
  - ![japicmp 0.23.0](https://img.shields.io/badge/japicmp-0.23.0-blue "japicmp 0.23.0")
- **New archives**:
  - ![japicmp 0.23.1](https://img.shields.io/badge/japicmp-0.23.1-blue "japicmp 0.23.1")
- **Evaluate annotations**: Yes
- **Include synthetic classes and class members**: No
- **Include specific elements**: No
- **Exclude specific elements**: Yes
  - `japicmp.*#__cobertura*()`
  - `japicmp.*#__cobertura*(net.sourceforge.cobertura.coveragedata.LightClassmapListener)`
  - `japicmp.*#__cobertura*`
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

| Status   | Type                                                    | Serialization       | Compatibility Changes |
|----------|---------------------------------------------------------|---------------------|-----------------------|
| Modified | [japicmp.cmp.JarArchiveComparator]                      | ![Not serializable] | ![Method return type changed] |
| Modified | [japicmp.config.Options]                                | ![Not serializable] | ![Method removed] ![Method return type changed] ![Method added to public class] |
| Modified | [japicmp.model.AccessModifier]                          | ![Compatible]       | ![Method return type changed] |
| Modified | [japicmp.model.JApiAnnotation]                          | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiAnnotationElement]                   | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiAnnotationElementValue]              | ![Not serializable] | ![Method removed] ![Method return type changed] ![Method added to public class] |
| Modified | [japicmp.model.JApiAttribute]                           | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiBehavior]                            | ![Not serializable] | ![Method removed] ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiClass]                               | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiClassType]                           | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiConstructor]                         | ![Not serializable] | ![Method removed in superclass] ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiException]                           | ![Not serializable] | ![Constructor removed] |
| Modified | [japicmp.model.JApiField]                               | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiGenericTemplate]                     | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiHasLineNumber]                       | ![Not serializable] | ![Method return type changed] |
| Modified | [japicmp.model.JApiImplementedInterface]                | ![Not serializable] | ![Method return type changed] |
| Modified | [japicmp.model.JApiMethod]                              | ![Not serializable] | ![Method removed] ![Method removed in superclass] ![Method return type changed] ![Method added to public class] ![Constructor removed] |
| Modified | [japicmp.model.JApiModifier]                            | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiParameter]                           | ![Not serializable] | ![Method removed] ![Method return type changed] ![Method added to public class] ![Constructor removed] |
| Modified | [japicmp.model.JApiReturnType]                          | ![Not serializable] | ![Constructor removed] |
| Modified | [japicmp.model.JApiSerialVersionUid]                    | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiSuperclass]                          | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JApiType]                                | ![Not serializable] | ![Method return type changed] ![Constructor removed] |
| Modified | [japicmp.model.JavaObjectSerializationCompatibility]    | ![Not serializable] | ![Method removed] ![Method added to public class] |
| Modified | [japicmp.output.html.HtmlOutputGeneratorOptions]        | ![Not serializable] | ![Method return type changed] |
| Modified | [japicmp.output.markdown.config.MarkdownMessageOptions] | ![Not serializable] | ![Method removed] ![Method added to public class] |
| Modified | [japicmp.output.markdown.config.MarkdownOptions]        | ![Not serializable] | ![Field type changed] |
| Modified | [japicmp.output.xml.XmlOutput]                          | ![Not serializable] | ![Method removed] ![Method return type changed] ![Method added to public class] |
| Modified | [japicmp.output.xml.XmlOutputGeneratorOptions]          | ![Not serializable] | ![Method return type changed] |
| Modified | [japicmp.output.xml.model.JApiCmpXmlRoot]               | ![Not serializable] | ![Method removed] ![Method return type changed] ![Method added to public class] |
| Modified | [japicmp.util.AnnotationHelper]                         | ![Not serializable] | ![Method removed] ![Method added to public class] |
| Modified | [japicmp.util.ModifierHelper]                           | ![Not serializable] | ![Method removed] ![Method return type changed] ![Method added to public class] |
| Removed  | [japicmp.util.Optional]                                 | ![Not serializable] | ![Class removed] ![Superclass removed] ![Method removed] ![Constructor removed] |
| Modified | [japicmp.util.OptionalHelper]                           | ![Not serializable] | ![Method removed] ![Method added to public class] |
| Modified | [japicmp.util.TypeNameHelper]                           | ![Not serializable] | ![Method removed] ![Method added to public class] |
| Modified | [japicmp.versioning.SemanticVersion]                    | ![Not serializable] | ![Method return type changed] |
| Modified | [japicmp.versioning.Version]                            | ![Not serializable] | ![Method return type changed] |
| Modified | [japicmp.versioning.VersionChange]                      | ![Not serializable] | ![Method return type changed] |

<details markdown="1">
<summary>Expand for details.</summary>

___

<a id="user-content-japicmp.cmp.jararchivecomparator"></a>
### `japicmp.cmp.JarArchiveComparator`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                   | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JarArchiveComparator` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers | Generics | Type                                                          | Method                                   | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|---------------------------------------------------------------|------------------------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<CtClass>`]~~ &rarr; **[`Optional<CtClass>`][1]** | `loadClass`([`ArchiveType`], [`String`]) |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.config.options"></a>
### `japicmp.config.Options`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `Options` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers    | Generics | Type                                                        | Method                                                           | Annotations | Throws | Compatibility Changes |
|----------|--------------|----------|-------------------------------------------------------------|------------------------------------------------------------------|-------------|--------|-----------------------|
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`addExcludeFromArgument`~~([`Optional<String>`], `boolean`)    |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`addExcludeFromArgument`**([`Optional<String>`][2], `boolean`) |             |        | ![Method added to public class] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`addIncludeFromArgument`~~([`Optional<String>`], `boolean`)    |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`addIncludeFromArgument`**([`Optional<String>`][2], `boolean`) |             |        | ![Method added to public class] |
| Removed  | ~~`public`~~ |          | ~~[`List<Filter>`]~~                                        | ~~`createFilterList`~~([`Optional<String>`], [`List<Filter>`], [`String`], `boolean`) |  |  | ![Method removed] |
| Added    | **`public`** |          | **[`List<Filter>`]**                                        | **`createFilterList`**([`Optional<String>`][2], [`List<Filter>`], [`String`], `boolean`) |  |  | ![Method added to public class] |
| Modified | `public`     |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getHtmlOutputFile`()                                            |             |        | ![Method return type changed] |
| Modified | `public`     |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getHtmlStylesheet`()                                            |             |        | ![Method return type changed] |
| Modified | `public`     |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getNewClassPath`()                                              |             |        | ![Method return type changed] |
| Modified | `public`     |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getOldClassPath`()                                              |             |        | ![Method return type changed] |
| Modified | `public`     |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getXmlOutputFile`()                                             |             |        | ![Method return type changed] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`setAccessModifier`~~([`Optional<AccessModifier>`])            |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`setAccessModifier`**([`Optional<AccessModifier>`][3])         |             |        | ![Method added to public class] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`setHtmlOutputFile`~~([`Optional<String>`])                    |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`setHtmlOutputFile`**([`Optional<String>`][2])                 |             |        | ![Method added to public class] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`setHtmlStylesheet`~~([`Optional<String>`])                    |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`setHtmlStylesheet`**([`Optional<String>`][2])                 |             |        | ![Method added to public class] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`setNewClassPath`~~([`Optional<String>`])                      |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`setNewClassPath`**([`Optional<String>`][2])                   |             |        | ![Method added to public class] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`setOldClassPath`~~([`Optional<String>`])                      |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`setOldClassPath`**([`Optional<String>`][2])                   |             |        | ![Method added to public class] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`setXmlOutputFile`~~([`Optional<String>`])                     |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`setXmlOutputFile`**([`Optional<String>`][2])                  |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.model.accessmodifier"></a>
### `japicmp.model.AccessModifier`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers        | Type | Name             | Extends     | JDK   | Serialization | Compatibility Changes |
|----------|------------------|------|------------------|-------------|-------|---------------|-----------------------|
| Modified | `final` `public` | Enum | `AccessModifier` | [`Enum<E>`] | JDK 8 | ![Compatible] | ![No changes]         |


#### Methods

| Status   | Modifiers         | Generics | Type | Method                   | Annotations | Throws | Compatibility Changes |
|----------|-------------------|----------|------|--------------------------|-------------|--------|-----------------------|
| Modified | `static` `public` |          | ~~[`Optional<AccessModifier>`]~~ &rarr; **[`Optional<AccessModifier>`][3]** | `toModifier`([`String`]) |  |  | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japiannotation"></a>
### `japicmp.model.JApiAnnotation`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiAnnotation` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiAnnotation`~~([`String`], [`Optional<Annotation>`], [`Optional<Annotation>`], [`JApiChangeStatus`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiAnnotation`**([`String`], [`Optional<Annotation>`][4], [`Optional<Annotation>`][4], [`JApiChangeStatus`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type | Method                        | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|------|-------------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<JApiClass>`]~~ &rarr; **[`Optional<JApiClass>`][5]** | `getCorrespondingJApiClass`() |  |  | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<Annotation>`]~~ &rarr; **[`Optional<Annotation>`][4]** | `getNewAnnotation`() |  |  | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<Annotation>`]~~ &rarr; **[`Optional<Annotation>`][4]** | `getOldAnnotation`() |  |  | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japiannotationelement"></a>
### `japicmp.model.JApiAnnotationElement`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                    | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiAnnotationElement` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiAnnotationElement`~~([`String`], [`Optional<MemberValue>`], [`Optional<MemberValue>`], [`JApiChangeStatus`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiAnnotationElement`**([`String`], [`Optional<MemberValue>`][6], [`Optional<MemberValue>`][6], [`JApiChangeStatus`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type | Method          | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|------|-----------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<MemberValue>`]~~ &rarr; **[`Optional<MemberValue>`][6]** | `getNewValue`() |  |  | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<MemberValue>`]~~ &rarr; **[`Optional<MemberValue>`][6]** | `getOldValue`() |  |  | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japiannotationelementvalue"></a>
### `japicmp.model.JApiAnnotationElementValue`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                         | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiAnnotationElementValue` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers    | Generics | Type                                                        | Method                                 | Annotations | Throws | Compatibility Changes |
|----------|--------------|----------|-------------------------------------------------------------|----------------------------------------|-------------|--------|-----------------------|
| Modified | `public`     |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getName`()                            |             |        | ![Method return type changed] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`setName`~~([`Optional<String>`])    |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`setName`**([`Optional<String>`][2]) |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.model.japiattribute"></a>
### `japicmp.model.JApiAttribute`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name            | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiAttribute` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `T`  | [`Object`] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiAttribute`~~([`JApiChangeStatus`], [`Optional<T>`], [`Optional<T>`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiAttribute`**([`JApiChangeStatus`], [`Optional<T>`][7], [`Optional<T>`][7]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type                                              | Method              | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|---------------------------------------------------|---------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<T>`]~~ &rarr; **[`Optional<T>`][7]** | `getNewAttribute`() |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<T>`]~~ &rarr; **[`Optional<T>`][7]** | `getOldAttribute`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japibehavior"></a>
### `japicmp.model.JApiBehavior`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type  | Name           | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-------|----------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Class | `JApiBehavior` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Implemented Interfaces

| Status       | Interface             | Compatibility Changes |
|--------------|-----------------------|-----------------------|
| Incompatible | [`JApiHasLineNumber`] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiBehavior`~~([`JApiClass`], [`String`], [`Optional<? extends CtBehavior>`], [`Optional<? extends CtBehavior>`], [`JApiChangeStatus`], [`JarArchiveComparator`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiBehavior`**([`JApiClass`], [`String`], [`Optional<? extends CtBehavior>`][8], [`Optional<? extends CtBehavior>`][8], [`JApiChangeStatus`], [`JarArchiveComparator`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers       | Generics | Type                                                          | Method               | Annotations | Throws | Compatibility Changes |
|----------|-----------------|----------|---------------------------------------------------------------|----------------------|-------------|--------|-----------------------|
| Removed  | ~~`protected`~~ |          | ~~`void`~~                                                    | ~~`enhanceGenericTypeToParameters`~~([`JApiClass`], [`Optional<? extends CtBehavior>`], [`Optional<? extends CtBehavior>`]) |  |  | ![Method removed] |
| Added    | **`protected`** |          | **`void`**                                                    | **`enhanceGenericTypeToParameters`**([`JApiClass`], [`Optional<? extends CtBehavior>`][8], [`Optional<? extends CtBehavior>`][8]) |  |  | ![No changes] |
| Removed  | ~~`protected`~~ |          | ~~[`JApiAttribute<SyntheticAttribute>`]~~                     | ~~`extractSyntheticAttribute`~~([`Optional<? extends CtBehavior>`], [`Optional<? extends CtBehavior>`]) |  |  | ![Method removed] |
| Added    | **`protected`** |          | **[`JApiAttribute<SyntheticAttribute>`]**                     | **`extractSyntheticAttribute`**([`Optional<? extends CtBehavior>`][8], [`Optional<? extends CtBehavior>`][8]) |  |  | ![No changes] |
| Modified | `public`        |          | ~~[`Optional<Integer>`]~~ &rarr; **[`Optional<Integer>`][9]** | `geNewLineNumber`()  |             |        | ![Method return type changed] |
| Modified | `public`        |          | ~~[`Optional<Integer>`]~~ &rarr; **[`Optional<Integer>`][9]** | `getOldLineNumber`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japiclass"></a>
### `japicmp.model.JApiClass`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiClass` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiClass`~~([`JarArchiveComparator`], [`String`], [`Optional<CtClass>`], [`Optional<CtClass>`], [`JApiChangeStatus`], [`JApiClassType`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiClass`**([`JarArchiveComparator`], [`String`], [`Optional<CtClass>`][1], [`Optional<CtClass>`][1], [`JApiChangeStatus`], [`JApiClassType`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type                                                          | Method          | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|---------------------------------------------------------------|-----------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<CtClass>`]~~ &rarr; **[`Optional<CtClass>`][1]** | `getNewClass`() |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<CtClass>`]~~ &rarr; **[`Optional<CtClass>`][1]** | `getOldClass`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japiclasstype"></a>
### `japicmp.model.JApiClassType`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name            | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiClassType` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiClassType`~~([`Optional<ClassType>`], [`Optional<ClassType>`], [`JApiChangeStatus`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiClassType`**([`Optional<ClassType>`][10], [`Optional<ClassType>`][10], [`JApiChangeStatus`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type | Method                 | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|------|------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<ClassType>`]~~ &rarr; **[`Optional<ClassType>`][10]** | `getNewTypeOptional`() |  |  | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<ClassType>`]~~ &rarr; **[`Optional<ClassType>`][10]** | `getOldTypeOptional`() |  |  | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japiconstructor"></a>
### `japicmp.model.JApiConstructor`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name              | Extends          | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-------------------|------------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiConstructor` | [`JApiBehavior`] | JDK 8 | ![Not serializable] | ![Method removed in superclass] |


#### Implemented Interfaces

| Status       | Interface             | Compatibility Changes |
|--------------|-----------------------|-----------------------|
| Incompatible | [`JApiHasLineNumber`] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiConstructor`~~([`JApiClass`], [`String`], [`JApiChangeStatus`], [`Optional<CtConstructor>`], [`Optional<CtConstructor>`], [`JarArchiveComparator`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiConstructor`**([`JApiClass`], [`String`], [`JApiChangeStatus`], [`Optional<CtConstructor>`][11], [`Optional<CtConstructor>`][11], [`JarArchiveComparator`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type | Method                | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|------|-----------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<CtConstructor>`]~~ &rarr; **[`Optional<CtConstructor>`][11]** | `getNewConstructor`() |  |  | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<CtConstructor>`]~~ &rarr; **[`Optional<CtConstructor>`][11]** | `getOldConstructor`() |  |  | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japiexception"></a>
### `japicmp.model.JApiException`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name            | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiException` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiException`~~([`JarArchiveComparator`], [`String`], [`Optional<CtClass>`], [`JApiChangeStatus`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiException`**([`JarArchiveComparator`], [`String`], [`Optional<CtClass>`][1], [`JApiChangeStatus`]) |  |  | ![No changes] |

___

<a id="user-content-japicmp.model.japifield"></a>
### `japicmp.model.JApiField`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiField` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiField`~~([`JApiClass`], [`JApiChangeStatus`], [`Optional<CtField>`], [`Optional<CtField>`], [`JarArchiveComparatorOptions`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiField`**([`JApiClass`], [`JApiChangeStatus`], [`Optional<CtField>`][12], [`Optional<CtField>`][12], [`JarArchiveComparatorOptions`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type                                                           | Method                  | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|----------------------------------------------------------------|-------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<CtField>`]~~ &rarr; **[`Optional<CtField>`][12]** | `getNewFieldOptional`() |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<CtField>`]~~ &rarr; **[`Optional<CtField>`][12]** | `getOldFieldOptional`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japigenerictemplate"></a>
### `japicmp.model.JApiGenericTemplate`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                  | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiGenericTemplate` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiGenericTemplate`~~([`JApiChangeStatus`], [`String`], [`Optional<String>`], [`Optional<String>`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiGenericTemplate`**([`JApiChangeStatus`], [`String`], [`Optional<String>`][2], [`Optional<String>`][2]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type                                                        | Method                 | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|-------------------------------------------------------------|------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getNewTypeOptional`() |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getOldTypeOptional`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japihaslinenumber"></a>
### `japicmp.model.JApiHasLineNumber`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers           | Type      | Name                | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|---------------------|-----------|---------------------|------------|-------|---------------------|-----------------------|
| Modified | `public` `abstract` | Interface | `JApiHasLineNumber` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers           | Generics | Type                                                          | Method               | Annotations | Throws | Compatibility Changes |
|----------|---------------------|----------|---------------------------------------------------------------|----------------------|-------------|--------|-----------------------|
| Modified | `public` `abstract` |          | ~~[`Optional<Integer>`]~~ &rarr; **[`Optional<Integer>`][9]** | `geNewLineNumber`()  |             |        | ![Method return type changed] |
| Modified | `public` `abstract` |          | ~~[`Optional<Integer>`]~~ &rarr; **[`Optional<Integer>`][9]** | `getOldLineNumber`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japiimplementedinterface"></a>
### `japicmp.model.JApiImplementedInterface`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                       | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|----------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiImplementedInterface` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers | Generics | Type | Method                        | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|------|-------------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<JApiClass>`]~~ &rarr; **[`Optional<JApiClass>`][5]** | `getCorrespondingJApiClass`() |  |  | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japimethod"></a>
### `japicmp.model.JApiMethod`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name         | Extends          | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|--------------|------------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiMethod` | [`JApiBehavior`] | JDK 8 | ![Not serializable] | ![Method removed in superclass] |


#### Implemented Interfaces

| Status       | Interface             | Compatibility Changes |
|--------------|-----------------------|-----------------------|
| Incompatible | [`JApiHasLineNumber`] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiMethod`~~([`JApiClass`], [`String`], [`JApiChangeStatus`], [`Optional<CtMethod>`], [`Optional<CtMethod>`], [`JarArchiveComparator`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiMethod`**([`JApiClass`], [`String`], [`JApiChangeStatus`], [`Optional<CtMethod>`][13], [`Optional<CtMethod>`][13], [`JarArchiveComparator`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers                 | Generics | Type                                                             | Method                                     | Annotations | Throws | Compatibility Changes |
|----------|---------------------------|----------|------------------------------------------------------------------|--------------------------------------------|-------------|--------|-----------------------|
| Modified | `public`                  |          | ~~[`Optional<CtMethod>`]~~ &rarr; **[`Optional<CtMethod>`][13]** | `getNewMethod`()                           |             |        | ![Method return type changed] |
| Modified | `public`                  |          | ~~[`Optional<CtMethod>`]~~ &rarr; **[`Optional<CtMethod>`][13]** | `getOldMethod`()                           |             |        | ![Method return type changed] |
| Removed  | ~~`static`~~ ~~`public`~~ |          | ~~[`String`]~~                                                   | ~~`toString`~~([`Optional<CtMethod>`])     |             |        | ![Method removed]     |
| Added    | **`static`** **`public`** |          | **[`String`]**                                                   | **`toString`**([`Optional<CtMethod>`][13]) |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.model.japimodifier"></a>
### `japicmp.model.JApiModifier`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name           | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|----------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiModifier` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Generics

| Status    | Name | Extends    | Compatibility Changes |
|-----------|------|------------|-----------------------|
| Unchanged | `T`  | [`Object`] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiModifier`~~([`Optional<T>`], [`Optional<T>`], [`JApiChangeStatus`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiModifier`**([`Optional<T>`][7], [`Optional<T>`][7], [`JApiChangeStatus`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type                                              | Method             | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|---------------------------------------------------|--------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<T>`]~~ &rarr; **[`Optional<T>`][7]** | `getNewModifier`() |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<T>`]~~ &rarr; **[`Optional<T>`][7]** | `getOldModifier`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japiparameter"></a>
### `japicmp.model.JApiParameter`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name            | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiParameter` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor                                              | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|----------------------------------------------------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiParameter`~~([`String`], [`Optional<String>`])    |             |        | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiParameter`**([`String`], [`Optional<String>`][2]) |             |        | ![No changes]         |


#### Methods

| Status   | Modifiers    | Generics | Type                                                        | Method                                         | Annotations | Throws | Compatibility Changes |
|----------|--------------|----------|-------------------------------------------------------------|------------------------------------------------|-------------|--------|-----------------------|
| Modified | `public`     |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getTemplateNameOptional`()                    |             |        | ![Method return type changed] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`setTemplateName`~~([`Optional<String>`])    |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`setTemplateName`**([`Optional<String>`][2]) |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.model.japireturntype"></a>
### `japicmp.model.JApiReturnType`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiReturnType` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiReturnType`~~([`JApiChangeStatus`], [`Optional<String>`], [`Optional<String>`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiReturnType`**([`JApiChangeStatus`], [`Optional<String>`][2], [`Optional<String>`][2]) |  |  | ![No changes] |

___

<a id="user-content-japicmp.model.japiserialversionuid"></a>
### `japicmp.model.JApiSerialVersionUid`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                   | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiSerialVersionUid` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiSerialVersionUid`~~(`boolean`, `boolean`, [`Optional<Long>`], [`Optional<Long>`], [`Optional<Long>`], [`Optional<Long>`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiSerialVersionUid`**(`boolean`, `boolean`, [`Optional<Long>`][14], [`Optional<Long>`][14], [`Optional<Long>`][14], [`Optional<Long>`][14]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type                                                     | Method                            | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|----------------------------------------------------------|-----------------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<Long>`]~~ &rarr; **[`Optional<Long>`][14]** | `getSerialVersionUidDefaultNew`() |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<Long>`]~~ &rarr; **[`Optional<Long>`][14]** | `getSerialVersionUidDefaultOld`() |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<Long>`]~~ &rarr; **[`Optional<Long>`][14]** | `getSerialVersionUidInClassNew`() |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<Long>`]~~ &rarr; **[`Optional<Long>`][14]** | `getSerialVersionUidInClassOld`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japisuperclass"></a>
### `japicmp.model.JApiSuperclass`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiSuperclass` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiSuperclass`~~([`JApiClass`], [`Optional<CtClass>`], [`Optional<CtClass>`], [`JApiChangeStatus`], [`JarArchiveComparator`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiSuperclass`**([`JApiClass`], [`Optional<CtClass>`][1], [`Optional<CtClass>`][1], [`JApiChangeStatus`], [`JarArchiveComparator`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type                                                          | Method                        | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|---------------------------------------------------------------|-------------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<JApiClass>`]~~ &rarr; **[`Optional<JApiClass>`][5]** | `getCorrespondingJApiClass`() |         |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<JApiClass>`]~~ &rarr; **[`Optional<JApiClass>`][5]** | `getJApiClass`()          |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<CtClass>`]~~ &rarr; **[`Optional<CtClass>`][1]** | `getNewSuperclass`()          |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]**   | `getNewSuperclassName`()      |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<CtClass>`]~~ &rarr; **[`Optional<CtClass>`][1]** | `getOldSuperclass`()          |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]**   | `getOldSuperclassName`()      |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.japitype"></a>
### `japicmp.model.JApiType`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name       | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiType` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|-------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`JApiType`~~([`Optional<String>`], [`Optional<String>`], [`JApiChangeStatus`]) |  |  | ![Constructor removed] |
| Added   | **`public`** |          | **`JApiType`**([`Optional<String>`][2], [`Optional<String>`][2], [`JApiChangeStatus`]) |  |  | ![No changes] |


#### Methods

| Status   | Modifiers | Generics | Type                                                        | Method                 | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|-------------------------------------------------------------|------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getNewTypeOptional`() |             |        | ![Method return type changed] |
| Modified | `public`  |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getOldTypeOptional`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.model.javaobjectserializationcompatibility"></a>
### `japicmp.model.JavaObjectSerializationCompatibility`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                               | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JavaObjectSeria…ionCompatibility` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status  | Modifiers                 | Generics | Type                         | Method | Annotations | Throws | Compatibility Changes |
|---------|---------------------------|----------|------------------------------|--------|-------------|--------|-----------------------|
| Removed | ~~`static`~~ ~~`public`~~ |          | ~~[`JApiSerialVersionUid`]~~ | ~~`extractSerialVersionUid`~~([`JarArchiveComparatorOptions`], [`JarArchiveComparator`], [`Optional<CtClass>`], [`Optional<CtClass>`]) |  |  | ![Method removed] |
| Added   | **`static`** **`public`** |          | **[`JApiSerialVersionUid`]** | **`extractSerialVersionUid`**([`JarArchiveComparatorOptions`], [`JarArchiveComparator`], [`Optional<CtClass>`][1], [`Optional<CtClass>`][1]) |  |  | ![Method added to public class] |

___

<a id="user-content-japicmp.output.html.htmloutputgeneratoroptions"></a>
### `japicmp.output.html.HtmlOutputGeneratorOptions`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                         | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `HtmlOutputGeneratorOptions` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers | Generics | Type                                                        | Method       | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|-------------------------------------------------------------|--------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getTitle`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.output.markdown.config.markdownmessageoptions"></a>
### `japicmp.output.markdown.config.MarkdownMessageOptions`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                     | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|--------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `MarkdownMessageOptions` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status  | Modifiers    | Generics | Type           | Method                                          | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|----------------|-------------------------------------------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~[`String`]~~ | ~~`getClassType`~~([`Optional<ClassType>`])     |             |        | ![Method removed]     |
| Added   | **`public`** |          | **[`String`]** | **`getClassType`**([`Optional<ClassType>`][10]) |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.output.markdown.config.markdownoptions"></a>
### `japicmp.output.markdown.config.MarkdownOptions`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name              | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `MarkdownOptions` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Fields

| Status   | Modifiers | Type                                                        | Name               | Annotations | Compatibility Changes |
|----------|-----------|-------------------------------------------------------------|--------------------|-------------|-----------------------|
| Modified | `public`  | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `targetNewVersion` |             | ![Field type changed] |
| Modified | `public`  | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `targetOldVersion` |             | ![Field type changed] |

___

<a id="user-content-japicmp.output.xml.xmloutput"></a>
### `japicmp.output.xml.XmlOutput`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `XmlOutput` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers    | Generics | Type       | Method                                                        | Annotations | Throws | Compatibility Changes |
|----------|--------------|----------|------------|---------------------------------------------------------------|-------------|--------|-----------------------|
| Modified | `public`     |          | ~~[`Optional<ByteArrayOutputStream>`]~~ &rarr; **[`Optional<ByteArrayOutputStream>`][15]** | `getXmlOutputStream`() |  |  | ![Method return type changed] |
| Removed  | ~~`public`~~ |          | ~~`void`~~ | ~~`setXmlOutputStream`~~([`Optional<ByteArrayOutputStream>`]) |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`** | **`setXmlOutputStream`**([`Optional<ByteArrayOutputStream>`][15]) |         |        | ![Method added to public class] |

___

<a id="user-content-japicmp.output.xml.xmloutputgeneratoroptions"></a>
### `japicmp.output.xml.XmlOutputGeneratorOptions`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                        | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `XmlOutputGeneratorOptions` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers | Generics | Type                                                        | Method       | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|-------------------------------------------------------------|--------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getTitle`() |             |        | ![Method return type changed] |

___

<a id="user-content-japicmp.output.xml.model.japicmpxmlroot"></a>
### `japicmp.output.xml.model.JApiCmpXmlRoot`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiCmpXmlRoot` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers    | Generics | Type                                                        | Method                                          | Annotations | Throws | Compatibility Changes |
|----------|--------------|----------|-------------------------------------------------------------|-------------------------------------------------|-------------|--------|-----------------------|
| Modified | `public`     |          | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getTitleOptional`()                            |             |        | ![Method return type changed] |
| Removed  | ~~`public`~~ |          | ~~`void`~~                                                  | ~~`setTitleOptional`~~([`Optional<String>`])    |             |        | ![Method removed]     |
| Added    | **`public`** |          | **`void`**                                                  | **`setTitleOptional`**([`Optional<String>`][2]) |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.util.annotationhelper"></a>
### `japicmp.util.AnnotationHelper`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name               | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|--------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `AnnotationHelper` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status  | Modifiers                 | Generics                     | Type       | Method | Annotations | Throws | Compatibility Changes |
|---------|---------------------------|------------------------------|------------|--------|-------------|--------|-----------------------|
| Removed | ~~`static`~~ ~~`public`~~ | \<~~[`T extends Object`]~~\> | ~~`void`~~ | ~~`computeAnnotationChanges`~~([`List<JApiAnnotation>`], [`Optional<T>`], [`Optional<T>`], [`JarArchiveComparatorOptions`], [`AnnotationsAttributeCallback<T>`]) |  |  | ![Method removed] |
| Added   | **`static`** **`public`** | \<**[`T extends Object`]**\> | **`void`** | **`computeAnnotationChanges`**([`List<JApiAnnotation>`], [`Optional<T>`][7], [`Optional<T>`][7], [`JarArchiveComparatorOptions`], [`AnnotationsAttributeCallback<T>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-japicmp.util.modifierhelper"></a>
### `japicmp.util.ModifierHelper`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `ModifierHelper` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers                 | Generics                               | Type                                                        | Method | Annotations | Throws | Compatibility Changes |
|----------|---------------------------|----------------------------------------|-------------------------------------------------------------|--------|-------------|--------|-----------------------|
| Removed  | ~~`static`~~ ~~`public`~~ | \<~~[`T extends JApiModifierBase`]~~\> | ~~[`JApiModifier<T>`]~~                                     | ~~`extractModifierFromBehavior`~~([`Optional<? extends CtBehavior>`], [`Optional<? extends CtBehavior>`], [`ExtractModifier…BehaviorCallback<T>`]) |  |  | ![Method removed] |
| Added    | **`static`** **`public`** | \<**[`T extends JApiModifierBase`]**\> | **[`JApiModifier<T>`]**                                     | **`extractModifierFromBehavior`**([`Optional<? extends CtBehavior>`][8], [`Optional<? extends CtBehavior>`][8], [`ExtractModifier…BehaviorCallback<T>`]) |  |  | ![Method added to public class] |
| Removed  | ~~`static`~~ ~~`public`~~ | \<~~[`T extends JApiModifierBase`]~~\> | ~~[`JApiModifier<T>`]~~                                     | ~~`extractModifierFromClass`~~([`Optional<CtClass>`], [`Optional<CtClass>`], [`ExtractModifierFromClassCallback<T>`]) |  |  | ![Method removed] |
| Added    | **`static`** **`public`** | \<**[`T extends JApiModifierBase`]**\> | **[`JApiModifier<T>`]**                                     | **`extractModifierFromClass`**([`Optional<CtClass>`][1], [`Optional<CtClass>`][1], [`ExtractModifierFromClassCallback<T>`]) |  |  | ![Method added to public class] |
| Removed  | ~~`static`~~ ~~`public`~~ | \<~~[`T extends JApiModifierBase`]~~\> | ~~[`JApiModifier<T>`]~~                                     | ~~`extractModifierFromField`~~([`Optional<CtField>`], [`Optional<CtField>`], [`ExtractModifierFromFieldCallback<T>`]) |  |  | ![Method removed] |
| Added    | **`static`** **`public`** | \<**[`T extends JApiModifierBase`]**\> | **[`JApiModifier<T>`]**                                     | **`extractModifierFromField`**([`Optional<CtField>`][12], [`Optional<CtField>`][12], [`ExtractModifierFromFieldCallback<T>`]) |  |  | ![Method added to public class] |
| Modified | `static` `public`         |                                        | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getNewModifierName`([`JApiModifier<? extends Enum<? extends Enum<? extends ?>>>`]) |  |  | ![Method return type changed] |
| Modified | `static` `public`         |                                        | ~~[`Optional<String>`]~~ &rarr; **[`Optional<String>`][2]** | `getOldModifierName`([`JApiModifier<? extends Enum<? extends Enum<? extends ?>>>`]) |  |  | ![Method return type changed] |

___

<a id="user-content-japicmp.util.optional"></a>
### `japicmp.util.Optional`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status  | Modifiers                   | Type      | Name           | Extends        | JDK       | Serialization       | Compatibility Changes |
|---------|-----------------------------|-----------|----------------|----------------|-----------|---------------------|-----------------------|
| Removed | ~~`public`~~ ~~`abstract`~~ | ~~Class~~ | ~~`Optional`~~ | ~~[`Object`]~~ | ~~JDK 8~~ | ![Not serializable] | ![Class removed] ![Superclass removed] |


#### Generics

| Status  | Name    | Extends        | Compatibility Changes |
|---------|---------|----------------|-----------------------|
| Removed | ~~`T`~~ | ~~[`Object`]~~ | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor      | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|------------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`Optional`~~() |             |        | ![Constructor removed] |


#### Methods

| Status  | Modifiers                   | Generics                     | Type                | Method                              | Annotations | Throws | Compatibility Changes |
|---------|-----------------------------|------------------------------|---------------------|-------------------------------------|-------------|--------|-----------------------|
| Removed | ~~`static`~~ ~~`public`~~   | \<~~[`T extends Object`]~~\> | ~~[`Optional<T>`]~~ | ~~`absent`~~()                      |             |        | ![Method removed]     |
| Removed | ~~`public`~~ ~~`abstract`~~ |                              | ~~`boolean`~~       | ~~`equals`~~([`Object`])            |             |        | ![Method removed]     |
| Removed | ~~`static`~~ ~~`public`~~   | \<~~[`T extends Object`]~~\> | ~~[`Optional<T>`]~~ | ~~`fromNullable`~~(`T`)             |             |        | ![Method removed]     |
| Removed | ~~`public`~~ ~~`abstract`~~ |                              | ~~[`Object`]~~      | ~~`get`~~()                         |             |        | ![Method removed]     |
| Removed | ~~`public`~~ ~~`abstract`~~ |                              | ~~`int`~~           | ~~`hashCode`~~()                    |             |        | ![Method removed]     |
| Removed | ~~`public`~~ ~~`abstract`~~ |                              | ~~`boolean`~~       | ~~`isPresent`~~()                   |             |        | ![Method removed]     |
| Removed | ~~`static`~~ ~~`public`~~   | \<~~[`T extends Object`]~~\> | ~~[`Optional<T>`]~~ | ~~`of`~~(`T`)                       |             |        | ![Method removed]     |
| Removed | ~~`public`~~ ~~`abstract`~~ |                              | ~~[`Optional<T>`]~~ | ~~`or`~~([`Optional<? extends T>`]) |             |        | ![Method removed]     |
| Removed | ~~`public`~~ ~~`abstract`~~ |                              | ~~[`Object`]~~      | ~~`or`~~(`T`)                       |             |        | ![Method removed]     |

___

<a id="user-content-japicmp.util.optionalhelper"></a>
### `japicmp.util.OptionalHelper`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `OptionalHelper` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status  | Modifiers                 | Generics                     | Type           | Method                                     | Annotations | Throws | Compatibility Changes |
|---------|---------------------------|------------------------------|----------------|--------------------------------------------|-------------|--------|-----------------------|
| Removed | ~~`static`~~ ~~`public`~~ | \<~~[`T extends Object`]~~\> | ~~[`String`]~~ | ~~`optionalToString`~~([`Optional<T>`])    |             |        | ![Method removed]     |
| Added   | **`static`** **`public`** | \<**[`T extends Object`]**\> | **[`String`]** | **`optionalToString`**([`Optional<T>`][7]) |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.util.typenamehelper"></a>
### `japicmp.util.TypeNameHelper`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `TypeNameHelper` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status  | Modifiers                 | Generics | Type           | Method | Annotations | Throws | Compatibility Changes |
|---------|---------------------------|----------|----------------|--------|-------------|--------|-----------------------|
| Removed | ~~`static`~~ ~~`public`~~ |          | ~~[`String`]~~ | ~~`formatParameterTypeName`~~([`JApiBehavior`], [`JApiParameter`], [`Optional<VarargsModifier>`], [`List<JApiGenericType>`], `boolean`) |  |  | ![Method removed] |
| Added   | **`static`** **`public`** |          | **[`String`]** | **`formatParameterTypeName`**([`JApiBehavior`], [`JApiParameter`], [`Optional<VarargsModifier>`][16], [`List<JApiGenericType>`], `boolean`) |  |  | ![Method added to public class] |

___

<a id="user-content-japicmp.versioning.semanticversion"></a>
### `japicmp.versioning.SemanticVersion`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name              | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `SemanticVersion` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers | Generics | Type | Method                                   | Annotations | Throws | Compatibility Changes |
|----------|-----------|----------|------|------------------------------------------|-------------|--------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<ChangeType>`]~~ &rarr; **[`Optional<ChangeType>`][17]** | `computeChangeType`([`SemanticVersion`]) |  |  | ![Method return type changed] |

___

<a id="user-content-japicmp.versioning.version"></a>
### `japicmp.versioning.Version`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `Version` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers         | Generics | Type | Method                           | Annotations | Throws | Compatibility Changes |
|----------|-------------------|----------|------|----------------------------------|-------------|--------|-----------------------|
| Modified | `public`          |          | ~~[`Optional<SemanticVersion>`]~~ &rarr; **[`Optional<SemanticVersion>`][18]** | `getSemanticVersion`() |  |  | ![Method return type changed] |
| Modified | `static` `public` |          | ~~[`Optional<SemanticVersion>`]~~ &rarr; **[`Optional<SemanticVersion>`][18]** | `getSemanticVersion`([`String`]) |  |  | ![Method return type changed] |

___

<a id="user-content-japicmp.versioning.versionchange"></a>
### `japicmp.versioning.VersionChange`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name            | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `VersionChange` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status   | Modifiers | Generics | Type | Method                | Annotations | Throws               | Compatibility Changes |
|----------|-----------|----------|------|-----------------------|-------------|----------------------|-----------------------|
| Modified | `public`  |          | ~~[`Optional<ChangeType>`]~~ &rarr; **[`Optional<ChangeType>`][17]** | `computeChangeType`() |  | [`JApiCmpException`] | ![Method return type changed] |


</details>


___

*Generated on: 2025-01-09 18:44:51.279+0000*.

[1]: # "java.util.Optional<javassist.CtClass>"
[2]: # "java.util.Optional<java.lang.String>"
[3]: # "java.util.Optional<japicmp.model.AccessModifier>"
[4]: # "java.util.Optional<javassist.bytecode.annotation.Annotation>"
[5]: # "java.util.Optional<japicmp.model.JApiClass>"
[6]: # "java.util.Optional<javassist.bytecode.annotation.MemberValue>"
[7]: # "java.util.Optional<T>"
[8]: # "java.util.Optional<? extends javassist.CtBehavior>"
[9]: # "java.util.Optional<java.lang.Integer>"
[10]: # "java.util.Optional<japicmp.model.JApiClassType$ClassType>"
[11]: # "java.util.Optional<javassist.CtConstructor>"
[12]: # "java.util.Optional<javassist.CtField>"
[13]: # "java.util.Optional<javassist.CtMethod>"
[14]: # "java.util.Optional<java.lang.Long>"
[15]: # "java.util.Optional<java.io.ByteArrayOutputStream>"
[16]: # "java.util.Optional<japicmp.model.VarargsModifier>"
[17]: # "java.util.Optional<japicmp.versioning.SemanticVersion$ChangeType>"
[18]: # "java.util.Optional<japicmp.versioning.SemanticVersion>"
[Class removed]: https://img.shields.io/badge/Class_removed-red "Class removed"
[Compatible]: https://img.shields.io/badge/Compatible-green "Compatible"
[Constructor removed]: https://img.shields.io/badge/Constructor_removed-red "Constructor removed"
[Field type changed]: https://img.shields.io/badge/Field_type_changed-red "Field type changed"
[Method added to public class]: https://img.shields.io/badge/Method_added_to_public_class-yellow "Method added to public class"
[Method removed]: https://img.shields.io/badge/Method_removed-red "Method removed"
[Method removed in superclass]: https://img.shields.io/badge/Method_removed_in_superclass-red "Method removed in superclass"
[Method return type changed]: https://img.shields.io/badge/Method_return_type_changed-red "Method return type changed"
[No changes]: https://img.shields.io/badge/No_changes-green "No changes"
[Not serializable]: https://img.shields.io/badge/Not_serializable-green "Not serializable"
[Superclass removed]: https://img.shields.io/badge/Superclass_removed-red "Superclass removed"
[`AnnotationsAttributeCallback<T>`]: # "japicmp.util.AnnotationHelper$AnnotationsAttributeCallback<T>"
[`ArchiveType`]: # "japicmp.cmp.JarArchiveComparator$ArchiveType"
[`Enum<E>`]: # "java.lang.Enum<E extends java.lang.Enum<E>>"
[`ExtractModifierFromClassCallback<T>`]: # "japicmp.util.ModifierHelper$ExtractModifierFromClassCallback<T>"
[`ExtractModifierFromFieldCallback<T>`]: # "japicmp.util.ModifierHelper$ExtractModifierFromFieldCallback<T>"
[`ExtractModifier…BehaviorCallback<T>`]: # "japicmp.util.ModifierHelper$ExtractModifierFromBehaviorCallback<T>"
[`JApiAttribute<SyntheticAttribute>`]: # "japicmp.model.JApiAttribute<japicmp.model.SyntheticAttribute>"
[`JApiBehavior`]: # "japicmp.model.JApiBehavior"
[`JApiChangeStatus`]: # "japicmp.model.JApiChangeStatus"
[`JApiClassType`]: # "japicmp.model.JApiClassType"
[`JApiClass`]: # "japicmp.model.JApiClass"
[`JApiCmpException`]: # "japicmp.exception.JApiCmpException"
[`JApiHasLineNumber`]: # "japicmp.model.JApiHasLineNumber"
[`JApiModifier<? extends Enum<? extends Enum<? extends ?>>>`]: # "japicmp.model.JApiModifier<? extends java.lang.Enum<? extends java.lang.Enum<? extends ?>>>"
[`JApiModifier<T>`]: # "japicmp.model.JApiModifier<T>"
[`JApiParameter`]: # "japicmp.model.JApiParameter"
[`JApiSerialVersionUid`]: # "japicmp.model.JApiSerialVersionUid"
[`JarArchiveComparatorOptions`]: # "japicmp.cmp.JarArchiveComparatorOptions"
[`JarArchiveComparator`]: # "japicmp.cmp.JarArchiveComparator"
[`List<Filter>`]: # "java.util.List<japicmp.filter.Filter>"
[`List<JApiAnnotation>`]: # "java.util.List<japicmp.model.JApiAnnotation>"
[`List<JApiGenericType>`]: # "java.util.List<japicmp.model.JApiGenericType>"
[`Object`]: # "java.lang.Object"
[`Optional<? extends CtBehavior>`]: # "japicmp.util.Optional<? extends javassist.CtBehavior>"
[`Optional<? extends T>`]: # "japicmp.util.Optional<? extends T>"
[`Optional<AccessModifier>`]: # "japicmp.util.Optional<japicmp.model.AccessModifier>"
[`Optional<Annotation>`]: # "japicmp.util.Optional<javassist.bytecode.annotation.Annotation>"
[`Optional<ByteArrayOutputStream>`]: # "japicmp.util.Optional<java.io.ByteArrayOutputStream>"
[`Optional<ChangeType>`]: # "japicmp.util.Optional<japicmp.versioning.SemanticVersion$ChangeType>"
[`Optional<ClassType>`]: # "japicmp.util.Optional<japicmp.model.JApiClassType$ClassType>"
[`Optional<CtClass>`]: # "japicmp.util.Optional<javassist.CtClass>"
[`Optional<CtConstructor>`]: # "japicmp.util.Optional<javassist.CtConstructor>"
[`Optional<CtField>`]: # "japicmp.util.Optional<javassist.CtField>"
[`Optional<CtMethod>`]: # "japicmp.util.Optional<javassist.CtMethod>"
[`Optional<Integer>`]: # "japicmp.util.Optional<java.lang.Integer>"
[`Optional<JApiClass>`]: # "japicmp.util.Optional<japicmp.model.JApiClass>"
[`Optional<Long>`]: # "japicmp.util.Optional<java.lang.Long>"
[`Optional<MemberValue>`]: # "japicmp.util.Optional<javassist.bytecode.annotation.MemberValue>"
[`Optional<SemanticVersion>`]: # "japicmp.util.Optional<japicmp.versioning.SemanticVersion>"
[`Optional<String>`]: # "japicmp.util.Optional<java.lang.String>"
[`Optional<T>`]: # "japicmp.util.Optional<T>"
[`Optional<VarargsModifier>`]: # "japicmp.util.Optional<japicmp.model.VarargsModifier>"
[`SemanticVersion`]: # "japicmp.versioning.SemanticVersion"
[`String`]: # "java.lang.String"
[`T extends JApiModifierBase`]: # "T extends japicmp.model.JApiModifierBase"
[`T extends Object`]: # "T extends java.lang.Object"
[japicmp.cmp.JarArchiveComparator]: #user-content-japicmp.cmp.jararchivecomparator
[japicmp.config.Options]: #user-content-japicmp.config.options
[japicmp.model.AccessModifier]: #user-content-japicmp.model.accessmodifier
[japicmp.model.JApiAnnotation]: #user-content-japicmp.model.japiannotation
[japicmp.model.JApiAnnotationElement]: #user-content-japicmp.model.japiannotationelement
[japicmp.model.JApiAnnotationElementValue]: #user-content-japicmp.model.japiannotationelementvalue
[japicmp.model.JApiAttribute]: #user-content-japicmp.model.japiattribute
[japicmp.model.JApiBehavior]: #user-content-japicmp.model.japibehavior
[japicmp.model.JApiClass]: #user-content-japicmp.model.japiclass
[japicmp.model.JApiClassType]: #user-content-japicmp.model.japiclasstype
[japicmp.model.JApiConstructor]: #user-content-japicmp.model.japiconstructor
[japicmp.model.JApiException]: #user-content-japicmp.model.japiexception
[japicmp.model.JApiField]: #user-content-japicmp.model.japifield
[japicmp.model.JApiGenericTemplate]: #user-content-japicmp.model.japigenerictemplate
[japicmp.model.JApiHasLineNumber]: #user-content-japicmp.model.japihaslinenumber
[japicmp.model.JApiImplementedInterface]: #user-content-japicmp.model.japiimplementedinterface
[japicmp.model.JApiMethod]: #user-content-japicmp.model.japimethod
[japicmp.model.JApiModifier]: #user-content-japicmp.model.japimodifier
[japicmp.model.JApiParameter]: #user-content-japicmp.model.japiparameter
[japicmp.model.JApiReturnType]: #user-content-japicmp.model.japireturntype
[japicmp.model.JApiSerialVersionUid]: #user-content-japicmp.model.japiserialversionuid
[japicmp.model.JApiSuperclass]: #user-content-japicmp.model.japisuperclass
[japicmp.model.JApiType]: #user-content-japicmp.model.japitype
[japicmp.model.JavaObjectSerializationCompatibility]: #user-content-japicmp.model.javaobjectserializationcompatibility
[japicmp.output.html.HtmlOutputGeneratorOptions]: #user-content-japicmp.output.html.htmloutputgeneratoroptions
[japicmp.output.markdown.config.MarkdownMessageOptions]: #user-content-japicmp.output.markdown.config.markdownmessageoptions
[japicmp.output.markdown.config.MarkdownOptions]: #user-content-japicmp.output.markdown.config.markdownoptions
[japicmp.output.xml.XmlOutput]: #user-content-japicmp.output.xml.xmloutput
[japicmp.output.xml.XmlOutputGeneratorOptions]: #user-content-japicmp.output.xml.xmloutputgeneratoroptions
[japicmp.output.xml.model.JApiCmpXmlRoot]: #user-content-japicmp.output.xml.model.japicmpxmlroot
[japicmp.util.AnnotationHelper]: #user-content-japicmp.util.annotationhelper
[japicmp.util.ModifierHelper]: #user-content-japicmp.util.modifierhelper
[japicmp.util.Optional]: #user-content-japicmp.util.optional
[japicmp.util.OptionalHelper]: #user-content-japicmp.util.optionalhelper
[japicmp.util.TypeNameHelper]: #user-content-japicmp.util.typenamehelper
[japicmp.versioning.SemanticVersion]: #user-content-japicmp.versioning.semanticversion
[japicmp.versioning.Version]: #user-content-japicmp.versioning.version
[japicmp.versioning.VersionChange]: #user-content-japicmp.versioning.versionchange
