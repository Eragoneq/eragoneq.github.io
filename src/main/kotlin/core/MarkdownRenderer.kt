package core

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

/**
 * Renders a Markdown string (without front matter) to HTML
 * using the JetBrains Markdown library with CommonMark flavour.
 */
fun renderMarkdown(markdownBody: String): String {
    val flavour = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdownBody)
    val html = HtmlGenerator(markdownBody, parsedTree, flavour).generateHtml()

    // The library wraps output in <body>...</body> — strip it since we embed in our own <main>
    return html
        .removePrefix("<body>")
        .removeSuffix("</body>")
        .trim()
}
