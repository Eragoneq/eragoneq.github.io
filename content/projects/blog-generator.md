---
name: Blog Generator
description: A small static site generator written in Kotlin
date: 2026-08-28
last_update: 2026-08-28
status: Active
---

# Blog Generator

A custom static site generator for my personal website. Used as a replacement for my previous workflow that utilized Hugo with PaperMod.
The previous workflow was too slow and annoying for my needs, so I decided to create a lightweight generator that could handle my specific requirements.

The choice to use Kotlin was made because of my familiarity with the language, and I find the JVM to be quite good in terms of stability, so I wouldn't have to worry about the generator breaking due to changes in the language or libraries.


## Tech Stack

- **Kotlin** with kotlinx-html DSL for type-safe HTML generation
- **JetBrains Markdown** for CommonMark parsing
- **Clikt** for CLI argument handling
- **SnakeYAML** for front matter parsing
- **Kotlinx Coroutines** for asynchronous file I/O
- **Webpack** for bundling and optimizing static assets

## Features

- Markdown to HTML conversion with YAML front matter
- Blog posts and project pages
- RSS Feed and static sitemap generation
- HTML minification and optimization
- Static asset pipeline (CSS, JS, fonts, images)
- Configurable input/output directories via CLI
