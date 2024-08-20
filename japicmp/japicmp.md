
# Compatibility Report

![semver PATCH](https://img.shields.io/badge/semver-PATCH-yellow?logo=semver "semver PATCH")

## Summary

> [!IMPORTANT]
>
> Compatible bug fixes found while checking backward compatibility of version `0.23.1-SNAPSHOT` with the previous version `0.22.0`.

<details markdown="1">
<summary>Expand to see options used.</summary>

- **Report only summary**: No
- **Report only changes**: Yes
- **Report only binary-incompatible changes**: No
- **Access modifier filter**: `PROTECTED`
- **Old archives**:
  - ![japicmp 0.22.0](https://img.shields.io/badge/japicmp-0.22.0-blue "japicmp 0.22.0")
- **New archives**:
  - ![japicmp 0.23.1-SNAPSHOT](https://img.shields.io/badge/japicmp-0.23.1_SNAPSHOT-blue "japicmp 0.23.1-SNAPSHOT")
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
| Modified | [japicmp.config.Options]                                | ![Not serializable] | ![Method added to public class] |
| Added    | [japicmp.output.markdown.Markdown]                      | ![Not serializable] | ![Method added to public class] |
| Added    | [japicmp.output.markdown.MarkdownBadge]                 | ![Not serializable] | ![Method added to public class] |
| Added    | [japicmp.output.markdown.MarkdownOutputGenerator]       | ![Not serializable] | ![Method added to public class] |
| Added    | [japicmp.output.markdown.config.MarkdownHeaderOptions]  | ![Not serializable] | ![No changes]         |
| Added    | [japicmp.output.markdown.config.MarkdownMessageOptions] | ![Not serializable] | ![Method added to public class] |
| Added    | [japicmp.output.markdown.config.MarkdownOptions]        | ![Not serializable] | ![Method added to public class] |
| Added    | [japicmp.output.markdown.config.MarkdownSortOptions]    | ![Not serializable] | ![No changes]         |
| Added    | [japicmp.output.markdown.config.MarkdownTitleOptions]   | ![Not serializable] | ![No changes]         |
| Added    | [japicmp.util.JApiClassFileFormatVersionHelper]         | ![Not serializable] | ![Method added to public class] |
| Added    | [japicmp.util.MemberValueHelper]                        | ![Not serializable] | ![Method added to public class] |
| Modified | [japicmp.util.ModifierHelper]                           | ![Not serializable] | ![Method added to public class] |
| Added    | [japicmp.util.TypeNameHelper]                           | ![Not serializable] | ![Method added to public class] |

<details markdown="1">
<summary>Expand for details.</summary>

___

<a id="user-content-japicmp.config.options"></a>
### `japicmp.config.Options`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name      | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-----------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `Options` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type          | Method                       | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|---------------|------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`boolean`** | **`isMarkdown`**()           |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **`void`**    | **`setMarkdown`**(`boolean`) |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.output.markdown.markdown"></a>
### `japicmp.output.markdown.Markdown`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers                   | Type      | Name           | Extends        | JDK       | Serialization       | Compatibility Changes |
|--------|-----------------------------|-----------|----------------|----------------|-----------|---------------------|-----------------------|
| Added  | **`public`** **`abstract`** | **Class** | **`Markdown`** | **[`Object`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Constructors

| Status | Modifiers    | Generics | Constructor      | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`Markdown`**() |             |        | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics | Type           | Method                        | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|----------|----------------|-------------------------------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** |          | **[`String`]** | **`angles`**([`String`])      |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`backticks`**([`String`])   |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`brackets`**([`String`])    |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`parenthesis`**([`String`]) |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`quotes`**([`String`])      |             |        | ![Method added to public class] |


#### Fields

| Status | Modifiers                             | Type                                       | Name                       | Annotations | Compatibility Changes |
|--------|---------------------------------------|--------------------------------------------|----------------------------|-------------|-----------------------|
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `ANGLE_CLOSE`              |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `ANGLE_OPEN`               |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `BACKSLASH`                |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `BACKTICK`                 |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `BANG`                     |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`Collector<CharSequence, ?, String>`]** | `BR`                       |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `BRACKET_CLOSE`            |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `BRACKET_OPEN`             |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `COLON`                    |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`Collector<CharSequence, ?, String>`]** | `CSV`                      |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `DASH`                     |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `DOT`                      |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `EMPTY`                    |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `EOL`                      |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `EQUAL`                    |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `HASH`                     |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`Collector<CharSequence, ?, String>`]** | `LINES`                    |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `MARKDOWN_HORIZONTAL_RULE` |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `PARAGRAPH`                |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `PARENTHESIS_CLOSE`        |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `PARENTHESIS_OPEN`         |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **`char`**                                 | `PIPE`                     |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `QUOTE`                    |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `SPACE`                    |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`Collector<CharSequence, ?, String>`]** | `SPACES`                   |             | ![No changes]         |
| Added  | **`public`** **`static`** **`final`** | **[`String`]**                             | `UNDERSCORE`               |             | ![No changes]         |

