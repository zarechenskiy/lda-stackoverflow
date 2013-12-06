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

//    val truthData = getUsersTags()
    val estimatedTags = calculateEstimatedTags(file, THRESHOLD)
    printTags(estimatedTags)
//    runWithDifferentThresholds(file, truthData)
}

fun runWithDifferentThresholds(file: File, truthData: DocumentsToTags) {
    val thresholdRange = jet.DoubleRange(0.0001, 0.96)
    val step = 0.0001

    var threshold = thresholdRange.start
    val spAndSe = ArrayList<Pair<Double, Double>>()
    while (threshold < thresholdRange.end) {
        println("Threshold: $threshold")

        val estimatedData = calculateEstimatedTags(file, threshold)

        val rocModel = rocAnalyze(estimatedData, truthData, "android", AnalyzerMatching.PARTIALLY)

        spAndSe.add(Pair(1.0 - rocModel.specificity, rocModel.sensitivity))
        println("${rocModel.falsePositiveRate} ${rocModel.sensitivity}")
        println()

        threshold += step
    }

    saveToFile(spAndSe)
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

fun printTags(documentToTags: DocumentsToTags) {
    for ((numDocument, tags) in documentToTags.data) {
        for (tag in tags) {
            print("$tag ")
        }
        println()
    }
}

