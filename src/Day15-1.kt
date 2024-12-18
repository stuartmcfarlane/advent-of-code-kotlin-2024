import java.util.Collections
import kotlin.math.min

fun main() {

    data class Vec2(val x: Int, val y: Int)

    fun Vec2.add(v: Vec2): Vec2 {
        return Vec2(x + v.x, y + v.y)
    }
    fun Vec2.mul(n: Int): Vec2 {
        return Vec2(x * n, y * n)
    }

   class MutableGrid <T> {
        var cells: MutableList<MutableList<T>>
        val height: Int
        val width: Int

        constructor(input: List<String>, cellInitializer: (cellInput: Char) -> T) {
            cells = input.map{ it.map( cellInitializer).toMutableList() }.toMutableList()
            height = input.size
            width = input.map{ it.length }.max()
        }
        constructor(_width: Int, _height: Int, cellInitializer: (x: Int, y: Int) -> T) {
            width = _width
            height = _height
            cells = (0..height-1).map{
                y -> (0..width-1).map{
                    x -> cellInitializer(x, y)
                }.toMutableList()
            }.toMutableList()
        }
        constructor(grid: MutableGrid<T>, cell: Vec2, v: T) {
            cells = grid.cells.mapIndexed{
                y, row -> row.mapIndexed{
                    x, col -> if (x == cell.x && y == cell.y) v else grid.get(x, y)
                }.toMutableList()
            }.toMutableList()
            height = grid.height
            width = grid.width
        }
        fun set(x: Int, y: Int, t: T) {
            if (0 <= x && 0 <= y && x < width && y < height) cells.get(y).set(x, t)
        }
        fun set(cell: Vec2, t: T) {
            set(cell.x, cell.y, t)
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
        fun getOr(cell: Vec2, or: T): T {
            return getOr(cell.x, cell.y, or)
        }
        fun get(cell: Vec2): T {
            return get(cell.x, cell.y)
        }
        fun getOrNull(cell: Vec2): T? {
            return getOrNull(cell.x, cell.y)
        }
        fun forEach( visit: (cell: Vec2, v: T) -> Unit) {
            for (y in 0..height-1) {
                for (x in 0..width-1) {
                    visit(Vec2(x, y), get(x, y))
                }
            }
        }
        fun find(test: (t: T) -> Boolean): List<Vec2> {
            var found = mutableListOf<Vec2>()
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

    val N = Vec2(0, -1)
    val E = Vec2(1, 0)
    val S = Vec2(0, 1)
    val W = Vec2(-1, 0)
    val move2vec = mapOf<Char, Vec2>('v' to S, '>' to E, '<' to W, '^' to N)

    fun parse(input: List<String>): Pair<MutableGrid<Char>, List<Char>> {
        val room = input.filter{ "#".toRegex().containsMatchIn(it) }
        val grid = MutableGrid<Char>(room) { it }
        val moves = input.filter{ Regex("[v><^]").containsMatchIn(it) }.joinToString("").toList()
        return grid to moves
    }
    fun runMove(moveFrom: Vec2, grid: MutableGrid<Char>, move: Char): MutableGrid<Char> {
        // println(">runMove")
        // println(moveFrom)
        // println(move)
        val moveTo = moveFrom.add(move2vec[move]!!)
        if (grid.get(moveTo) == 'O') {
            // push boxes
            runMove(moveTo, grid, move)
        }
        if (grid.get(moveTo) == '#' || grid.get(moveTo) == 'O') {
            // wall or boxed (eventually)
            return grid
        }
        val movingObject = grid.get(moveFrom)
        grid.set(moveFrom, '.')
        grid.set(moveTo, movingObject)
        // grid.println()
        // println("<runMove")
        return grid
    }
    fun runMoves(grid: MutableGrid<Char>, moves: List<Char>): MutableGrid<Char> {
        if (moves.size == 0) return grid
        var todo = moves.toMutableList()
        while (todo.size > 0) {
            val robotPosition = grid.find{ it == '@' }.first()
            runMove(robotPosition, grid, todo.first())
            todo.removeAt(0)
        }
        // println("Move ${moves.first()}")
        // grid.println()
        return grid
    }
    fun gps(boxPosition: Vec2): Int {
        return boxPosition.x + 100 * boxPosition.y
    }
    fun part1(input: Pair<MutableGrid<Char>, List<Char>>): Int {
        var grid = input.first
        val moves = input.second
        // grid.println()
        // moves.println()
        runMoves(grid, moves)
        // grid.println()
        // println(grid.find{ it == 'O' }.map{ gps(it) }.sum())
        return grid.find{ it == 'O' }.map{ gps(it) }.sum()
    }

    // part1(parse(listOf(
    //     "########",
    //     "#..O.O.#",
    //     "##@.O..#",
    //     "#...O..#",
    //     "#.#.O..#",
    //     "#...O..#",
    //     "#......#",
    //     "########",
    //     "",
    //     "<^^>>>vv<v>>v<<",
    // )))
    require(10092 == part1(parse(listOf(
        "##########",
        "#..O..O.O#",
        "#......O.#",
        "#.OO..O.O#",
        "#..O@..O.#",
        "#O#..O...#",
        "#O..O..O.#",
        "#.OO.O.OO#",
        "#....O...#",
        "##########",
        "",
        "<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^",
        "vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v",
        "><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<",
        "<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^",
        "^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><",
        "^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^",
        ">^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^",
        "<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>",
        "^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>",
        "v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^",
    ))))
    val input = readInput("Day15")

    val result1 = part1(parse(input))
    println("part 1: $result1")
}