___

<a id="user-content-japicmp.output.markdown.markdownbadge"></a>
### `japicmp.output.markdown.MarkdownBadge`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                | Extends          | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|---------------------|------------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`MarkdownBadge`** | **[`Markdown`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Constructors

| Status | Modifiers    | Generics | Constructor                                             | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|---------------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`MarkdownBadge`**([`String`], [`String`], [`String`]) |             |        | ![No changes]         |
| Added  | **`public`** |          | **`MarkdownBadge`**([`String`], [`String`], [`String`], [`String`]) |  |       | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type                     | Method                                                           | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|--------------------------|------------------------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`MarkdownRefImage`]** | **`toRefImage`**([`MarkdownReferences`], [`String`], [`String`]) |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`MarkdownRefImage`]** | **`toRefImage`**([`MarkdownReferences`], [`String`])             |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`MarkdownRefImage`]** | **`toRefImage`**([`MarkdownReferences`])                         |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`String`]**           | **`toString`**()                                                 |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.output.markdown.markdownoutputgenerator"></a>
### `japicmp.output.markdown.MarkdownOutputGenerator`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                          | Extends                    | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|-------------------------------|----------------------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`MarkdownOutputGenerator`** | **[`OutputGenerator<T>`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Constructors

| Status | Modifiers    | Generics | Constructor                                                     | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|-----------------------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`MarkdownOutputGenerator`**([`MarkdownOptions`], [`List<JApiClass>`]) |     |        | ![No changes]         |
| Added  | **`public`** |          | **`MarkdownOutputGenerator`**([`Options`], [`List<JApiClass>`]) |             |        | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type           | Method           | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|----------------|------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`String`]** | **`generate`**() |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.output.markdown.config.markdownheaderoptions"></a>
### `japicmp.output.markdown.config.MarkdownHeaderOptions`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                        | Extends        | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|-----------------------------|----------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`MarkdownHeaderOptions`** | **[`Object`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Constructors

| Status | Modifiers    | Generics | Constructor                   | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|-------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`MarkdownHeaderOptions`**() |             |        | ![No changes]         |


#### Fields

| Status | Modifiers    | Type           | Name                           | Annotations | Compatibility Changes |
|--------|--------------|----------------|--------------------------------|-------------|-----------------------|
| Added  | **`public`** | **[`String`]** | `annotationName`               |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `annotations`                  |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `classJdk`                     |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `className`                    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `classType`                    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `compatibilityChanges`         |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `constructorNameAndParameters` |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `exceptions`                   |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `fieldName`                    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `fieldType`                    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `genericTemplateName`          |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `genericTemplateType`          |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `generics`                     |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `interfaceName`                |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `methodNameAndParameters`      |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `methodReturnType`             |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `modifiers`                    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `serialization`                |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `status`                       |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `superclass`                   |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `type`                         |             | ![No changes]         |

___

<a id="user-content-japicmp.output.markdown.config.markdownmessageoptions"></a>
### `japicmp.output.markdown.config.MarkdownMessageOptions`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                         | Extends        | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|------------------------------|----------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`MarkdownMessageOptions`** | **[`Object`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Constructors

