package ru.spbu.lda

import java.io.File
import java.util.ArrayList
import java.io.FileWriter
import java.io.BufferedWriter
import java.io.BufferedReader
import java.io.FileReader
import java.util.HashMap

fun main(args: Array<String>) {
    val file = File("distribution.txt")
    val THRESHOLD = 0.22

    val truthTags = getUsersTags()
    val estimatedTags = calculateEstimatedTags(file, THRESHOLD)
    printTags(estimatedTags, truthTags, "estimated_truth_tags.txt")
//    printTags(estimatedTags)
}

fun saveToFile(data: List<Pair<Double, Double>>) {
    val fileWriter = FileWriter("spse.txt")
    val writer = BufferedWriter(fileWriter)

    for (pair in data) {
        writer.write("${pair.first} ${pair.second}\n")
    }

    writer.close()
}

fun calculateEstimatedTags(file: File, threshold: Double): DocumentsToTags {
    val (documentsToClusters, clustersToDocuments) = getDocumentsWithClusters(file, threshold)
    val clustersToTags = matchClustersToTags(clustersToDocuments)

    return matchDocumentsToTags(clustersToTags, documentsToClusters)
}

fun printTags(estimatedTags: DocumentsToTags, truthTags: DocumentsToTags, file: String) {
    val fileWriter = FileWriter(file)
    val writer = BufferedWriter(fileWriter)

    for ((numDocument, tags) in estimatedTags.data) {
        val tagBuilder = StringBuilder()
        for (tag in tags) {
            tagBuilder.append("$tag ")
        }
        tagBuilder.append("| ")
        for (estimatedTag in truthTags.get(numDocument)) {
            tagBuilder.append("$estimatedTag ")
        }
        writer.write(tagBuilder.toString() + "\n")
    }

    writer.close()
}

fun printTags(documentToTags: DocumentsToTags) {
    for ((numDocument, tags) in documentToTags.data) {
        for (tag in tags) {
            print("$tag ")
        }
        println()
    }
}

