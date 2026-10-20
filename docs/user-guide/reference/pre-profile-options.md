# Pre-Profile options

Release v0.0.2. This is the chip list on the Diagram Orchestrator: the label you see, then the value the profile keeps. The [Pre-Profile](../creating-a-diagram.md#the-pre-profile-card) card shows the label. How the screen behaves is in [Creating a diagram](../creating-a-diagram.md).

These are the values in [decision 0033](../../decisions/product/0033-pre-profile-chip-values.md). The labels come from the app.

You pick one chip in each group. Audience is inside **Advanced**, which is open when you arrive. Refresh the page and the chips return to the defaults. v0.0.2 does not save the profile.

## Domain

Subject of the diagram.

| Label | Value | Default |
| --- | --- | --- |
| Java | `java` | yes |
| Spring Boot | `spring-boot` | |
| Python | `python` | |
| ML | `ml` | |
| React | `react` | |

## Goal

What the diagram is for.

| Label | Value | Default |
| --- | --- | --- |
| Explain flow | `explain-flow` | yes |
| Walk through use case | `use-case` | |
| Compare concepts | `compare` | |
| Show layers | `layers` | |
| Other | `other` | |

The longer values `walk-through-use-case`, `compare-concepts`, and `show-layers` are not used. The labels **Walk through use case**, **Compare concepts**, and **Show layers** are the current ones.

## Interactivity

How the reader moves through the diagram.

| Label | Value | Default |
| --- | --- | --- |
| Static | `static` | |
| Step-by-step | `step-by-step` | yes |
| Multi-scenario | `multi-scenario` | |

## Type

Layout of the diagram. Comparison is a later layout. It is not a type in v0.0.2.

| Label | Value | Default |
| --- | --- | --- |
| Auto | `auto` | yes |
| Horizontal steps | `horizontal-steps` | |
| U-shape request/response | `u-shape` | |
| Pyramid | `pyramid` | |

## Depth

How much detail the diagram carries.

| Label | Value | Default |
| --- | --- | --- |
| Overview | `overview` | |
| With code | `with-code` | yes |
| With code + framework parallels | `with-code-parallels` | |

## Audience

Who the diagram is for. Shown under **Advanced**.

| Label | Value | Default |
| --- | --- | --- |
| Beginner | `beginner` | yes |
| Intermediate | `intermediate` | |

## Title and topic

| Row on the card | In v0.0.2 |
| --- | --- |
| Title | **not set yet**. You will type it. Coming later (S3). |
| Topic | **not set yet**. The Profile assistant will ask for one sentence. Coming later (S4). |

## Rules

**Multi-scenario** is allowed only with **U-shape request/response** (`u-shape`) or **Auto** (`auto`).

- Type **Pyramid** or **Horizontal steps** greys out **Multi-scenario**.
- Interactivity **Multi-scenario** greys out **Horizontal steps** and **Pyramid**. **U-shape request/response** and **Auto** stay available.

The note on the greyed-out chip says: "Multi-scenario works with U-shape or Auto only."

**Static** and **Step-by-step** are allowed with every type. They are never greyed out by these rules.

Goal **Compare concepts** (`compare`) forces type **Auto** and greys out **Horizontal steps**, **U-shape request/response**, and **Pyramid**. If another type was selected, it moves back to Auto. The note says: "Comparison layout is v2, so Compare concepts stays on Auto."

In that note, v2 means a later layout. It is not a diagram version and not an app release.

When type is Auto, a later step will suggest a layout. The card does not show that suggestion in v0.0.2. The suggestion is horizontal steps for Explain flow, U-shape for Walk through use case, and pyramid for Show layers. Compare concepts waits until the comparison layout exists.
