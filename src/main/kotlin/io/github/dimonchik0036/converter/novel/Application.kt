package io.github.dimonchik0036.converter.novel

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody

fun main(args: Array<String>) = mainBody {
    val config = ArgParser(args).parseInto(::NovelConfig)
    Novel(config).run()
}
