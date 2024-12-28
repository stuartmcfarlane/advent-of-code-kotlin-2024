import java.util.Collections
import kotlin.math.pow

fun main() {

    fun debug(d: Any) {
        // println(d)
    }

    fun parse(lines: List<String>): Pair<List<String>, List<String>> {
        val patterns = lines.filter{ it.contains(',') }.flatMap{ it.split(",") }.map{ it.trim() }
        val designs = lines.filter{ it.length > 0 && !it.contains(',') }
        return patterns to designs
    }
    fun part1(patternsDesigns: Pair<List<String>, List<String>>): Int {
        val (patterns, designs) = patternsDesigns
        val regex = "(${patterns.joinToString("|")})+".toRegex()
        
        return designs.filter{ it.matches(regex)}.size
    }

    require(6 == 
    part1(parse(listOf(
        "r, wr, b, g, bwu, rb, gb, br",
        "",
        "brwrr",
        "bggr",
        "gbbr",
        "rrbgbr",
        "ubwu",
        "bwurrg",
        "brgr",
        "bbrgwb",
    )))
    )

    val input = readInput("Day19")

    val result1 = part1(parse(input))
    println("part 1: $result1")
}

