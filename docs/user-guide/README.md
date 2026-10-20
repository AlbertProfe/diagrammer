# Diagrammer user guide

Diagrammer is for self-contained teaching pages: interactive HTML diagrams in the style of the [PersonCarDog](https://albertprofe.github.io/PersonCarDog/) pages. In v0.0.2 you sign in and choose what the diagram is about. Generating the page comes later.

This guide describes release **v0.0.2**. The pictures were taken from a local run of that release, at a desktop width of 1280 pixels. The pictures of the default chips and of the changed chips show the whole page, which is taller than one window. The pictures of blocked chips show the chip row and its note.

## What you can do now

| Release | What works |
| --- | --- |
| v0.0.1 | Sign in, see your name, and log out. |
| v0.0.2 | On the Diagram Orchestrator, pick one chip in each group. The Pre-Profile card updates as you click. Some combinations are blocked, and the greyed-out chip explains why. **Confirm & Generate** is on the screen and stays disabled. The Profile assistant panel reads "Profile assistant coming soon". |

Diagram text is English.

## Coming later

These stories are not on the screen yet. This guide does not give dates for them.

| What you will do | Story |
| --- | --- |
| Type a title | S3 ([issue #5](https://github.com/AlbertProfe/diagrammer/issues/5)) |
| State the topic in the Profile assistant | S4 ([issue #6](https://github.com/AlbertProfe/diagrammer/issues/6)) |
| Light and dark theme for the diagram | S15 ([issue #17](https://github.com/AlbertProfe/diagrammer/issues/17)) |
| Choose template rendering or a model | S16 ([issue #18](https://github.com/AlbertProfe/diagrammer/issues/18)) |
| See the result | S11 ([issue #13](https://github.com/AlbertProfe/diagrammer/issues/13)) |
| Share a public link | S9 ([issue #11](https://github.com/AlbertProfe/diagrammer/issues/11)) |
| Open the diagram page (horizontal steps, U-shape, pyramid) | S8, S13, S14 ([issues #10](https://github.com/AlbertProfe/diagrammer/issues/10), [#15](https://github.com/AlbertProfe/diagrammer/issues/15), [#16](https://github.com/AlbertProfe/diagrammer/issues/16)) |
| Regenerate as a new version (Tinker) | S12 ([issue #14](https://github.com/AlbertProfe/diagrammer/issues/14)) |
| My diagrams, and switch versions | S17 ([issue #19](https://github.com/AlbertProfe/diagrammer/issues/19)) |

Two pages from this guide are waiting on those stories:

- Viewing and sharing: coming later
- Versions: coming later

## Pages

- [Signing in](signing-in.md)
- [Creating a diagram](creating-a-diagram.md)
- [Pre-Profile options](reference/pre-profile-options.md)

## Words this guide uses

**Diagram Orchestrator.** The screen you land on after you sign in. The heading is "Diagram Orchestrator".

**Pre-Profile.** The card on that screen. It lists the title, the six choices, and the topic. In v0.0.2 the title and the topic say "not set yet".

**Create and Tinker.** Create is a diagram that does not exist yet: you set the profile, then generate version v1. Tinker is a diagram that already has versions: you adjust the profile and generate the next version. v0.0.2 is the start of Create (the chips only). Tinker is coming later.

**Two kinds of version.** An app release is v0.0.1 or v0.0.2: the product you are reading about. A diagram version is v1, v2, v3, and so on: one generate, kept as it was. Generating the next one does not overwrite the earlier one. Diagram versions are coming later.

The chip note "Comparison layout is v2" uses v2 in a third way: that layout is a later layout, not diagram version v2 and not release v0.0.2.