| Status | Modifiers    | Generics | Constructor                    | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|--------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`MarkdownMessageOptions`**() |             |        | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type           | Method                                               | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|----------------|------------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **[`String`]** | **`checkbox`**(`boolean`)                            |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`String`]** | **`getClassType`**([`Optional<ClassType>`])          |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`String`]** | **`getCurrentTimestamp`**()                          |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`String`]** | **`getSemanticColor`**([`JApiSemanticVersionLevel`]) |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`String`]** | **`getSemverBadge`**([`String`])                     |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`String`]** | **`getSummaryMessage`**([`String`])                  |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **[`String`]** | **`yesNo`**(`boolean`)                               |             |        | ![Method added to public class] |


#### Fields

| Status | Modifiers    | Type                                                  | Name                                                          | Annotations | Compatibility Changes |
|--------|--------------|-------------------------------------------------------|---------------------------------------------------------------|-------------|-----------------------|
| Added  | **`public`** | **[`String`]**                                        | `accessModifierFilter`                                        |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `added`                                                       |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `anyChanges`                                                  |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `badgeMajorChanges`                                           |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `badgeMinorChanges`                                           |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `badgeNoChanges`                                              |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `badgePatchChanges`                                           |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `binaryIncompatibleChanges`                                   |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `checked`                                                     |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `classpathMode`                                               |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `colorMajorChanges`                                           |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `colorMinorChanges`                                           |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `colorNoChanges`                                              |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `colorPatchChanges`                                           |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `colorVersionNumber`                                          |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `compatibilityBinary`                                         |             | ![No changes]         |
| Added  | **`public`** | **[`Map<JApiCompatibilityChangeType, String>`]**      | `compatibilityChangeType`                                     |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `compatibilitySerialization`                                  |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `compatibilitySource`                                         |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `dateTimeFormat`                                              |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `evaluateAnnotations`                                         |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `excludeSpecificElements`                                     |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `expandOptions`                                               |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `expandResults`                                               |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `generatedOn`                                                 |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `ignoreAllMissingClasses`                                     |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `ignoreSpecificMissingClasses`                                |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `includeSpecificElements`                                     |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `includeSynthetic`                                            |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `incompatibleChangesCausedByExcludedClasses`                  |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `manyNewArchives`                                             |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `manyOldArchives`                                             |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `modified`                                                    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `newArchives`                                                 |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `newClasspath`                                                |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `no`                                                          |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `noCompatibilityChanges`                                      |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `oldArchives`                                                 |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `oldClasspath`                                                |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `oneNewVersion`                                               |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `oneOldVersion`                                               |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `removed`                                                     |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `reportOnlyBinaryIncompatibleChanges`                         |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `reportOnlyChanges`                                           |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `reportOnlySummary`                                           |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `semanticallyIncompatibleChanges`                             |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `semanticallyIncompatibleChangesIncludingDevelopmentVersions` |             | ![No changes]         |
| Added  | **`public`** | **[`Map<JApiJavaObjectS…tionChangeStatus, String>`]** | `serializationCompatibility`                                  |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `sourceIncompatibleChanges`                                   |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `statusBinaryIncompatible`                                    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `statusIncompatible`                                          |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `statusModified`                                              |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `statusNew`                                                   |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `statusRemoved`                                               |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `statusSerializationIncompatible`                             |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `statusSourceIncompatible`                                    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `statusUnchanged`                                             |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `summaryMajorChanges`                                         |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `summaryMinorChanges`                                         |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `summaryNoChanges`                                            |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `summaryPatchChanges`                                         |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `treatChangesAsErrors`                                        |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `typeAnnotation`                                              |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `typeClass`                                                   |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `typeEnum`                                                    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `typeInterface`                                               |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `unchanged`                                                   |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `unchecked`                                                   |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `unknownVersion`                                              |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `warningAllMissingClassesIgnored`                             |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `warningSomeMissingClassesIgnored`                            |             | ![No changes]         |
| Added  | **`public`** | **[`String`]**                                        | `yes`                                                         |             | ![No changes]         |

___

