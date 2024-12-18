import java.util.Collections
import kotlin.math.min

fun main() {

    fun List<Int>.product(): Int {
        return this.fold(1) { acc, n -> acc * n }
    }
    data class Vec2(val x: Int, val y: Int)

    fun Vec2.add(v: Vec2): Vec2 {
        return Vec2(x + v.x, y + v.y)
    }
    fun Vec2.mul(n: Int): Vec2 {
        return Vec2(x * n, y * n)
    }

    fun parse(line: String): Pair<Vec2, Vec2> {
        val pv = line.split(" ")
        val p = pv[0].substringAfter("=").split(",")
        val v = pv[1].substringAfter("=").split(",")
        return Pair(
            Vec2(p[0].toInt(), p[1].toInt()),
            Vec2(v[0].toInt(), v[1].toInt()),
        )
    }
    fun animate( pv: Pair<Vec2, Vec2>, iterations: Int): Vec2 {
        val (p, v) = pv
        return p.add(v.mul(iterations))
    }
    fun restrict( p: Vec2, width: Int, height: Int): Vec2 {
        val (x, y) = p
        val mx = x % width
        val my = y % height
        val xx = if(mx >= 0) mx else width + mx 
        val yy = if(my >= 0) my else height + my 
        return Vec2(xx, yy)
    }
    fun inQuadrant(p: Vec2, width: Int, height: Int): Int {
        val x = p.x
        val y = p.y
        if (x < width/2) {
            if (y < height/2) return 0
            if (height/2 < y) return 1
        }
        if (width/2 < x) {
            if (y < height/2) return 2
            if (height/2 < y) return 3
        }
        return -1
    }
    fun printRobots(robots: List<Vec2>, width: Int, height: Int) {
        (0..height-1).forEach{ y -> println((0..width-1).map{ x -> "${robots.filter{ x == it.x && y == it.y}.size}"}.map{ if (it == "0") "." else it}.joinToString("")) }
    }
    fun modelRobots(robots: List<Pair<Vec2, Vec2>>, width: Int, height: Int, iterations: Int): List<Vec2> {
        return robots.map{ animate(it, iterations) }.map{ restrict(it, width, height) }
    }
    fun part1(input: List<String>, width: Int = 101, height: Int = 103, iterations: Int = 100): Int {
        val initialRobots = input.map{ parse(it) }
        val modeledRobots = modelRobots(initialRobots, width, height, iterations)
        val quadrantCounts = (0..3).map{ quadrant -> modeledRobots.filter{ quadrant == inQuadrant(it, width, height) }.size }
        val safetyFactor = quadrantCounts.product()

        return safetyFactor
    }
    fun part2(input: List<String>, width: Int = 101, height: Int = 103): Int {
        val initialRobots = input.map{ parse(it) }
        var iterations = 0
        do {
            println(++iterations)
            printRobots(modelRobots(initialRobots, width, height, iterations), width, height)
        } while (true)
        return 0
    }

    // println(part1(listOf(
    //     "p=2,4 v=2,-3",
    // ), 11, 7, 0))
    // println(part1(listOf(
    //     "p=2,4 v=2,-3",
    // ), 11, 7, 1))
    // println(part1(listOf(
    //     "p=2,4 v=2,-3",
    // ), 11, 7, 2))
    // println(part1(listOf(
    //     "p=2,4 v=2,-3",
    // ), 11, 7, 3))
    // println(part1(listOf(
    //     "p=2,4 v=2,-3",
    // ), 11, 7, 4))
    // println(part1(listOf(
    //     "p=2,4 v=2,-3",
    // ), 11, 7, 5))
    // println(part1(listOf(
    //     "p=0,4 v=3,-3",
    //     "p=6,3 v=-1,-3",
    //     "p=10,3 v=-1,2",
    //     "p=2,0 v=2,-1",
    //     "p=0,0 v=1,3",
    //     "p=3,0 v=-2,-2",
    //     "p=7,6 v=-1,-3",
    //     "p=3,0 v=-1,-2",
    //     "p=9,3 v=2,3",
    //     "p=7,3 v=-1,2",
    //     "p=2,4 v=2,-3",
    //     "p=9,5 v=-3,-3",
    // ), 11, 7, 100))

    val input = readInput("Day14")

    val result1 = part1(input)
    println("part 1: $result1")
    // val result2 = part2(input)
    // println("part 2: $result2")
}
