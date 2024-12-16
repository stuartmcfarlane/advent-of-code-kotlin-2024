import java.util.Collections
import kotlin.math.abs

fun main() {

    data class Cell(val x: Int, val y: Int)

    fun Cell.add(c: Cell): Cell {
        return Cell(x + c.x, y + c.y)
    }
    fun <T> MutableCollection<T>.removeFirst() = first().also{ remove(it) }
    fun <T> MutableCollection<T>.findAndRemove( pred: (t: T) -> Boolean) = find{pred(it)}.also{ remove(it) }

    class Grid <T> {
        val cells: List<List<T>>
        val height: Int
        val width: Int

        constructor(input: List<String>, cellInitializer: (cellInput: Char) -> T) {
            cells = input.map{ it.map( cellInitializer) }
            height = input.size
            width = input.map{ it.length }.max()
        }
        constructor(_width: Int, _height: Int, cellInitializer: (x: Int, y: Int) -> T) {
            width = _width
            height = _height
            cells = (0..height-1).map{
                y -> (0..width-1).map{
                    x -> cellInitializer(x, y)
                }.toList()
            }.toList()
        }
        constructor(grid: Grid<T>, cell: Cell, v: T) {
            cells = grid.cells.mapIndexed{ y, row -> row.mapIndexed{ x, col -> if (x == cell.x && y == cell.y) v else grid.get(x, y)} }
            height = grid.height
            width = grid.width
        }
        fun get(x: Int, y: Int): T {
            return cells.get(y).get(x)
        }
        fun getOrNull(x: Int, y: Int): T? {
            return cells.getOrNull(y)?.getOrNull(x)
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
        fun getOrNull(cell: Cell): T? {
            return getOrNull(cell.x, cell.y)
        }
        fun forEach( visit: (cell: Cell, v: T) -> Unit) {
            for (y in 0..height-1) {
                for (x in 0..width-1) {
                    visit(Cell(x, y), get(x, y))
                }
            }
        }
        fun find(test: (t: T) -> Boolean): List<Cell> {
            var found = mutableListOf<Cell>()
            forEach{ c, v -> if (test(v)) found.add(c) }
            return found.toList()
        }
        fun println() {
            cells.forEach{ println( it.map{ it }.joinToString("") ) }
        }
        fun println(cellPrinter: (cell: T) -> Char) {
            cells.forEach{ println( it.map{ cellPrinter(it) }.joinToString("") ) }
        }
    }

    val N = Cell(0, -1)
    val E = Cell(1, 0)
    val S = Cell(0, 1)
    val W = Cell(-1, 0)
    fun isAdjacent(a: Cell, b: Cell): Boolean {
        val dx = abs(a.x - b.x)
        val dy = abs(a.y - b.y)
        return (dx == 1) xor (dy == 1)
    }
    fun Grid<Char>.getAdjacent(c: Cell): Set<Cell> {
        return setOf(N, E, S, W).map{ c.add(it) }.filter{ getOr(it, '?') == get(c) }.toSet()
    }
    fun Grid<Char>.perimeterOf(c: Cell): Int {
        return listOf(N, E, S, W).filter{ getOr(c.add(it), '?') != get(c) }.size
    }
    fun Grid<Char>.perimeterOf(region: Set<Cell>): Int {
        return region.map{ perimeterOf(it)}.sum()
    }
    fun Grid<Char>.simplifiedPerimeterOf(region: Set<Cell>): Int {
        if (region.size == 1) return 4
        if (region.size == 2) return 4
        return 0
    }

    fun makeRegions(garden: Grid<Char>): Set<Pair<Char, Set<Cell>>> {
        var regions = mutableSetOf<Pair<Char, Set<Cell>>>()
        var cellsTodo = mutableSetOf<Cell>()
        garden.forEach{ c, _ -> cellsTodo.add(c) }
        while (cellsTodo.size > 0) {
            // println("outer loop")
            // // print("todo"); println(cellsTodo)
            var doingCell = cellsTodo.first()
            var doingCells = mutableSetOf<Cell>(doingCell)
            val plant = garden.get(doingCell)
            var region = mutableSetOf<Cell>(doingCell)
            cellsTodo.remove(doingCell)
            do {
                // println("making region $plant")
                doingCell = doingCells.first()
                doingCells.remove(doingCell)
                // // // print("doing"); print(doingCell); println(doingCells)
                val adjacentCells = garden.getAdjacent(doingCell).filter{ !region.contains(it) }
                // // print("adjacent"); println(adjacentCells)
                region.addAll(adjacentCells)
                doingCells.addAll(adjacentCells)
                cellsTodo.removeAll(adjacentCells)
                // // print("region"); println(region)
            } while (doingCells.size > 0)
            regions.add(plant to region.toSet())
        }
        return regions.toSet()
    }
    fun part1(input: List<String>): Int {
        val garden = Grid<Char>(input) { it }
        val regions = makeRegions(garden)
        var keys = mutableSetOf<Char>()
        garden.forEach{ _, v ->
            keys.add(v)
        }
        var regionsByPlant = mutableMapOf<Char, MutableList<Set<Cell>>>()
        for (k in keys) {
            for (region in regions.filter{ (kk, _) -> k == kk}.map{ (_, region) -> region}) {
                val was = regionsByPlant.getOrPut(k){ mutableListOf<Set<Cell>>() }
                regionsByPlant[k]?.add(region)
            }
        }

        fun price(region: Set<Cell>): Int {
            val a = region.size
            val p = garden.perimeterOf(region)
            return a * p
        }
        return regionsByPlant.values.map{ regions -> regions.map{ price(it) }.sum() }.sum()
    }

    fun part2(input: List<String>): Int {
        val garden = Grid<Char>(input) { it }
        val regions = makeRegions(garden)
        var keys = mutableSetOf<Char>()
        garden.forEach{ _, v ->
            keys.add(v)
        }
        var regionsByPlant = mutableMapOf<Char, MutableList<Set<Cell>>>()
        for (k in keys) {
            for (region in regions.filter{ (kk, _) -> k == kk}.map{ (_, region) -> region}) {
                val was = regionsByPlant.getOrPut(k){ mutableListOf<Set<Cell>>() }
                regionsByPlant[k]?.add(region)
            }
        }

        fun price(region: Set<Cell>): Int {
            val a = region.size
            val p = garden.simplifiedPerimeterOf(region)
            return a * p
        }
        return regionsByPlant.values.map{ regions -> regions.map{ price(it) }.sum() }.sum()
    }

    // println(part1(listOf(
    //     "AAAA",
    //     "BBCD",
    //     "BBCC",
    //     "EEEC",
    // )))
    // println(part1(listOf(
    //     "OOOOO",
    //     "OXOXO",
    //     "OOOOO",
    //     "OXOXO",
    //     "OOOOO",
    // )))
    // println(part1(listOf(
    //     "RRRRIICCFF",
    //     "RRRRIICCCF",
    //     "VVRRRCCFFF",
    //     "VVRCCCJFFF",
    //     "VVVVCJJCFE",
    //     "VVIVCCJJEE",
    //     "VVIIICJJEE",
    //     "MIIIIIJJEE",
    //     "MIIISIJEEE",
    //     "MMMISSJEEE",
    // )))

    val input = readInput("Day12")

    val result1 = part1(input)
    println("part 1: $result1")
    val result2 = part2(input)
    println("part 2: $result2")
}