<a id="user-content-japicmp.output.markdown.config.markdownoptions"></a>
### `japicmp.output.markdown.config.MarkdownOptions`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                  | Extends        | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|-----------------------|----------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`MarkdownOptions`** | **[`Object`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics | Type                    | Method                                | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|----------|-------------------------|---------------------------------------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** |          | **[`MarkdownOptions`]** | **`newDefault`**()                    |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`MarkdownOptions`]** | **`newDefault`**([`Options`])         |             |        | ![Method added to public class] |
| Added  | **`public`**              |          | **`void`**              | **`setTargetNewVersion`**([`String`]) |             |        | ![Method added to public class] |
| Added  | **`public`**              |          | **`void`**              | **`setTargetOldVersion`**([`String`]) |             |        | ![Method added to public class] |


#### Fields

| Status | Modifiers    | Type                           | Name               | Annotations | Compatibility Changes |
|--------|--------------|--------------------------------|--------------------|-------------|-----------------------|
| Added  | **`public`** | **[`MarkdownHeaderOptions`]**  | `header`           |             | ![No changes]         |
| Added  | **`public`** | **[`MarkdownMessageOptions`]** | `message`          |             | ![No changes]         |
| Added  | **`public`** | **[`Options`]**                | `options`          |             | ![No changes]         |
| Added  | **`public`** | **[`MarkdownSortOptions`]**    | `sort`             |             | ![No changes]         |
| Added  | **`public`** | **[`Optional<String>`]**       | `targetNewVersion` |             | ![No changes]         |
| Added  | **`public`** | **[`Optional<String>`]**       | `targetOldVersion` |             | ![No changes]         |
| Added  | **`public`** | **[`MarkdownTitleOptions`]**   | `title`            |             | ![No changes]         |

___

<a id="user-content-japicmp.output.markdown.config.markdownsortoptions"></a>
### `japicmp.output.markdown.config.MarkdownSortOptions`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                      | Extends        | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|---------------------------|----------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`MarkdownSortOptions`** | **[`Object`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Constructors

| Status | Modifiers    | Generics | Constructor                 | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|-----------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`MarkdownSortOptions`**() |             |        | ![No changes]         |


#### Fields

| Status | Modifiers    | Type                                         | Name           | Annotations | Compatibility Changes |
|--------|--------------|----------------------------------------------|----------------|-------------|-----------------------|
| Added  | **`public`** | **[`Comparator<JApiAnnotation>`]**           | `annotations`  |             | ![No changes]         |
| Added  | **`public`** | **[`Comparator<JApiClass>`]**                | `classes`      |             | ![No changes]         |
| Added  | **`public`** | **[`Comparator<JApiConstructor>`]**          | `constructors` |             | ![No changes]         |
| Added  | **`public`** | **[`Comparator<JApiField>`]**                | `fields`       |             | ![No changes]         |
| Added  | **`public`** | **[`Comparator<JApiGenericTemplate>`]**      | `generics`     |             | ![No changes]         |
| Added  | **`public`** | **[`Comparator<JApiImplementedInterface>`]** | `interfaces`   |             | ![No changes]         |
| Added  | **`public`** | **[`Comparator<JApiMethod>`]**               | `methods`      |             | ![No changes]         |

___

<a id="user-content-japicmp.output.markdown.config.markdowntitleoptions"></a>
### `japicmp.output.markdown.config.MarkdownTitleOptions`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                       | Extends        | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|----------------------------|----------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`MarkdownTitleOptions`** | **[`Object`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Constructors

| Status | Modifiers    | Generics | Constructor                  | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`MarkdownTitleOptions`**() |             |        | ![No changes]         |


#### Fields

| Status | Modifiers    | Type           | Name              | Annotations | Compatibility Changes |
|--------|--------------|----------------|-------------------|-------------|-----------------------|
| Added  | **`public`** | **[`String`]** | `annotations`     |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `constructors`    |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `fields`          |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `generics`        |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `interfaces`      |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `methods`         |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `report`          |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `results`         |             | ![No changes]         |
| Added  | **`public`** | **[`String`]** | `summary`         |             | ![No changes]         |
| Added  | **`public`** | **`int`**      | `topHeadingLevel` |             | ![No changes]         |

___

