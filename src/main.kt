import kotlin.math.abs

fun main() {

    fun part1(input: List<String>): Int {
        val pairs = input.map{ it.split(Regex("\\s+")).let{ Pair(it[0].toInt(), it[1].toInt()) } }

        val left = pairs.map{ it.first }.sortedBy{ it }
        val right = pairs.map{ it.second }.sortedBy{ it }

        val diffs = (left zip right).map{ abs(it.second - it.first) }
        val distance = diffs.reduce{ acc, n -> acc + n}
        return distance
    }

    fun part2(input: List<String>): Int {
        val pairs = input.map{ it.split(Regex("\\s+")).let{ Pair(it[0].toInt(), it[1].toInt()) } }

        val left = pairs.map{ it.first }.sortedBy{ it }
        val right = pairs.map{ it.second }.sortedBy{ it }

        val occurrenceInRight = right.groupBy({ it }, { 1 }).mapValues{ it.value.sum()}
        val similarity = left.map{ it * (occurrenceInRight[it] ?: 0) }.sum()

        return similarity
    }

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
