import cli.BlogCommands
import cli.GenerateCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {
    BlogCommands()
        .subcommands(GenerateCommand())
        .main(args)
}