<a id="user-content-japicmp.util.japiclassfileformatversionhelper"></a>
### `japicmp.util.JApiClassFileFormatVersionHelper`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                                   | Extends        | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|----------------------------------------|----------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`JApiClassFileFormatVersionHelper`** | **[`Object`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics | Type           | Method                                                 | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|----------|----------------|--------------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** |          | **[`String`]** | **`getNewJdkVersion`**([`JApiClassFileFormatVersion`]) |             |        | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`getOldJdkVersion`**([`JApiClassFileFormatVersion`]) |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.util.membervaluehelper"></a>
### `japicmp.util.MemberValueHelper`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                    | Extends        | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|-------------------------|----------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`MemberValueHelper`** | **[`Object`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics | Type           | Method                                              | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|----------|----------------|-----------------------------------------------------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** |          | **[`String`]** | **`formatMemberValue`**([`MemberValue`], `boolean`) |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.util.modifierhelper"></a>
### `japicmp.util.ModifierHelper`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name             | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `ModifierHelper` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics | Type                     | Method | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|----------|--------------------------|--------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** |          | **[`Optional<String>`]** | **`getNewModifierName`**([`JApiModifier<? extends Enum<? extends Enum<? extends ?>>>`]) |  |  | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`Optional<String>`]** | **`getOldModifierName`**([`JApiModifier<? extends Enum<? extends Enum<? extends ?>>>`]) |  |  | ![Method added to public class] |

___

<a id="user-content-japicmp.util.typenamehelper"></a>
### `japicmp.util.TypeNameHelper`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status | Modifiers    | Type      | Name                 | Extends        | JDK       | Serialization       | Compatibility Changes |
|--------|--------------|-----------|----------------------|----------------|-----------|---------------------|-----------------------|
| Added  | **`public`** | **Class** | **`TypeNameHelper`** | **[`Object`]** | **JDK 8** | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics | Type           | Method | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|----------|----------------|--------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** |          | **[`String`]** | **`formatGenericTemplate`**([`String`], [`String`], [`List<JApiGenericType>`], `boolean`) |  |  | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`formatParameterTypeName`**([`JApiBehavior`], [`JApiParameter`], [`Optional<VarargsModifier>`], [`List<JApiGenericType>`], `boolean`) |  |  | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`formatTypeName`**([`String`], [`List<JApiGenericType>`], `int`) |  |  | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`formatTypeName`**([`String`], [`JApiHasChangeStatus`], [`JApiHasGenericTemplates`], `int`) |  |  | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`formatTypeName`**([`String`], [`List<JApiGenericType>`], `boolean`) |  |  | ![Method added to public class] |
| Added  | **`static`** **`public`** |          | **[`String`]** | **`formatTypeName`**([`String`], [`JApiHasChangeStatus`], [`JApiHasGenericTemplates`], `boolean`) |  |  | ![Method added to public class] |


</details>


___

*Generated on: 2024-08-20 21:23:02.884+0000*.

