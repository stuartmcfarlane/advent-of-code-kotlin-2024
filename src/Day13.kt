import java.util.Collections
import kotlin.math.min

fun main() {

    data class Vec2(val x: Long, val y: Long)

    fun Vec2.gt(v: Vec2): Boolean {
        return x > v.x || y > v.y
    }
    fun Vec2.eq(v: Vec2): Boolean {
        return x == v.x && y == v.y
    }
    fun Vec2.add(v: Vec2): Vec2 {
        return Vec2(x + v.x, y + v.y)
    }
    fun Vec2.mul(n: Long): Vec2 {
        return Vec2(x * n, y * n)
    }

    data class ClawMachine(val a: Vec2, val b: Vec2, val prize: Vec2)

    fun parse(lines: List<String>): ClawMachine {
        val matchA = Regex("""Button A: X\+(?<x>\d+), Y\+(?<y>\d+)""").find(lines[0])!!
        val a = Vec2(
            matchA.groups["x"]?.value?.toLong()!!,
            matchA.groups["y"]?.value?.toLong()!!
        )
        val matchB = Regex("""Button B: X\+(?<x>\d+), Y\+(?<y>\d+)""").find(lines[1])!!
        val b = Vec2(
            matchB.groups["x"]?.value?.toLong()!!,
            matchB.groups["y"]?.value?.toLong()!!
        )
        val matchPrize = Regex("""Prize: X=(?<x>\d+), Y=(?<y>\d+)""").find(lines[2])!!
        val prize = Vec2(
            matchPrize.groups["x"]?.value?.toLong()!!,
            matchPrize.groups["y"]?.value?.toLong()!!
        )

        return ClawMachine(a, b, prize)
    }
    fun solveClawMachine( clawMachine: ClawMachine): Long {
        val (a, b, prize) = clawMachine
        val maxA = min(prize.x / a.x, prize.y / a.y)
        val maxB = min(prize.x / b.x, prize.y / b.y)
        for (nA in maxA downTo 0L) {
            for (nB in 0L..maxB) {
                val p = a.mul(nA).add(b.mul(nB))
                if (p.gt(prize)) break
                if (p.eq(prize)) {
                    // println("$nA $nB")
                    return 3L * nA + nB
                }
            }
        }
        return 0
    }
    fun solveClawMachine2( clawMachine: ClawMachine): Long {
        val (a, b, prize) = clawMachine
        return solveClawMachine(
            ClawMachine(
                a,
                b,
                Vec2(
                    prize.x + 10000000000000L,
                    prize.y + 10000000000000L
                )
            )
        )
    }
    fun part1(input: List<String>): Long {
        return input.filter{ it.length > 0 }.chunked(3).map{ solveClawMachine( parse(it)) }.sum()
    }
    fun part2(input: List<String>): Long {
        return 0
        // need something clever here
        // return input.filter{ it.length > 0 }.chunked(3).map{ solveClawMachine2( parse(it)) }.sum()
    }

    // println(part2(listOf(
    //     "Button A: X+94, Y+34",
    //     "Button B: X+22, Y+67",
    //     "Prize: X=8400, Y=5400",
    //     "",
    //     "Button A: X+26, Y+66",
    //     "Button B: X+67, Y+21",
    //     "Prize: X=12748, Y=12176",
    //     "",
    //     "Button A: X+17, Y+86",
    //     "Button B: X+84, Y+37",
    //     "Prize: X=7870, Y=6450",
    //     "",
    //     "Button A: X+69, Y+23",
    //     "Button B: X+27, Y+71",
    //     "Prize: X=18641, Y=10279",
    // )))
    val input = readInput("Day13")

    val result1 = part1(input)
    println("part 1: $result1")
    val result2 = part2(input)
    println("part 2: $result2")
}
