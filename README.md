# Papi

Yea another Fabric API reforged project. (WIP)

Work on 1.16.5, 1.18.2, 1.19.2 minecraft version.

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

| API                                  | State                    |
|:------------------------------------:|:------------------------:|
| fabric-api-base                      | ✅ Tested                 |
| fabric-api-lookup-api-v1             | 🛠️ Testing              |
| fabric-biome-api-v1                  | ⚠️ Missing               |
| fabric-block-api-v1                  | ⚠️ Missing               |
| fabric-blockrenderlayer-v1           | ⚠️ Missing               |
| fabric-command-api-v1                | 🛠️ Testing              |
| fabric-commands-v0                   | 🛠️ Testing              |
| fabric-containers-v0                 | ⚠️ Missing               |
| fabric-content-registries-v0         | ⚠️ Missing               |
| fabric-convention-tags-v1            | ⚠️ Missing               |
| fabric-crash-report-info-v1          | 🚧 Not Planned [[1]](#1) |
| fabric-data-generation-api-v1        | ⚠️ Missing               |
| fabric-dimensions-v1                 | ⚠️ Missing               |
| fabric-entity-events-v1              | ⚠️ Missing               |
| fabric-events-interaction-v0         | 🛠️ Testing              |
| fabric-events-lifecycle-v0           | 🛠️ Testing              |
| fabric-game-rule-api-v1              | ⚠️ Missing               |
| fabric-gametest-api-v1               | 🚧 Not Planned [[2]](#2) |
| fabric-item-api-v1                   | 🛠️ Testing              |
| fabric-item-groups-v0                | 🛠️ Testing              |
| fabric-key-binding-api-v1            | 🛠️ Testing              |
| fabric-keybindings-v0                | 🛠️ Testing              |
| fabric-lifecycle-events-v1           | 🛠️ Testing              |
| fabric-loot-api-v2                   | ⚠️ Missing               |
| fabric-loot-tables-v1                | ⚠️ Missing               |
| fabric-mining-level-api-v1           | 🛠️ Testing              |
| fabric-models-v0                     | ⚠️ Missing               |
| fabric-networking-v0                 | 🛠️ Testing              |
| fabric-object-builder-api-v1         | ⚠️ Missing               |
| fabric-particles-v1                  | ⚠️ Missing               |
| fabric-registry-sync-v0              | ⚠️ Missing               |
| fabric-renderer-api-v1               | 🛠️ Testing              |
| fabric-renderer-indigo               | 🛠️ Testing              |
| fabric-renderer-registries-v1        | ⚠️ Missing               |
| fabric-rendering-data-attachment-v1  | 🛠️ Testing              |
| fabric-rendering-fluids-v1           | ⚠️ Missing               |
| fabric-rendering-v0                  | ⚠️ Missing               |
| fabric-rendering-v1                  | 🛠️ Testing              |
| fabric-resource-conditions-api-v1    | ⚠️ Missing               |
| fabric-resource-loader-v0            | 🛠️ Testing              |
| fabric-screen-api-v1                 | 🛠️ Testing              |
| fabric-screen-handler-api-v1         | 🛠️ Testing              |
| fabric-transfer-api-v1               | ⚠️ Missing               |
| fabric-transitive-access-wideners-v1 | ⚠️ Missing               |

<a id="1">[1]</a> Does not provide an API, features already implemented by FML.<br>
<a id="2">[2]</a> Forge's <a href="https://forge.gemwire.uk/wiki/Game_Tests">GameTest runner</a> provides the same capabilities.




