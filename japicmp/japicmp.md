
# Compatibility Report

![semver MAJOR](https://img.shields.io/badge/semver-MAJOR-red?logo=semver "semver MAJOR")

## Summary

> [!CAUTION]
>
> Incompatible changes found while checking backward compatibility of version `0.24.1-SNAPSHOT` with the previous version `0.23.1`.

<details markdown="1">
<summary>Expand to see options used.</summary>

- **Report only summary**: No
- **Report only changes**: Yes
- **Report only binary-incompatible changes**: No
- **Access modifier filter**: `PROTECTED`
- **Old archives**:
  - ![japicmp 0.23.1](https://img.shields.io/badge/japicmp-0.23.1-blue "japicmp 0.23.1")
- **New archives**:
  - ![japicmp 0.24.1-SNAPSHOT](https://img.shields.io/badge/japicmp-0.24.1_SNAPSHOT-blue "japicmp 0.24.1-SNAPSHOT")
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

| Status   | Type                                        | Serialization       | Compatibility Changes |
|----------|---------------------------------------------|---------------------|-----------------------|
| Modified | [japicmp.compat.CompatibilityChanges]       | ![Not serializable] | ![No changes]         |
| Modified | [japicmp.model.JApiClass]                   | ![Not serializable] | ![Method added to public class] |
| Modified | [japicmp.model.JApiCompatibilityChangeType] | ![Field removed]    | ![Field removed][1]   |

<details markdown="1">
<summary>Expand for details.</summary>

___

<a id="user-content-japicmp.compat.compatibilitychanges"></a>
### `japicmp.compat.CompatibilityChanges`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                   | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `CompatibilityChanges` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |

___

<a id="user-content-japicmp.model.japiclass"></a>
### `japicmp.model.JApiClass`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name        | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|-------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JApiClass` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers    | Generics | Type          | Method                       | Annotations | Throws | Compatibility Changes |
|--------|--------------|----------|---------------|------------------------------|-------------|--------|-----------------------|
| Added  | **`public`** |          | **`boolean`** | **`isNewClassExtendable`**() |             |        | ![Method added to public class] |
| Added  | **`public`** |          | **`boolean`** | **`isOldClassExtendable`**() |             |        | ![Method added to public class] |

___

<a id="user-content-japicmp.model.japicompatibilitychangetype"></a>
### `japicmp.model.JApiCompatibilityChangeType`

- [ ] Binary-compatible
- [ ] Source-compatible
- [ ] Serialization-compatible

| Status   | Modifiers        | Type | Name                          | Extends     | JDK   | Serialization    | Compatibility Changes |
|----------|------------------|------|-------------------------------|-------------|-------|------------------|-----------------------|
| Modified | `final` `public` | Enum | `JApiCompatibilityChangeType` | [`Enum<E>`] | JDK 8 | ![Field removed] | ![No changes]         |


#### Fields

| Status  | Modifiers                             | Type                                | Name                       | Annotations | Compatibility Changes |
|---------|---------------------------------------|-------------------------------------|----------------------------|-------------|-----------------------|
| Removed | ~~`public`~~ ~~`static`~~ ~~`final`~~ | ~~[`JApiCompatibilityChangeType`]~~ | `CLASS_NOW_FINAL`          |             | ![Field removed][1]   |
| Added   | **`public`** **`static`** **`final`** | **[`JApiCompatibilityChangeType`]** | `CLASS_NOW_NOT_EXTENDABLE` |             | ![No changes]         |


</details>


___

*Generated on: 2025-10-03 14:10:43.235+0000*.

[1]: https://img.shields.io/badge/Field_removed-red "Field removed"
[Field removed]: https://img.shields.io/badge/Incompatible-red "Field removed"
[Method added to public class]: https://img.shields.io/badge/Method_added_to_public_class-yellow "Method added to public class"
[No changes]: https://img.shields.io/badge/No_changes-green "No changes"
[Not serializable]: https://img.shields.io/badge/Not_serializable-green "Not serializable"
[`Enum<E>`]: # "java.lang.Enum<E extends java.lang.Enum<E>>"
[`JApiCompatibilityChangeType`]: # "japicmp.model.JApiCompatibilityChangeType"
[`Object`]: # "java.lang.Object"
[japicmp.compat.CompatibilityChanges]: #user-content-japicmp.compat.compatibilitychanges
[japicmp.model.JApiClass]: #user-content-japicmp.model.japiclass
[japicmp.model.JApiCompatibilityChangeType]: #user-content-japicmp.model.japicompatibilitychangetype
