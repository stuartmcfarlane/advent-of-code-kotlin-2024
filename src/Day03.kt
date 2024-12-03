import kotlin.collections.joinToString
import kotlin.collections.mutableListOf
import kotlin.text.MatchResult

fun main() {

    fun part1(input: List<String>): Long {
        val code = input.joinToString(separator = "")
        val regex = Regex("mul\\((\\d{1,3}),(\\d{1,3})\\)")
        val matchResult = regex.findAll(code)
        var pairs: List<Pair<Long, Long>> = listOf<Pair<Long, Long>>()
        matchResult.forEach{ match -> pairs = pairs + Pair(match.groups?.get(1)?.value?.toLong()!!, match.groups?.get(2)?.value?.toLong()!!)}
        val product = pairs.map{ (a, b): Pair<Long, Long> -> a * b}.sum()
        return product
    }

    fun part2(input: List<String>): Long {
        val code = input.joinToString(separator = "")
        val regex = Regex("(?:mul\\((\\d{1,3}),(\\d{1,3})\\))|(don't|do)")
        val matchResult = regex.findAll(code)
        var pairs: List<Pair<Long, Long>> = listOf<Pair<Long, Long>>()
        var doing = true
        matchResult.forEach{ match ->
            if (match.value == "do")
                doing = true
            else if (match.value == "don't")
                doing = false
            else if (doing)
                pairs = pairs + Pair(match.groups?.get(1)?.value?.toLong()!!, match.groups?.get(2)?.value?.toLong()!!)
        }
        val product = pairs.map{ (a, b): Pair<Long, Long> -> a * b}.sum()
        return product
    }

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