[Method added to public class]: https://img.shields.io/badge/Method_added_to_public_class-yellow "Method added to public class"
[No changes]: https://img.shields.io/badge/No_changes-green "No changes"
[Not serializable]: https://img.shields.io/badge/Not_serializable-green "Not serializable"
[`Collector<CharSequence, ?, String>`]: # "java.util.stream.Collector<java.lang.CharSequence, ?, java.lang.String>"
[`Comparator<JApiAnnotation>`]: # "java.util.Comparator<japicmp.model.JApiAnnotation>"
[`Comparator<JApiClass>`]: # "java.util.Comparator<japicmp.model.JApiClass>"
[`Comparator<JApiConstructor>`]: # "java.util.Comparator<japicmp.model.JApiConstructor>"
[`Comparator<JApiField>`]: # "java.util.Comparator<japicmp.model.JApiField>"
[`Comparator<JApiGenericTemplate>`]: # "java.util.Comparator<japicmp.model.JApiGenericTemplate>"
[`Comparator<JApiImplementedInterface>`]: # "java.util.Comparator<japicmp.model.JApiImplementedInterface>"
[`Comparator<JApiMethod>`]: # "java.util.Comparator<japicmp.model.JApiMethod>"
[`JApiBehavior`]: # "japicmp.model.JApiBehavior"
[`JApiClassFileFormatVersion`]: # "japicmp.model.JApiClassFileFormatVersion"
[`JApiHasChangeStatus`]: # "japicmp.model.JApiHasChangeStatus"
[`JApiHasGenericTemplates`]: # "japicmp.model.JApiHasGenericTemplates"
[`JApiModifier<? extends Enum<? extends Enum<? extends ?>>>`]: # "japicmp.model.JApiModifier<? extends java.lang.Enum<? extends java.lang.Enum<? extends ?>>>"
[`JApiParameter`]: # "japicmp.model.JApiParameter"
[`JApiSemanticVersionLevel`]: # "japicmp.model.JApiSemanticVersionLevel"
[`List<JApiClass>`]: # "java.util.List<japicmp.model.JApiClass>"
[`List<JApiGenericType>`]: # "java.util.List<japicmp.model.JApiGenericType>"
[`Map<JApiCompatibilityChangeType, String>`]: # "java.util.Map<japicmp.model.JApiCompatibilityChangeType, java.lang.String>"
[`Map<JApiJavaObjectS…tionChangeStatus, String>`]: # "java.util.Map<japicmp.model.JApiJavaObjectSerializationCompatibility$JApiJavaObjectSerializationChangeStatus, java.lang.String>"
[`MarkdownHeaderOptions`]: # "japicmp.output.markdown.config.MarkdownHeaderOptions"
[`MarkdownMessageOptions`]: # "japicmp.output.markdown.config.MarkdownMessageOptions"
[`MarkdownOptions`]: # "japicmp.output.markdown.config.MarkdownOptions"
[`MarkdownRefImage`]: # "japicmp.output.markdown.MarkdownRefImage"
[`MarkdownReferences`]: # "japicmp.output.markdown.MarkdownReferences"
[`MarkdownSortOptions`]: # "japicmp.output.markdown.config.MarkdownSortOptions"
[`MarkdownTitleOptions`]: # "japicmp.output.markdown.config.MarkdownTitleOptions"
[`Markdown`]: # "japicmp.output.markdown.Markdown"
[`MemberValue`]: # "javassist.bytecode.annotation.MemberValue"
[`Object`]: # "java.lang.Object"
[`Optional<ClassType>`]: # "japicmp.util.Optional<japicmp.model.JApiClassType$ClassType>"
[`Optional<String>`]: # "japicmp.util.Optional<java.lang.String>"
[`Optional<VarargsModifier>`]: # "japicmp.util.Optional<japicmp.model.VarargsModifier>"
[`Options`]: # "japicmp.config.Options"
[`OutputGenerator<T>`]: # "japicmp.output.OutputGenerator<T extends java.lang.Object>"
[`String`]: # "java.lang.String"
[japicmp.config.Options]: #user-content-japicmp.config.options
[japicmp.output.markdown.Markdown]: #user-content-japicmp.output.markdown.markdown
[japicmp.output.markdown.MarkdownBadge]: #user-content-japicmp.output.markdown.markdownbadge
[japicmp.output.markdown.MarkdownOutputGenerator]: #user-content-japicmp.output.markdown.markdownoutputgenerator
[japicmp.output.markdown.config.MarkdownHeaderOptions]: #user-content-japicmp.output.markdown.config.markdownheaderoptions
[japicmp.output.markdown.config.MarkdownMessageOptions]: #user-content-japicmp.output.markdown.config.markdownmessageoptions
[japicmp.output.markdown.config.MarkdownOptions]: #user-content-japicmp.output.markdown.config.markdownoptions
[japicmp.output.markdown.config.MarkdownSortOptions]: #user-content-japicmp.output.markdown.config.markdownsortoptions
[japicmp.output.markdown.config.MarkdownTitleOptions]: #user-content-japicmp.output.markdown.config.markdowntitleoptions
[japicmp.util.JApiClassFileFormatVersionHelper]: #user-content-japicmp.util.japiclassfileformatversionhelper
[japicmp.util.MemberValueHelper]: #user-content-japicmp.util.membervaluehelper
[japicmp.util.ModifierHelper]: #user-content-japicmp.util.modifierhelper
[japicmp.util.TypeNameHelper]: #user-content-japicmp.util.typenamehelper
