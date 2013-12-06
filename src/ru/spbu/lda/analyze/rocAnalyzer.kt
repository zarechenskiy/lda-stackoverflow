package ru.spbu.lda

data class RocModel (
        val truePositive: Int,
        val trueNegative: Int,
        val falsePositive: Int,
        val falseNegative: Int) {

    val truePositiveRate = truePositive.toDouble() / (truePositive + falseNegative).toDouble()

    val falsePositiveRate = falsePositive.toDouble() / (trueNegative + falsePositive).toDouble()

    val sensitivity = truePositiveRate

    val specificity = trueNegative.toDouble() / (trueNegative + falsePositive).toDouble()
}

enum class AnalyzerMatching {
    PRECISELY
    PARTIALLY
}

fun rocAnalyze(estimatedData: DocumentsToTags,
               truthData: DocumentsToTags,
               keyword: String,
               matching: AnalyzerMatching): RocModel {
    var truePositive = 0
    var falsePositive = 0
    var trueNegative = 0
    var falseNegative = 0

    for ((numDocument, estimatedTags) in estimatedData.data) {
        val truthTags = truthData.get(numDocument)

        val containsInEstimatedTags = contains(estimatedTags, keyword, matching)
        val containsInTruthTags = contains(truthTags, keyword, matching)
        when {
            containsInEstimatedTags && containsInTruthTags -> truePositive++
            !containsInEstimatedTags && !containsInTruthTags -> trueNegative++
            containsInEstimatedTags && !containsInTruthTags -> falsePositive++
            !containsInEstimatedTags && containsInTruthTags -> falseNegative++
        }
    }

    return RocModel(truePositive, trueNegative, falsePositive, falseNegative)
}

fun contains(tags: Set<String>, keyword: String, matching: AnalyzerMatching): Boolean {
    if (matching == AnalyzerMatching.PRECISELY) return tags.contains(keyword)

    for (tag in tags) {
        if (tag.contains(keyword)) {
            return true
        }
    }

    return false
}


