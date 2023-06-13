# How to contribute

This document well help you getting started on contributing to the Wanderer project!

## Translations

You can contribute to translating the game on [my Weblate](https://weblate.vinceh121.me/)

If adding texts that are originally in Project Nomads, please respect the original text.

## Code

The technology stack:
 - Engine is in Java and based on LibGDX
 - Gameplay scripts are in JavaScript, you can consult [the story book conversion guide](/book-part-conversion-guide.md)
 - Graphics run on OpenGL ES 3.0, with GLSL shaders
 - Asset formats are:
   - OBJ or glTF 2.0 for models, converted from Nebula's NVX
   - KTX 1 for textures, converted from Nebula's NTX
   - glTF 2.0 for animations, converted from Nebula's NAX

To setup your workspace, please consult the [import and running](https://libgdx.com/wiki/start/import-and-running) article from LibGDX's wiki.
TL;DR: it's importing a multi-project Gradle workspace in your favorite Java IDE.

### What can I contribute to?

Any contribution adding a feature or fixing a bug in the gameplay scripts or the engine is always welcome!

### Branches?

You can base your contributions on the `master` branch.

