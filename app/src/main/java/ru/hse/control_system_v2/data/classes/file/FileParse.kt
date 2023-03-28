package ru.hse.control_system_v2.data.classes.file

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import org.json.JSONObject
import ru.hse.control_system_v2.App
import ru.hse.control_system_v2.data.classes.protocol.ProtocolModel
import ru.hse.control_system_v2.ui.protocol.XmlTag

class FileParse {
    //val url = "path-to-file"
    //val xml = getXmlFromUrl(url)
    //parseXml(xml)
    companion object {
        suspend fun getXmlFromUrl(url: Uri): String {
            return withContext(Dispatchers.IO) {
                val inputStream = App.instance.contentResolver.openInputStream(url)
                val xml = inputStream?.bufferedReader().use { it?.readText() }
                inputStream?.close()
                xml ?: ""
            }
        }

        suspend fun parseXml(xml: String) : ProtocolModel{

            try{
            val tagList = ArrayList<XmlTag>()
            withContext(Dispatchers.IO) {
                val factory = XmlPullParserFactory.newInstance()
                factory.isNamespaceAware = true
                val parser = factory.newPullParser()
                parser.setInput(xml.reader())

                var eventType = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    var xmlTag = XmlTag()
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            // Обработка начального тега
                            xmlTag.name = parser.name
                        }

                        XmlPullParser.TEXT -> {
                            // Обработка текста внутри тега
                            xmlTag.value = parser.text
                            tagList.add(xmlTag)
                        }

                        XmlPullParser.END_TAG -> {
                            // Обработка конечного тега
                        }
                    }
                    eventType = parser.next()
                }
            }
            return ProtocolModel(
                id = 0,
                name = "",
                tagList = tagList
            )
            } catch (e : Exception) {
                e.printStackTrace()
                return ProtocolModel(
                    id = 0,
                    name = "",
                    tagList = ArrayList()
                )
            }
        }

        suspend fun getJsonFromUrl(url: String): JSONObject {
            return withContext(Dispatchers.IO) {
                val inputStream = URL(url).openStream()
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()
                JSONObject(jsonString)
            }
        }

        suspend fun parseJson(json: JSONObject) {
            return withContext(Dispatchers.IO) {
                // Обработка json объекта
            }
        }
    }
}