import java.util.Collections
import kotlin.math.abs

fun main() {

    data class Cell(val x: Int, val y: Int)

    fun Cell.add(c: Cell): Cell {
        return Cell(x + c.x, y + c.y)
    }

    class Grid <T> {
        val cells: List<List<T>>
        val height: Int
        val width: Int

        constructor(input: List<String>, cellInitializer: (cellInput: Char) -> T) {
            cells = input.map{ it.map( cellInitializer) }
            height = input.size
            width = input.map{ it.length }.max()
        }
        fun get(x: Int, y: Int): T {
            return cells.get(y).get(x)
        }
        fun getOr(x: Int, y: Int, or: T): T {
            val cell = cells.getOrNull(y)?.getOrNull(x)
            if (cell == null) return or
            return cell
        }
        fun getOr(cell: Cell, or: T): T {
            return getOr(cell.x, cell.y, or)
        }
        fun get(cell: Cell): T {
            return get(cell.x, cell.y)
        }
        fun find(test: (t: T) -> Boolean): List<Cell> {
            var found = mutableListOf<Cell>()
            for (y in 0..height-1) {
                for (x in 0..width-1) {
                    if (test(get(x, y))) found.add(Cell(x, y))
                }
            }
            return found.toList()
        }
        fun println() {
            cells.forEach{ println( it.map{ it }.joinToString("") ) }
        }
        fun println(cellPrinter: (cell: T) -> Char) {
            cells.forEach{ println( it.map{ cellPrinter(it) }.joinToString("") ) }
        }
    }

    fun Grid<Int>.walk(path: List<Cell>): List<List<Cell>> {
        val cell = path.last()
        val altitude = get(cell)
        if (altitude == 9) {
            return listOf(path)
        }
        val N = Cell(0, -1)
        val E = Cell(1, 0)
        val S = Cell(0, 1)
        val W = Cell(-1, 0)
        val usedCells = path.toSet()
        val extensions = listOf(N, E, W, S)
            .map{ cell.add(it) }
            .filter{
                getOr(it, Int.MAX_VALUE) - altitude == 1
            }
            .filter{ !usedCells.contains(it) }
        if (extensions.size == 0) {
            return listOf()
        }
        val extended = extensions.map{ path + listOf(it) }
        val walked = extended.flatMap{ walk(it) }
        return walked
    }

    fun part1(input: List<String>): Int {
        val grid = Grid<Int>(input, { "$it".toInt() })

        val trailHeads = grid.find{ it == 0 }
        val trails = trailHeads.flatMap{ grid.walk(listOf(it)) }
        val headAndTails = trailHeads
            .map{ trails.filter{ trail -> trail.first() == it }.map{ it.last() }.toSet().size }
        return headAndTails.sum()
    }

    fun part2(input: List<String>): Int {
        val grid = Grid<Int>(input, { "$it".toInt() })

        val trailHeads = grid.find{ it == 0 }
        val trails = trailHeads.flatMap{ grid.walk(listOf(it)) }
        val headAndTails = trailHeads
            .map{ trails.filter{ trail -> trail.first() == it }.map{ it.last() }.size }
        return headAndTails.sum()
    }


    val input = readInput("Day10")
    val result1 = part1(input)
    val result2 = part2(input)

    print("part 1: ")
    println(result1)
    print("part 2: ")
    println(result2)
}
