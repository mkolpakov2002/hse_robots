package ru.hse.control_system_v2.ui.protocol

class XmlTagGroup {
    // Список для хранения тегов
    private val tags = mutableListOf<XmlTag>()

    // Метод для добавления тега в группу
    fun addTag(tag: XmlTag) {
        tags.add(tag)
    }

    // Метод для удаления тега из группы по имени
    fun removeTag(name: String) {
        tags.removeAll { it.name == name }
    }

    // Метод для поиска тега в группе по имени и возвращения его атрибутов и содержимого
    fun findTag(name: String): XmlTag? {
        return tags.find { it.name == name }
    }
}