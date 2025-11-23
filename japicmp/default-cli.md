
# Compatibility Report

![semver PATCH](https://img.shields.io/badge/semver-PATCH-yellow?logo=semver "semver PATCH")

## Summary

> [!IMPORTANT]
>
> Compatible bug fixes found while checking backward compatibility of version `0.25.0-SNAPSHOT` with the previous version `0.24.2`.

<details markdown="1">
<summary>Expand to see options used.</summary>

- **Report only summary**: No
- **Report only changes**: Yes
- **Report only binary-incompatible changes**: No
- **Access modifier filter**: `PROTECTED`
- **Old archives**:
  - ![japicmp 0.24.2](https://img.shields.io/badge/japicmp-0.24.2-blue "japicmp 0.24.2")
- **New archives**:
  - ![japicmp 0.25.0-SNAPSHOT](https://img.shields.io/badge/japicmp-0.25.0_SNAPSHOT-blue "japicmp 0.25.0-SNAPSHOT")
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

| Status   | Type                      | Serialization       | Compatibility Changes |
|----------|---------------------------|---------------------|-----------------------|
| Modified | [japicmp.util.FileHelper] | ![Not serializable] | ![Method added to public class] |

<details markdown="1">
<summary>Expand for details.</summary>

___

<a id="user-content-japicmp.util.filehelper"></a>
### `japicmp.util.FileHelper`

- [X] Binary-compatible
- [X] Source-compatible
- [X] Serialization-compatible

| Status   | Modifiers | Type  | Name         | Extends    | JDK   | Serialization       | Compatibility Changes |
|----------|-----------|-------|--------------|------------|-------|---------------------|-----------------------|
| Modified | `public`  | Class | `FileHelper` | [`Object`] | JDK 8 | ![Not serializable] | ![No changes]         |


#### Methods

| Status | Modifiers                 | Generics | Type           | Method                       | Annotations | Throws | Compatibility Changes |
|--------|---------------------------|----------|----------------|------------------------------|-------------|--------|-----------------------|
| Added  | **`static`** **`public`** |          | **[`String`]** | **`guessVersion`**([`File`]) |             |        | ![Method added to public class] |


</details>


___

*Generated on: 2025-11-23 12:24:52.125+0000*.

[Method added to public class]: https://img.shields.io/badge/Method_added_to_public_class-yellow "Method added to public class"
[No changes]: https://img.shields.io/badge/No_changes-green "No changes"
[Not serializable]: https://img.shields.io/badge/Not_serializable-green "Not serializable"
[`File`]: # "java.io.File"
[`Object`]: # "java.lang.Object"
[`String`]: # "java.lang.String"
[japicmp.util.FileHelper]: #user-content-japicmp.util.filehelper
