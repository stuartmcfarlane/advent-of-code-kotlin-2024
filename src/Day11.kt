import java.util.Collections
import java.math.BigInteger

fun main() {

    fun hasEvenDigits(n: BigInteger): Boolean {
        val result = "$n".length % 2 == 0
        return result
    }
    fun splitStone(n: BigInteger): List<BigInteger> {
        val s = "$n"
        return s.chunked(s.length / 2).map{ it.toBigInteger() }
    }
    fun part1(input: List<String>, length: Int = 25): Int {
        var stones = input.first().split(" ").map{ it.toBigInteger() }
        var blinksRemaining = length
        while (blinksRemaining > 0) {
            stones = stones.flatMap{
                when {
                    it == 0.toBigInteger() -> listOf(1.toBigInteger())
                    hasEvenDigits(it) -> splitStone(it)
                    else -> listOf(it * 2024.toBigInteger())
                }
            }
            blinksRemaining--
        }
        return stones.size
    }

    fun part2(input: List<String>, targetDepth: Int = 75): Long {

        var cache = mutableMapOf<Pair<BigInteger, Int>, Long>()

        fun splitStone(stone: BigInteger): List<BigInteger> {
            val stoneStr = "$stone"
            val digits = stoneStr.length
            if (stone == 0.toBigInteger()) return listOf(1.toBigInteger())
            if (digits % 2 == 1) return listOf(stone * 2024.toBigInteger())
            return stoneStr.chunked(digits / 2).map{ it.toBigInteger() }
        }
        fun countStones(stone: BigInteger, depth: Int): Long {
            val key = Pair(stone, depth)
            println ("countStones $stone $depth")
            val cached = cache.getOrDefault(key, null)
            if (null != cached) println ("$stone $depth cached $cached")
            return cache.getOrPut(key) {
                val result = (
                    if (depth == targetDepth)
                        1L
                    else
                        splitStone(stone).map{ countStones(it, depth + 1) }.sum()
                )
                println("put $stone $depth $result")
                return result
            }
        }
       
        var stones = input.first().split(" ").map{ it.toBigInteger() }
        return stones.map{ countStones(it, 0) }.sum()
    }
    val input = readInput("Day11")
    
    val result1 = part1(input)
    println("part 1: $result1")
    val result2 = part2(input, 25)
    println("part 2: $result2")
}
