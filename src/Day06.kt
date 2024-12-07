import java.util.Collections

fun main() {

    data class Cell(val x: Int, val y: Int)

    fun List<Pair<Cell, Cell>>.isLoop(): Boolean {
        return this.size > 3 && this.take(this.size - 1).contains(this.last())
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
        fun set(x: Int, y: Int): T {
            return cells.get(y).get(x)
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
        fun get(cell: Cell): T {
            return get(cell.x, cell.y)
        }
        fun getOrNull(cell: Cell): T? {
            return getOrNull(cell.x, cell.y)
        }

        fun println() {
            cells.forEach{ println( it.map{ it }.joinToString("") ) }
        }
        fun println(cellPrinter: (cell: T) -> Char) {
            cells.forEach{ println( it.map{ cellPrinter(it) }.joinToString("") ) }
        }
    }
    class Room {
        val grid: Grid<Boolean>
        val startPosition: Cell
        val startDirection: Cell

        constructor(_grid: Grid<Boolean>, _startPosition: Cell, _startDirection: Cell = Cell(0, -1) ) {
            grid = _grid
            startPosition = _startPosition
            startDirection = _startDirection
        }
        constructor(input: List<String>) {
            grid = Grid(input, { it == '#'})
            val y = input.indexOfFirst{ it.contains('^')}
            val x = input.get(y).indexOf('^')
            startPosition = Cell(x, y)
            startDirection = Cell(0, -1)
        }
        fun step(direction: Cell, position: Cell): Cell {
            return Cell(position.x + direction.x, position.y + direction.y)
        }
        fun turn(direction: Cell): Cell {
            return Cell(-direction.y, direction.x)
        }
        fun walk(): List<Pair<Cell, Cell>> {
            var position = startPosition
            var direction = startDirection
            var path: List<Pair<Cell, Cell>> = listOf<Pair<Cell, Cell>>()
            while (!path.isLoop() && grid.getOrNull(position) != null) {
                if (true == grid.getOrNull(step(direction, position))) {
                    direction = turn(direction)
                }
                path = path + Pair(position, direction)
                position = step(direction, position)
            }
            return path
        }
        fun isLoop(): Boolean {
            var position = startPosition
            var direction = startDirection
            var path: MutableList<Pair<Cell, Cell>> = mutableListOf()
            var nodeSet: MutableSet<Pair<Cell, Cell>> = mutableSetOf()
            while (grid.getOrNull(position) != null) {
                if (true == grid.getOrNull(step(direction, position))) {
                    direction = turn(direction)
                }
                val node = Pair(position, direction)
                if (nodeSet.contains(node)) return true
                path.add(node)
                nodeSet.add(node)
                position = step(direction, position)
            }
            return false
        }
        fun println() {
            (0..grid.height-1).forEach{
                y -> (0..grid.width-1).map{
                    x -> if (x == startPosition.x && y == startPosition.y) '^' else if (grid.getOr(x, y, false)) '#' else '.'
                }.joinToString("").println()
            }
        }
    }

    fun permutations(room: Room) = sequence {
        val grid = room.grid
        val X = room.startPosition.x
        val Y = room.startPosition.y
        var basePath = room.walk().drop(1)
        var count = basePath.size

        basePath.forEach{ 
            val (position, _) = it
            val nextMap = Room(Grid<Boolean>(grid, position, true), room.startPosition, room.startDirection)
            println(--count)
            yield( Pair(position, nextMap) )
        }
    }

    fun part1(input: List<String>): Int {
        val room = Room(input)
        val path = room.walk()
        val cells = path.map{ (position, direction) -> position }.toSet()
        return cells.size
    }

    fun printRoom(room: Room, added: Cell) {
        val pathSet = room.walk().map{ (p, _) -> p}.toSet()
        fun makeCell(x: Int, y: Int): Char {
            if (Cell(x, y) == room.startPosition) return '^' 
            if (added == Cell(x, y)) return 'O' 
            if (pathSet.contains(Cell(x, y))) return '+'
            if (true == room.grid.get(x, y)) return '#' 
            return '.'
        }
        Grid<Char>(room.grid.width, room.grid.height, { x, y -> makeCell(x, y) }).println()
    }
    fun part2(input: List<String>): Int {
        val room = Room(input)

        var loopSet: MutableSet<List<Pair<Cell, Cell>>> = mutableSetOf()
        val withLoops = permutations(room).forEach{
            (added, room) -> 
            if (room.isLoop()) {
                // println("Loop in ")
                // printRoom(room, added)
                loopSet.add(room.walk())
            }
        }
        return loopSet.size
    }


    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
