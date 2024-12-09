import java.util.Collections

fun main() {

    data class Cell(val x: Int, val y: Int)

    fun makeAntennaPairs(input: List<String>): Map<Char, Set<Pair<Cell, Cell>>> {
        val cells = input.flatMapIndexed{ y, row -> row.mapIndexed{ x, data -> Pair(data, Cell(x, y)) } }.filter{ (data, _) -> data != '.' }
        val antennas = cells.map{ (antenna, _) -> antenna }.toSet()
        val antennaCells: Map<Char, Set<Cell>> = antennas.map{ antenna -> Pair(antenna, cells.filter{ (a, _) -> antenna == a }.map{ (_, cell) -> cell }.toSet()) }.toMap()
        val antennaPairs: Map<Char, Set<Pair<Cell, Cell>>>  = antennaCells.map{
            (antenna: Char, cells: Set<Cell>) -> Pair(
                antenna,
                cells.flatMap{
                    a -> cells.map{ b -> Pair(a, b) }
                }
                .filter{  (left, right) -> left != right }
                .toSet()
            )
        }
        .toMap()
        return antennaPairs
    }
    fun part1(input: List<String>): Int {
        val antennaPairs = makeAntennaPairs(input)
        val width = input[0].length
        val height = input.size
        fun makeAntiNodes(c1: Cell, c2: Cell): List<Cell> {
            
            val (p1, p2) = if (c1.x <= c2.x) Pair(c1, c2) else Pair(c2, c1)
            val (x1, y1) = p1
            val (x2, y2) = p2
            val dx = x2 - x1
            val dy = y2 - y1
            return listOf(Cell(x1 - dx, y1 - dy), Cell(x2 + dx, y2 + dy))
        }
        fun isInsideArea(c: Cell): Boolean {
            return 0 <= c.x
            	&& c.x < width
            	&& 0 <= c.y
            	&& c.y < height
        }
        val antiNodes = antennaPairs.flatMap{
            (_, cellPairs) -> cellPairs.flatMap{
         		(c1, c2) -> makeAntiNodes(c1, c2)
            }
        }
        .filter{ isInsideArea(it) }
        .toSet()

        return antiNodes.size
    }

    fun printCellsOn(input: List<String>, antiNodes: Set<Cell>) {
        input.mapIndexed{
            y, line -> line.mapIndexed{
                x, c -> if (antiNodes.contains(Cell(x, y))) '#' else c
            }.joinToString("")
        }.forEach{ println(it) }
    }

    fun part2(input: List<String>): Int {
        val antennaPairs = makeAntennaPairs(input)
        val width = input[0].length
        val height = input.size
        fun makeHarmonicAntiNodes(c1: Cell, c2: Cell): List<Cell> {
            // println(">makeHarmonicAntiNodes")
            val (p1, p2) = if (c1.x <= c2.x) Pair(c1, c2) else Pair(c2, c1)
            val (x1, y1) = p1
            val (x2, y2) = p2
            val dx = x2 - x1
            val dy = y2 - y1
            val left = (0..x1/dx).map{ Cell(x1 - it * dx, y1 - it * dy) }
            val right = (0..(width - x2)/dx).map{ Cell(x2 + it * dx, y2 + it * dy) }
            // println(c1)
            // println(c2)
            // println(p1)
            // println(p2)
            // println(dx)
            // println(dy)
            // println(1..x1/dx)
            // println(1..(width - x2)/dx)
            // println(left)
            // println(right)
            // println("<makeHarmonicAntiNodes")
            return left + right
        }
        fun isInsideArea(c: Cell): Boolean {
            return 0 <= c.x
            	&& c.x < width
            	&& 0 <= c.y
            	&& c.y < height
        }
        val antiNodes = antennaPairs.flatMap{
            (_, cellPairs) -> cellPairs.flatMap{
         		(c1, c2) -> makeHarmonicAntiNodes(c1, c2)
            }
        }
        .filter{ isInsideArea(it) }
        .toSet()
        // println(antiNodes)
        // printCellsOn(input, antiNodes)
        return antiNodes.size
    }


    val input = readInput("Day08")
    val result1 = part1(input)
    val result2 = part2(input)

    print("part 1: ")
    println(result1)
    print("part 2: ")
    println(result2)
}
