This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

| Nome colore                           | Valore HEX   | Anteprima                |
|---------------------------------------|--------------|--------------------------|
| primaryDarkHighContrast               | #FBFBFB      | <span style="display:inline-block;width:40px;height:20px;background:#FBFBFB;border:1px solid #ccc"></span> |
| onPrimaryDarkHighContrast             | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| primaryContainerDarkHighContrast      | #CBCBCB      | <span style="display:inline-block;width:40px;height:20px;background:#CBCBCB;border:1px solid #ccc"></span> |
| onPrimaryContainerDarkHighContrast    | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| secondaryDarkHighContrast             | #F1FDFF      | <span style="display:inline-block;width:40px;height:20px;background:#F1FDFF;border:1px solid #ccc"></span> |
| onSecondaryDarkHighContrast           | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| secondaryContainerDarkHighContrast    | #64DBED      | <span style="display:inline-block;width:40px;height:20px;background:#64DBED;border:1px solid #ccc"></span> |
| onSecondaryContainerDarkHighContrast  | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| tertiaryDarkHighContrast              | #FFF9FF      | <span style="display:inline-block;width:40px;height:20px;background:#FFF9FF;border:1px solid #ccc"></span> |
| onTertiaryDarkHighContrast            | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| tertiaryContainerDarkHighContrast     | #D6C1FF      | <span style="display:inline-block;width:40px;height:20px;background:#D6C1FF;border:1px solid #ccc"></span> |
| onTertiaryContainerDarkHighContrast   | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| errorDarkHighContrast                 | #FFF9F9      | <span style="display:inline-block;width:40px;height:20px;background:#FFF9F9;border:1px solid #ccc"></span> |
| onErrorDarkHighContrast               | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| errorContainerDarkHighContrast        | #FFBAB1      | <span style="display:inline-block;width:40px;height:20px;background:#FFBAB1;border:1px solid #ccc"></span> |
| onErrorContainerDarkHighContrast      | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| backgroundDarkHighContrast            | #131313      | <span style="display:inline-block;width:40px;height:20px;background:#131313;border:1px solid #ccc"></span> |
| onBackgroundDarkHighContrast          | #E2E2E2      | <span style="display:inline-block;width:40px;height:20px;background:#E2E2E2;border:1px solid #ccc"></span> |
| surfaceDarkHighContrast               | #131313      | <span style="display:inline-block;width:40px;height:20px;background:#131313;border:1px solid #ccc"></span> |
| onSurfaceDarkHighContrast             | #FFFFFF      | <span style="display:inline-block;width:40px;height:20px;background:#FFFFFF;border:1px solid #ccc"></span> |
| surfaceVariantDarkHighContrast        | #4C4546      | <span style="display:inline-block;width:40px;height:20px;background:#4C4546;border:1px solid #ccc"></span> |
| onSurfaceVariantDarkHighContrast      | #FFF9F9      | <span style="display:inline-block;width:40px;height:20px;background:#FFF9F9;border:1px solid #ccc"></span> |
| outlineDarkHighContrast               | #D3C8C9      | <span style="display:inline-block;width:40px;height:20px;background:#D3C8C9;border:1px solid #ccc"></span> |
| outlineVariantDarkHighContrast        | #D3C8C9      | <span style="display:inline-block;width:40px;height:20px;background:#D3C8C9;border:1px solid #ccc"></span> |
| scrimDarkHighContrast                 | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| inverseSurfaceDarkHighContrast        | #E2E2E2      | <span style="display:inline-block;width:40px;height:20px;background:#E2E2E2;border:1px solid #ccc"></span> |
| inverseOnSurfaceDarkHighContrast      | #000000      | <span style="display:inline-block;width:40px;height:20px;background:#000000;border:1px solid #ccc"></span> |
| inversePrimaryDarkHighContrast        | #2A2A2A      | <span style="display:inline-block;width:40px;height:20px;background:#2A2A2A;border:1px solid #ccc"></span> |
| surfaceDimDarkHighContrast            | #131313      | <span style="display:inline-block;width:40px;height:20px;background:#131313;border:1px solid #ccc"></span> |
| surfaceBrightDarkHighContrast         | #393939      | <span style="display:inline-block;width:40px;height:20px;background:#393939;border:1px solid #ccc"></span> |
| surfaceContainerLowestDarkHighContrast| #0E0E0E      | <span style="display:inline-block;width:40px;height:20px;background:#0E0E0E;border:1px solid #ccc"></span> |
| surfaceContainerLowDarkHighContrast   | #1B1B1B      | <span style="display:inline-block;width:40px;height:20px;background:#1B1B1B;border:1px solid #ccc"></span> |
| surfaceContainerDarkHighContrast      | #1F1F1F      | <span style="display:inline-block;width:40px;height:20px;background:#1F1F1F;border:1px solid #ccc"></span> |
| surfaceContainerHighDarkHighContrast  | #2A2A2A      | <span style="display:inline-block;width:40px;height:20px;background:#2A2A2A;border:1px solid #ccc"></span> |
| surfaceContainerHighestDarkHighContrast| #353535     | <span style="display:inline-block;width:40px;height:20px;background:#353535;border:1px solid #ccc"></span> |