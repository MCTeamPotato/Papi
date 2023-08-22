# Papi
Yea another Fabric API reforged project.
Essential hooks for modding with Fabric ported to MinecraftForge.

Papi is the library for essential hooks and interoperability mechanisms for mods. Examples include:

- Exposing functionality that is useful but difficult to access for many mods such as particles, biomes and dimensions
- Adding events, hooks and APIs to improve interopability between mods.
- Essential features such as registry synchronization and adding information to crash reports.
- An advanced rendering API designed for compatibility with optimization mods and graphics overhaul mods.

## Using Papi to play with mods

Make sure you have installed MinecraftForge first. More information about installing Forge can be
found [here](https://github.com/minecraftforge/minecraftforge/#installing-forge).

The Papi is available for download on the following platforms:
- [GitHub Releases (Not publish)](https://github.com/MCTeamPotato/Papi/releases)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/papi-project)
- [Modrinth (Not publish)](https://modrinth.com/mod/papi-project)

To use Papi, download it from CurseForge.
Standalone CurseForge and Modrinth distributions will be available soon.

The downloaded jar file should be placed in your `mods` folder.

## Using Papi to develop mods

To set up a Forge development environment, check out
the [MinecraftForge docs](https://docs.minecraftforge.net/en/latest/gettingstarted) and follow the instructions there.

~~The Papi is published under the `com.github.MCTeamPotato` group.~~ To include the full Papi
with all modules in the development environment, add the following to your `dependencies` block in the gradle
buildscript:

### Groovy DSL

```groovy
repositories {
    maven {
        url "https://www.jitpack.io"
    }
}
dependencies {
    implementation fg.deobf("com.github.MCTeamPotato:Papi:PAPI_VERSION")
}
```

### Kotlin DSL

```kotlin
repositories {
    maven("https://www.jitpack.io")
}
dependencies {
    implementation fg.deobf("com.github.MCTeamPotato:Papi:PAPI_VERSION")
}
```

<!--Linked to gradle documentation on properties-->
Instead of hardcoding version constants all over the build script, Gradle properties may be used to replace these
constants. Properties are defined in the `gradle.properties` file at the root of a project. More information is
available [here](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#declare_properties_in_gradle_properties_file).

## Modules

Fabric API is designed to be modular for ease of updating. We will move to modular when papi completed. ~~This also has the advantage of splitting up the codebase into
smaller chunks.~~

Each module contains its own `README.md`* explaining the module's purpose and additional info on using the module.

\* The README for each module is being worked on; not every module has a README at the moment

## Porting Status
| API                                  |          State           |  Lifecycle   |
|:-------------------------------------|:------------------------:|:------------:|
| fabric-api-base                      |         ✅ Tested         |    Stable    |
| fabric-api-lookup-api-v1             |        ⚠️ Missing        |    Stable    |
| fabric-biome-api-v1                  |        ⚠️ Missing        | Experimental |
| fabric-block-api-v1                  |        ⚠️ Missing        |    Stable    |
| fabric-blockrenderlayer-v1           |        ⚠️ Missing        |    Stable    |
| fabric-client-tags-api-v1            |        ⚠️ Missing        |    Stable    |
| fabric-command-api-v2                |         ✅ Tested         |    Stable    |
| fabric-content-registries-v0         |        ⚠️ Missing        |    Stable    |
| fabric-convention-tags-v1            |        ⚠️ Missing        |    Stable    |
| fabric-crash-report-info-v1          | 🚧 Not Planned [[1]](#1) |    Stable    |
| fabric-data-generation-api-v1        |        ⚠️ Missing        |    Stable    |
| fabric-dimensions-v1                 |        ⚠️ Missing        |    Stable    |
| fabric-entity-events-v1              |        ⚠️ Missing        |    Stable    |
| fabric-events-interaction-v0         |        ⚠️ Missing        |    Stable    |
| fabric-game-rule-api-v1              |        ⚠️ Missing        |    Stable    |
| fabric-gametest-api-v1               | 🚧 Not Planned [[2]](#2) |    Stable    |
| fabric-item-api-v1                   |       🛠️ Testing        |    Stable    |
| fabric-item-group-api-v1             |       🛠️ Testing        |    Stable    |
| fabric-key-binding-api-v1            |       🛠️ Testing        |    Stable    |
| fabric-lifecycle-events-v1           |       🛠️ Testing        |    Stable    |
| fabric-loot-api-v2                   |        ⚠️ Missing        |    Stable    |
| fabric-message-api-v1                |        ⚠️ Missing        | Experimental |
| fabric-mining-level-api-v1           |        ⚠️ Missing        |    Stable    |
| fabric-model-loading-api-v1          |        ⚠️ Missing        |    Stable    |
| fabric-networking-api-v1             |       🛠️ Testing        |    Stable    |
| fabric-object-builder-api-v1         |        ⚠️ Missing        |    Stable    |
| fabric-particles-v1                  |        ⚠️ Missing        |    Stable    |
| fabric-recipe-api-v1                 |        ⚠️ Missing        |    Stable    |
| fabric-registry-sync-v0              |        ⚠️ Missing        |    Stable    |
| fabric-renderer-api-v1               |       🛠️ Testing        |    Stable    |
| fabric-renderer-indigo               |       🛠️ Testing        |    Stable    |
| fabric-rendering-data-attachment-v1  |       🛠️ Testing        |    Stable    |
| fabric-rendering-fluids-v1           |        ⚠️ Missing        |    Stable    |
| fabric-rendering-v1                  |       🛠️ Testing        |    Stable    |
| fabric-resource-conditions-api-v1    |        ⚠️ Missing        | Experimental |
| fabric-resource-loader-v0            |       🛠️ Testing        |    Stable    |
| fabric-screen-api-v1                 |       🛠️ Testing        |    Stable    |
| fabric-screen-handler-api-v1         |       🛠️ Testing        |    Stable    |
| fabric-sound-api-v1                  |        ⚠️ Missing        |    Stable    |
| fabric-transfer-api-v1               |        ⚠️ Missing        | Experimental |
| fabric-transitive-access-wideners-v1 |        ⚠️ Missing        |    Stable    |
| fabric-command-api-v1                |       🛠️ Testing        |  Deprecated  |
| fabric-commands-v0                   |       🛠️ Testing        |  Deprecated  |
| fabric-containers-v0                 |        ⚠️ Missing        |  Deprecated  |
| fabric-events-lifecycle-v0           |       🛠️ Testing        |  Deprecated  |
| fabric-keybindings-v0                |       🛠️ Testing        |  Deprecated  |
| fabric-loot-tables-v1                |        ⚠️ Missing        |  Deprecated  |
| fabric-models-v0                     |        ⚠️ Missing        |    Stable    |
| fabric-networking-v0                 |        ⚠️ Missing        |  Deprecated  |
| fabric-renderer-registries-v1        |        ⚠️ Missing        |  Deprecated  |
| fabric-rendering-v0                  |        ⚠️ Missing        |  Deprecated  |

<a id="1">[1]</a> Does not provide an API, features already implemented by FML.<br>
<a id="2">[2]</a> Forge's <a href="https://forge.gemwire.uk/wiki/Game_Tests">GameTest runner</a> provides the same capabilities.