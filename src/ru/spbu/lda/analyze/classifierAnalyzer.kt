package ru.spbu.lda


fun calculatePreciseAndRecall(estimatedData: DocumentsToTags, truthData: DocumentsToTags): Pair<Double, Double> {
    var avgPrecise = 0.0
    var avgRecall = 0.0

    for ((numDocument, estimatedTags) in estimatedData.data) {
        val truthTags = truthData.get(numDocument)

        avgPrecise += calculatePrecise(estimatedTags, truthTags)
        avgRecall += calculateRecall(estimatedTags, truthTags)
    }

    val countDocuments = estimatedData.data.size.toDouble()

    return Pair(avgPrecise / countDocuments, avgRecall / countDocuments)
}

private fun calculatePrecise(estimatedTags: Set<String>, truthTags: Set<String>): Double {
    var matchedTags = 0
    for (estimatedTag in estimatedTags) {
        if (truthTags.contains(estimatedTag)) matchedTags++
    }

    return matchedTags.toDouble() / estimatedTags.size.toDouble()
}

private fun calculateRecall(estimatedTags: Set<String> truthTags: Set<String>) = calculatePrecise(truthTags, estimatedTags)

fun calculateProbability(docs1: Set<Int>, docs2: Set<Int>): Double {
    val intersected = countInIntersection(docs1, docs2)
    val united = docs1.size + docs2.size

    return intersected.toDouble() / united.toDouble()
}

fun countInIntersection(docs1: Set<Int>, docs2: Set<Int>): Int {
    var count = 0
    for (doc in docs1) {
        if (docs2.contains(doc)) count++
    }

    return count
}