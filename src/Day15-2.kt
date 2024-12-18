import java.util.Collections
import kotlin.math.min

fun main() {

    fun List<Boolean>.and(): Boolean {
        return this.fold(true) { acc, n -> acc && n }
    }

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
        val simpleGrid = MutableGrid<Char>(room) { it }
        var grid = MutableGrid<Char>(simpleGrid.width * 2, simpleGrid.height) { _, _ -> '.' }
        simpleGrid.find{ it == '#' }.forEach{ c ->
            val (x, y) = c
            grid.set(Vec2(x*2, y), '#')
            grid.set(Vec2(x*2, y).add(E), '#')
        }
        simpleGrid.find{ it == 'O' }.forEach{ c ->
            val (x, y) = c
            grid.set(Vec2(x*2, y), '[')
            grid.set(Vec2(x*2, y).add(E), ']')
        }
        simpleGrid.find{ it == '@' }.forEach{ c ->
            val (x, y) = c
            grid.set(Vec2(x*2, y), '@')
        }
        val moves = input.filter{ Regex("[v><^]").containsMatchIn(it) }.joinToString("").toList()
        return grid to moves
    }
    fun runMoveEW(moveFrom: Vec2, grid: MutableGrid<Char>, move: Char): MutableGrid<Char> {
        val moveTo = moveFrom.add(move2vec[move]!!)
        if (grid.get(moveTo) == '[' || grid.get(moveTo) == ']') {
            // push boxes
            runMoveEW(moveTo, grid, move)
        }
        if (grid.get(moveTo) == '#' || grid.get(moveTo) == '[' || grid.get(moveTo) == ']') {
            // wall or boxed (eventually)
            return grid
        }
        val movingObject = grid.get(moveFrom)
        grid.set(moveFrom, '.')
        grid.set(moveTo, movingObject)
        return grid
    }
    fun testMoveNS(moveFrom: Vec2, grid: MutableGrid<Char>, moveCh: Char): Boolean {
        val move = move2vec[moveCh]!!
        val movingObject = grid.get(moveFrom)
        val moveCells = (
            if (movingObject == '[') listOf(moveFrom, moveFrom.add(E))
            else if (movingObject == ']') listOf(moveFrom, moveFrom.add(W))
            else listOf(moveFrom)
        )
        // empty target cells?
        if (moveCells.map{ it.add(move) }.filter{ grid.getOr(it, '#') == '.' }.size == moveCells.size) {
            // target empty
            return true
        }
        // target cells contain barrier?
        if (moveCells.map{ it.add(move) }.filter{ grid.getOr(it, '#') == '#' }.size != 0) {
            // target blocked
            return false
        }
        // target cells must contain box(es)
        return moveCells.map{ testMoveNS( it.add(move), grid, moveCh) }.and()
    }
    fun runUncheckedMoveNS(moveFrom: Vec2, grid: MutableGrid<Char>, move: Vec2): MutableGrid<Char> {
        val movingObject = grid.get(moveFrom)
        val moveCells = (
            if (movingObject == '[') listOf(moveFrom, moveFrom.add(E))
            else if (movingObject == ']') listOf(moveFrom, moveFrom.add(W))
            else listOf(moveFrom)
        )
        // target cells contain box?
        if (moveCells.map{ it.add(move) }.filter{ grid.getOr(it, '#') == '[' || grid.getOr(it, '#') == ']' }.size != 0) {
            // move boxes
            moveCells.forEach{ runUncheckedMoveNS(it.add(move), grid, move) }
        }
        // empty target cells?
        if (moveCells.map{ it.add(move) }.filter{ grid.getOr(it, '#') == '.' }.size == moveCells.size) {
            // move into empty target cells
            moveCells.forEach{
                grid.set(it.add(move), grid.get(it))
                grid.set(it, '.')
            }
        }
        return grid
    }

    fun runMoveNS(moveFrom: Vec2, grid: MutableGrid<Char>, move: Char): MutableGrid<Char> {
        if (!testMoveNS(moveFrom, grid, move)) return grid
        // all clear, do the move
        return runUncheckedMoveNS(moveFrom, grid, move2vec[move]!!)
    }
    fun runMove(moveFrom: Vec2, grid: MutableGrid<Char>, move: Char): MutableGrid<Char> {
        if (move == '<' || move == '>') return runMoveEW(moveFrom, grid, move)
        return runMoveNS(moveFrom, grid, move)
    }
    fun runMoves(grid: MutableGrid<Char>, moves: List<Char>): MutableGrid<Char> {
        if (moves.size == 0) return grid
        var todo = moves.toMutableList()
        // println("Initial ..... ${todo[0]}")
        // grid.println()
        while (todo.size > 0) {
            val robotPosition = grid.find{ it == '@' }.first()
            runMove(robotPosition, grid, todo.first())
            // println("Move ${todo.first()} ..... ${if (todo.size > 1) todo[1] else ' '}")
            // grid.println()
            todo.removeAt(0)
        }
        return grid
    }
    fun gps(boxPosition: Vec2): Int {
        return boxPosition.x + 100 * boxPosition.y
    }
    fun part2(input: Pair<MutableGrid<Char>, List<Char>>): Int {
        var grid = input.first
        val moves = input.second
        runMoves(grid, moves)
        grid.println()
        return grid.find{ it == '[' }.map{ gps(it) }.sum()
    }

    // part2(parse(listOf(
    //     "#######",
    //     "#...#.#",
    //     "#.....#",
    //     "#..OO@#",
    //     "#..O..#",
    //     "#.....#",
    //     "#######",
    //     "",
    //     "<vv<<^^<<^^",
    // )))

    require(9021 == part2(parse(listOf(
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
    ))) )
    val input = readInput("Day15")

    val result2 = part2(parse(input))
    println("part 2: $result2")
}
