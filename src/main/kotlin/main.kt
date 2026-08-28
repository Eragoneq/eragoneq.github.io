import cli.BlogCommands
import cli.GenerateCommand
import cli.ValidateCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {
    BlogCommands()
        .subcommands(GenerateCommand(), ValidateCommand())
        .main(args)
}
