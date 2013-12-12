package ru.spbu.lda

import java.util.HashMap
import java.util.ArrayList
import java.util.HashSet
import java.sql.ResultSet
import ru.spbu.lda.data.utils.SQLUtils

val TAGS_COLUMN_NAME = "tags"

class DocumentsToTags {
    val data = HashMap<Int, MutableSet<String>>()

    fun put(numDocument: Int, tags: Set<String>) {
        data.put(numDocument, HashSet<String>(tags))
    }

    fun put(numDocument: Int, tag: String) {
        if (!data.containsKey(numDocument)) data.put(numDocument, HashSet<String>())

        data.get(numDocument).add(tag)
    }

    fun get(key: Int): Set<String> {
        return data.get(key)
    }
}

class TagsToDocuments {
    val data = HashMap<String, MutableSet<Int>>()

    fun put(key: String, value: Int) {
        if (!data.containsKey(key)) data.put(key, HashSet<Int>())

        data.get(key).add(value)
    }

    fun get(key: String): Set<Int> {
        return data.get(key)
    }
}

fun getUsersTags(): DocumentsToTags {
    val documentsToTags = DocumentsToTags()

    var numDocument = 0
    val resultSet = selectTagsFromDB()
    while (resultSet.next()) {
        documentsToTags.put(numDocument, splitTags(resultSet.getString(TAGS_COLUMN_NAME)!!))
        numDocument++
    }

    return documentsToTags
}

fun getUsersTagsToDocuments(): TagsToDocuments {
    val tagToDocuments = TagsToDocuments()

    var numDocument = 0
    val resultSet = selectTagsFromDB()
    while (resultSet.next()) {
        val tags = splitTags(resultSet.getString(TAGS_COLUMN_NAME)!!)
        for (tag in tags) {
            tagToDocuments.put(tag, numDocument)
        }

        numDocument++
    }

    return tagToDocuments
}

fun getDistinctTags(): Set<String> {
    val tagsSet = HashSet<String>()

    val resultSet = selectTagsFromDB()
    while (resultSet.next()) {
        val tags = splitTags(resultSet.getString(TAGS_COLUMN_NAME)!!)
        for (tag in tags) {
            tagsSet.add(tag)
        }
    }

    return tagsSet
}

fun splitTags(rawTags: String): Set<String> {
    val tags = rawTags.substring(1, rawTags.length - 1).split(",")

    val trimTags = HashSet<String>()
    for (tag in tags) {
        trimTags.add(tag.trim())
    }

    return trimTags
}

fun selectColumnsFromDB(vararg columnNames: String): ResultSet {
    val columns = StringBuilder()
    var first = true;
    for (columnName in columnNames) {
        if (!first) columns.append(",") else first = false

        columns.append(columnName)
    }

    val query = "SELECT ${columns.toString()} FROM lda_questions ORDER BY id_question"
    return SQLUtils.INSTANCE.getConnection().createStatement().executeQuery(query)
}

fun selectTagsFromDB(): ResultSet {
    return selectColumnsFromDB("tags")
}