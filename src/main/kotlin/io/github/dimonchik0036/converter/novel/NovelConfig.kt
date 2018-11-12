package io.github.dimonchik0036.converter.novel

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import com.xenomachina.argparser.default
import java.io.File

class NovelConfig(parser: ArgParser) {
    val title by parser.positional(
        "TITLE",
        help = "The title part in the URL"
    )

    val link by parser.flagging("-l", "--link", help = "Is a link")

    val destination by parser.storing(
        "-d", "--destination",
        help = "Destination directory, default is current directory"
    ) { File(this) }.default(File(".")).addValidator {
        if (!value.isDirectory) throw SystemExitException("Destination must be directory", 1)
    }
}

