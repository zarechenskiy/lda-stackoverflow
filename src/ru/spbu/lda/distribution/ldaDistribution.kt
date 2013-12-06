package ru.spbu.lda.distribution

import java.util.ArrayList
import java.io.File
import ru.spbu.lda.selectColumnsFromDB
import java.util.HashMap
import cc.mallet.topics.ParallelTopicModel
import java.io.FileWriter
import java.io.BufferedWriter
import ru.spbu.lda.LDA

class TopicSelector(val model: ParallelTopicModel) {
    private val topicSortedWords = model.getSortedWords()
    private val dataAlphabet = model.getAlphabet()

    fun getTopTopicsFor(topic: Int, countTopics: Int): List<String> {
        val idSorterIterator = topicSortedWords.get(topic).iterator()

        var numTopic = 0
        val topTopics = ArrayList<String>()
        while (idSorterIterator.hasNext() && numTopic < countTopics) {
            val idCountPair = idSorterIterator.next()
            topTopics.add(dataAlphabet!!.lookupObject(idCountPair.getID()) as String)

            numTopic++
        }

        return topTopics
    }
}

fun main(args: Array<String>) {
    val texts = selectAllTexts()
    val stopWords = File("stoplists/en.txt")

    val lda = LDA(stopWords, 2, 200)
    val instances = lda.createInstanceList(texts)
    val model = lda.createTopicModel(instances, 700)

    saveTopicDistributions(model, "topic_distributions.txt")
}

fun saveTopicDistributions(model: ParallelTopicModel, fileName: String) {
    val countDocuments = model.getData().size

    val fileWriter = FileWriter(fileName)
    val bufferedWriter = BufferedWriter(fileWriter)

    bufferedWriter.write("$countDocuments ${model.getNumTopics()}\n")
    for (document in 0..countDocuments - 1) {
        bufferedWriter.write("$document\n")
        val distribution = model.getTopicProbabilities(document)

        for (topic in 0..model.getNumTopics() - 1) {
            bufferedWriter.write("${distribution[topic]}\n")
        }
    }
}

fun selectAllTexts(): List<String> {
    val resultSet = selectColumnsFromDB("question", "description")

    val texts = ArrayList<String>()
    while (resultSet.next()) {
        val fullQuestion = resultSet.getString("question") + "\n" + resultSet.getString("description")
        texts.add(fullQuestion)
    }

    return texts
}

fun selectQuestionIndexes(): Map<Int, Int> {
    val idQuestionColumnName = "id_question"
    val resultSet = selectColumnsFromDB(idQuestionColumnName)

    var ind = 0
    val indexes = HashMap<Int, Int>()
    while (resultSet.next()) {
        indexes.put(ind, resultSet.getInt(idQuestionColumnName))
        ind++
    }

    return indexes
}

