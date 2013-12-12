package ru.spbu.lda

import java.io.File
import java.io.BufferedReader
import java.io.FileReader
import java.util.HashMap
import java.util.ArrayList
import java.util.HashSet
import java.util.Scanner

open class DocumentsWithClusters() {
    val data = HashMap<Int, MutableSet<Int>>()

    fun put(key: Int, value: Int) {
        if (!data.containsKey(key)) data.put(key, HashSet<Int>())

        data.get(key).add(value)
    }
}

class DocumentsToClusters: DocumentsWithClusters()
class ClustersToDocuments: DocumentsWithClusters()

class ClustersToTags {
    val data = HashMap<Int, String>()

    fun put(key: Int, value: String) {
        data.put(key, value)
    }

    fun get(key: Int) = data.get(key)
}

fun matchClustersToTags(clustersToDocuments: ClustersToDocuments): ClustersToTags {
    val tagsToDocuments = getUsersTagsToDocuments()

    val allTags = getDistinctTags()

    val clustersToTags = ClustersToTags()

    for ((cluster, clusterToDocuments) in clustersToDocuments.data) {
        var maxProbability = 0.0
        var applicableTag: String? = null

        for (tag in allTags) {
            val tagToDocuments = tagsToDocuments.get(tag)
            val probability = calculateProbability(clusterToDocuments, tagToDocuments)

            if (maxProbability < probability) {
                maxProbability = probability
                applicableTag = tag
            }
        }

        if (applicableTag != null) clustersToTags.put(cluster, applicableTag!!) // bug in kotlin, should be smart cast
    }

    return clustersToTags
}

fun matchDocumentsToTags(clustersToTags: ClustersToTags, documentsToClusters: DocumentsToClusters): DocumentsToTags {
    val documentsToTags = DocumentsToTags()
    for ((numDocument, clusters) in documentsToClusters.data) {
        for (cluster in clusters) {
            documentsToTags.put(numDocument, clustersToTags.get(cluster))
        }
    }

    return documentsToTags
}

fun getDocumentsWithClusters(distributions: Map<Int, List<Double>>,
                             threshold: Double): Pair<DocumentsToClusters, ClustersToDocuments> {

    val documentsToClusters = DocumentsToClusters()
    val clustersToDocuments = ClustersToDocuments()
    for ((document, documentDistributions) in distributions) {
        if (document % 1000 == 0) print("${document / 1000} ")

        var cluster = 0
        for (distribution in documentDistributions) {
            if (threshold < distribution) {
                documentsToClusters.put(document, cluster)
                clustersToDocuments.put(cluster, document)
                cluster++
            }
        }
    }

    println()

    return Pair(documentsToClusters, clustersToDocuments)
}

fun getDocumentsWithClusters(file: File, threshold: Double): Pair<DocumentsToClusters, ClustersToDocuments> {
    val reader = BufferedReader(FileReader(file))

    val (countDocuments, countClusters) = getCountDocumentsAndClusters(file)
    reader.readLine()

    val documentsToClusters = DocumentsToClusters()
    val clustersToDocuments = ClustersToDocuments()
    for (document in 0..countDocuments - 1) {
        if (document % 1000 == 0) print("${document / 1000} ")
        reader.readLine()

        for (cluster in 0..countClusters - 1) {
            val distribution = reader.readLine()!!.toDouble()
            if (threshold < distribution) {
                documentsToClusters.put(document, cluster)
                clustersToDocuments.put(cluster, document)
            }
        }
    }

    println()
    reader.close()

    return Pair(documentsToClusters, clustersToDocuments)
}

fun getCountDocumentsAndClusters(file: File): Pair<Int, Int> {
    val scanner = Scanner(file)
    val numDocuments = scanner.nextInt()
    val numClusters = scanner.nextInt()

    scanner.close()

    return Pair(numDocuments, numClusters)
}