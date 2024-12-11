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

    fun part2(input: List<String>, length: Int = 75): Int {
        fun blink(blinksRemaining: Int, stone: BigInteger): Int {
            if (blinksRemaining == 0) {
                return 1
            }
            if (stone == 0.toBigInteger()) return blink(blinksRemaining - 1, 1.toBigInteger())
            if (hasEvenDigits(stone)) return splitStone(stone).map{ blink(blinksRemaining - 1, it)}.sum()
            return blink(blinksRemaining - 1, stone * 2024.toBigInteger())
        }
        var stones = input.first().split(" ").map{ it.toBigInteger() }
        val result = stones.map{ blink(length, it)}.sum()
        return result
    }
    val input = readInput("Day11_test")
    // for (i in 1..75) {
    //     println("$i ${part2(input, i)}")
    // }
    
    val result1 = part1(input)
    println("part 1: $result1")
    val result2 = part2(input)
    println("part 2: $result2")
}
