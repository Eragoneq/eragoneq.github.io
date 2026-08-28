# BlogGenerator

A Kotlin static-site generator for the content in `content/` and the assets in
`static/`.

## Local generation

Generate the readable development version:

```shell
./gradlew run --args="generate"
```

On Windows, use `gradlew.bat` instead of `./gradlew`. The CLI also accepts
`--input`, `--output`, `--static`, and `--config` when non-default paths are
needed. Add `--strict` to reject malformed YAML front matter and invalid entry
dates instead of warning and continuing.

## Site configuration

Site metadata and deployment URLs live in `site.yml`. `base_url` is the scheme
and host only. Set `base_path` to a root-relative path such as `/blog` when the
generated files are served below a project path; leave it empty for a domain
root. The generator validates the URL, base path, metadata, and home-page entry
limits before writing output.

## Production build

The production runner requires Node.js 20.9 or newer in addition to the JDK
used by Gradle:

```shell
npm ci
npm run build:public
```

This command runs the existing Kotlin generator into `build/generated-site/`,
with strict content validation enabled, then runs Webpack in production mode.
Webpack cleans `public/`, bundles the CSS import graph into
`static/css/main.css`, minifies HTML, CSS, and JavaScript, and copies the
remaining static and SEO assets. The deployable result is `public/`. The clean
output prevents removed posts or renamed assets from surviving in a later
deployment.

`npm run bundle:public` can rerun only the Webpack stage when the intermediate
site already exists. Validate an existing production output with:

```shell
./gradlew validatePublicSite
```

The validation task checks every generated HTML `href` and `src` that points
inside the site, parses `sitemap.xml` and `feed.xml` securely, and confirms the
sitemap advertised by `robots.txt` matches `site.yml`.

## CI runner

`.github/workflows/build-public.yml` runs the focused generator tests, strict
production build, and generated-site link/XML validation on pushes to `main`,
pull requests, and manual dispatches. It uploads `public/` as the `public-site`
artifact; a host-specific deployment job can consume that artifact later.

The same workflow is intentionally usable by GitHub Actions and Forgejo
Actions. Forgejo falls back to `.github/workflows` when `.forgejo/workflows` is
absent. A self-hosted Forgejo installation must provide an `ubuntu-latest`
runner label (or change `runs-on` to one of its available labels) and access to
the configured action mirror. The workflow uses `upload-artifact@v4` on
GitHub.com and automatically falls back to Forgejo's supported v3 artifact
protocol on self-hosted forges.

## Trusted content assumption

Rendered Markdown remains intentionally unsanitized because content is authored
and reviewed by the site owner. Add sanitization before accepting Markdown from
untrusted contributors or external systems.
