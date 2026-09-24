# Documentation

Everything about this system that is not code lives here, in Markdown, in the same
repository as the code — diffable, reviewable in the same pull request, versioned with
the release it describes, and readable by your AI agent.

| Folder | What goes there |
| --- | --- |
| `src/user-stories/` | Who needs what, and why — in the stakeholder's language |
| `src/specifications/` | What the solution does — precise enough to build and test from |
| `src/manuals/` | Three one-page manuals — database, REST API, frontend — each with the required minimum and where the example meets it |
| `_templates/` | Copy these to start your own story and specification |

This repository's component is the **tea register**: users, a classification
list, and the teas they own. The story and the specification are
[TEA-US-001](user-stories/TEA-US-001.md) and
[TEA.01](specifications/TEA.01-tea-register.md).
