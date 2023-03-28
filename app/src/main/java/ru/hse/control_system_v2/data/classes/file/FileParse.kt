package ru.hse.control_system_v2.data.classes.file

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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
            val tagList = ArrayList<XmlTag>()
            try {
                // Запускаем корутину с диспатчером IO для работы с файлом
                runBlocking(Dispatchers.IO) {
                    val factory = XmlPullParserFactory.newInstance()
                    factory.isNamespaceAware = true
                    val parser = factory.newPullParser()
                    parser.setInput(xml.reader())

                    var eventType = parser.eventType
                    // Создаем переменную для хранения текущего тега
                    var currentTag: XmlTag? = null
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        when (eventType) {
                            XmlPullParser.START_TAG -> {
                                currentTag = null
                                // Обработка начального тега
                                val name = parser.name
                                // Если имя тега не равно xml (заголовок документа)
                                if (name != "xml") {
                                    // Создаем новый тег с именем и пустым значением
                                    currentTag = XmlTag(name, "")
                                }
                            }

                            XmlPullParser.TEXT -> {
                                /// Получаем текст тега
                                val text = parser.text
                                // Если текущий тег не пустой и текст не пустой или пробельный
                                if (currentTag != null && text.isNotBlank()) {
                                    // Устанавливаем значение текущего тега равным тексту
                                    currentTag.value = text
                                }
                            }

                            XmlPullParser.END_TAG -> {
                                // Обработка конечного тега
                                // Если текущий тег не пустой
                                if (currentTag != null) {
                                    // Добавляем текущий тег в список тегов
                                    tagList.add(currentTag)
                                    // Обнуляем текущий тег
                                    currentTag = null
                                }
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
                    tagList = tagList
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