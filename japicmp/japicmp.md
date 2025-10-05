
# Compatibility Report

![semver MAJOR](https://img.shields.io/badge/semver-MAJOR-red?logo=semver "semver MAJOR")

## Summary

> [!CAUTION]
>
> Incompatible changes found while checking backward compatibility of version `0.24.2-SNAPSHOT` with the previous version `0.24.0`.

<details markdown="1">
<summary>Expand to see options used.</summary>

- **Report only summary**: No
- **Report only changes**: Yes
- **Report only binary-incompatible changes**: No
- **Access modifier filter**: `PROTECTED`
- **Old archives**:
  - ![japicmp 0.24.0](https://img.shields.io/badge/japicmp-0.24.0-blue "japicmp 0.24.0")
- **New archives**:
  - ![japicmp 0.24.2-SNAPSHOT](https://img.shields.io/badge/japicmp-0.24.2_SNAPSHOT-blue "japicmp 0.24.2-SNAPSHOT")
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

| Status   | Type                                  | Serialization       | Compatibility Changes |
|----------|---------------------------------------|---------------------|-----------------------|
| Modified | [japicmp.cmp.JarArchiveComparator]    | ![Not serializable] | ![No changes]         |
| Modified | [japicmp.compat.CompatibilityChanges] | ![Not serializable] | ![Constructor removed] |

<details markdown="1">
<summary>Expand for details.</summary>

___

<a id="user-content-japicmp.cmp.jararchivecomparator"></a>
### `japicmp.cmp.JarArchiveComparator`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                   | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `JarArchiveComparator` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |

___

<a id="user-content-japicmp.compat.compatibilitychanges"></a>
### `japicmp.compat.CompatibilityChanges`

- [ ] Binary-compatible
- [ ] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name                   | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|------------------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `CompatibilityChanges` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Constructors

| Status  | Modifiers    | Generics | Constructor                                          | Annotations | Throws | Compatibility Changes |
|---------|--------------|----------|------------------------------------------------------|-------------|--------|-----------------------|
| Removed | ~~`public`~~ |          | ~~`CompatibilityChanges`~~([`JarArchiveComparator`]) |             |        | ![Constructor removed] |
| Added   | **`public`** |          | **`CompatibilityChanges`**([`JarArchiveComparator`], [`JarArchiveComparatorOptions`]) |  |  | ![No changes] |


</details>


___

*Generated on: 2025-10-05 09:43:43.200+0000*.

[Constructor removed]: https://img.shields.io/badge/Constructor_removed-red "Constructor removed"
[No changes]: https://img.shields.io/badge/No_changes-green "No changes"
[Not serializable]: https://img.shields.io/badge/Not_serializable-green "Not serializable"
[`JarArchiveComparatorOptions`]: # "japicmp.cmp.JarArchiveComparatorOptions"
[`JarArchiveComparator`]: # "japicmp.cmp.JarArchiveComparator"
[`Object`]: # "java.lang.Object"
[japicmp.cmp.JarArchiveComparator]: #user-content-japicmp.cmp.jararchivecomparator
[japicmp.compat.CompatibilityChanges]: #user-content-japicmp.compat.compatibilitychanges
