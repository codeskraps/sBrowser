package com.codeskraps.sbrowser.feature.webview.media

import android.text.TextUtils
import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.regex.Matcher
import java.util.regex.Pattern


class HandleVideo {
    companion object {
        private val TAG = HandleVideo::class.java.simpleName
    }

    operator fun invoke(url: String, result: (String) -> Unit) {
        try {
            val doc: Document = Jsoup.connect(url).get()
            var metalinks: Elements = doc.select("div[id=player]")

            if (!metalinks.isEmpty()) {
                val elements: Elements = metalinks.select("script")

                if (!elements.isEmpty()) {
                    val iterator: Iterator<Element> = elements.iterator()

                    while (iterator.hasNext()) {
                        var html: String = iterator.next().html()
                        Log.w(TAG, html)

                        if (html.contains("HTML5Player")) {
                            html = html.substring(html.indexOf("HTML5Player"))
                            val m: Matcher = Pattern.compile("\\((.*?)\\)").matcher(html)

                            while (m.find()) {
                                Log.v(TAG, "m:" + m.group(1))
                                m.group(1)?.let { groupOne ->
                                    val attr =
                                        groupOne.split(",".toRegex())
                                            .dropLastWhile { it.isEmpty() }
                                            .toTypedArray()
                                    Log.v(TAG, "attr:" + attr[3])
                                    val index1 = attr[3].indexOf("\'") + 1
                                    val index2 = attr[3].indexOf("\'", index1)
                                    Log.v(TAG, "video:${attr[3]}")
                                    result(attr[3].substring(index1, index2))
                                }
                            }
                        }
                    }
                }
            }

            metalinks = doc.select("a[id=play]")

            if (!metalinks.isEmpty()) {
                Log.v(TAG, "video:${metalinks.firstOrNull()}")
                metalinks.first()?.let {
                    result(it.attr("href"))
                }
            }

            Log.e(TAG, "script")
            metalinks = doc.select("script")

            if (!metalinks.isEmpty()) {
                val iterator: Iterator<Element> = metalinks.iterator()

                while (iterator.hasNext()) {
                    val html: String = iterator.next().html()
                    Log.w(TAG, "new:$html")

                    if (!TextUtils.isEmpty(html) && html.contains("setVideoUrlHigh")) {
                        val lines =
                            html.split("\\r\\n|\\n|\\r".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()

                        for (line in lines) {

                            if (line.contains("setVideoUrlHigh")) {
                                Log.w(TAG, "setVideoUrlHigh:$line")
                                result(line.substring(line.indexOf('(') + 2, line.indexOf(')') - 1))
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Handled - HandleVideo:$e", e)
        }
    }
}