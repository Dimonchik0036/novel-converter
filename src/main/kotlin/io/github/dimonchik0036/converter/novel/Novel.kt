package io.github.dimonchik0036.converter.novel

import awaitStringResponse
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.xenomachina.argparser.SystemExitException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

class Novel(private val config: NovelConfig) {
    fun run() = runBlocking {
        println("Processed ${config.title}")
        if (config.link) {
            downloadPage("$SITE${config.title}")
            return@runBlocking
        }

        getPage("$SITE${config.title}").fold({
            downloadAllChapter(it)
        }, {
            SystemExitException("Novel couldn't find: $it", 1)
        })
    }

    private suspend fun CoroutineScope.downloadAllChapter(mainPage: String) {
        val content = contentReg.findAll(mainPage)
        val isVolume = content.count() > 1

        content.forEachIndexed { index, partOfContent ->
            linkReg.findAll(partOfContent.value).forEach {
                launch {
                    downloadPage(
                        it.value.substringAfter("href=\"").substringBefore("\">"),
                        filePrefix = if (isVolume) "Volume ${index + 1} " else ""
                    )
                }
            }
        }
    }

    private suspend fun downloadPage(link: String, filePrefix: String = "") {
        getPage(link).fold({
            val title = findTitle(it)
            val body = removeUnnecessaryCode(it)
            val chapter = findChapterFromTitle(title)
            saveDocument(title, body, chapter, filePrefix)
        }, {
            System.err.println("Couldn't get page: $it")
        })
    }

    private fun saveDocument(title: String, body: String, chapter: String, filePrefix: String) = try {
        val file = File(config.destination, "${filePrefix}Chapter $chapter.html")
        file.writeText("<html><head><meta charset=\"utf-8\">$title</head><body><div>$body</div></body></html>")
        println("${filePrefix}Chapter $chapter saved")
    } catch (e: Exception) {
        System.err.println("Couldn't save ${filePrefix}chapter $chapter: $e")
    }

    companion object {
        const val SITE = "https://www.readlightnovel.org/"
    }
}

private val centerReg = Regex("<center(.*?)</center>", RegexOption.DOT_MATCHES_ALL)
private val scriptReg = Regex("<script(.*?)</script>", RegexOption.DOT_MATCHES_ALL)
private val contentReg =
    Regex("<div class=\"tab-content\">(.*?)<ul class=\"chapter-chs\">[ \n]*</ul>", RegexOption.DOT_MATCHES_ALL)
private val titleReg = Regex("<title>(.*?)</title>")
private val linkReg = Regex("<a (.*?)</a>")

private fun removeUnnecessaryCode(document: String): String =
    document.substringAfter("<div class=\"desc\" data-size=\"16\" style=\"word-wrap: break-word;font-size:16px !important\">")
        .substringBefore("<div style=\"display:none\" id=\"growfoodsmart\">")
        .replace(centerReg, "")
        .replace(scriptReg, "")

private fun findTitle(document: String): String = titleReg.find(document)?.value ?: ""

private fun findChapterFromTitle(title: String) = title.substringAfterLast("Chapter").substringBefore('<').trim()

private fun is404NotFound(title: String): Boolean = title == "<title>404 Not Found</title>"

private suspend fun getPage(link: String): Result<String, Exception> {
    val (_, response, result) = link.httpGet().awaitStringResponse()
    result.fold({
        if (!response.isSuccessful) {
            return Result.error(RuntimeException("Bad response: ${response.statusCode} on $link"))
        }

        val title = findTitle(it)
        if (is404NotFound(title)) {
            return Result.error(RuntimeException("$link not found"))
        }

        return Result.of(it)
    }, {
        return Result.error(it)
    })
}
