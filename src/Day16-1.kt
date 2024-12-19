import java.util.Collections
import kotlin.math.min
import java.util.Stack

fun main() {

    data class Vec2(val x: Int, val y: Int)

    fun Vec2.add(v: Vec2): Vec2 {
        return Vec2(x + v.x, y + v.y)
    }
    fun Vec2.subtract(v: Vec2): Vec2 {
        return Vec2(x - v.x, y - v.y)
    }

    class Grid <T> {
        val cells: List<List<T>>
        val height: Int
        val width: Int

        constructor(input: List<String>, cellInitializer: (cellInput: Char) -> T) {
            cells = input.map{ it.map( cellInitializer).toList() }.toList()
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
        constructor(grid: Grid<T>, cell: Vec2, v: T) {
            cells = grid.cells.mapIndexed{
                y, row -> row.mapIndexed{
                    x, col -> if (x == cell.x && y == cell.y) v else grid.get(x, y)
                }.toList()
            }.toList()
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
        fun println(cellPrinter: (c: Vec2, v: T) -> Char) {
            cells.forEachIndexed{ y, row -> println( row.mapIndexed{ x, v -> cellPrinter(Vec2(x, y), v) }.joinToString("") ) }
        }
    }

    fun parse(input: List<String>): Grid<Char> {
        return Grid<Char>(input) { it }
    }
    val N = Vec2(0, -1)
    val E = Vec2(1, 0)
    val S = Vec2(0, 1)
    val W = Vec2(-1, 0)
    val MOVES = listOf('^', '>', 'v', '<')
    val move2vec = mapOf<Char, Vec2>('v' to S, '>' to E, '<' to W, '^' to N)
    val vec2move = mapOf<Vec2, Char>(S to 'v', E to '>', W to '<', N to '^')

    fun traverse(maze: Grid<Char>): List<List<Vec2>> {
        var path = Stack<Vec2>()
        var paths: MutableList< Stack<Vec2> > = mutableListOf()
        // Push to connectionsPath the object that would be passed as the parameter 'node' into the method below
        fun findAllPaths(start: Vec2, end: Vec2) {
            for (next in MOVES.map{ start.add(move2vec[it]!!) }.filter{ maze.getOr(it, '#') != '#'}) {
                if (next.equals(end)) {
                    var tempPath = Stack<Vec2>()
                    for (node1 in path)
                        tempPath.add(node1)
                    paths.add(tempPath)
                } else if (!path.contains(next)) {
                    path.push(next)
                    findAllPaths(next, end)
                    path.pop()
                }
            }
        }
        val start = maze.find{ it == 'S' }.first()
        val end = maze.find{ it == 'E' }.first()
        findAllPaths(start, end)
        return paths.map{ listOf(start) + it }
    }
    fun costOf(path: List<Vec2>): Int {
        if (path.size == 0) return 0
        if (path.size == 1) return 1
        if (path.size == 2) return 2
        var p0 = path.first()
        var direction = E
        var turns = 0
        for (p1 in path.drop(1)) {
            val nextDirection = p1.subtract(p0)
            if (direction != nextDirection) {
                turns++
                direction = nextDirection
            }
            p0 = p1
        }
        return path.size + turns * 1000
    }
    fun printPath(maze: Grid<Char>, path: List<Vec2>) {
        maze.println{ c, v -> if (v == 'S') 'S' else if (path.contains(c)) '+' else v }
    }
    fun part1(maze: Grid<Char>): Int {
        return traverse(maze).map{ costOf(it) }.sorted().first()
    }

    require(11048 == part1(parse(listOf(
        "#################",
        "#...#...#...#..E#",
        "#.#.#.#.#.#.#.#.#",
        "#.#.#.#...#...#.#",
        "#.#.#.#.###.#.#.#",
        "#...#.#.#.....#.#",
        "#.#.#.#.#.#####.#",
        "#.#...#.#.#.....#",
        "#.#.#####.#.###.#",
        "#.#.#.......#...#",
        "#.#.###.#####.###",
        "#.#.#...#.....#.#",
        "#.#.#.#####.###.#",
        "#.#.#.........#.#",
        "#.#.#.#########.#",
        "#S#.............#",
        "#################",
    ))))
    require(7036 == part1(parse(listOf(
        "###############",
        "#.......#....E#",
        "#.#.###.#.###.#",
        "#.....#.#...#.#",
        "#.###.#####.#.#",
        "#.#.#.......#.#",
        "#.#.#####.###.#",
        "#...........#.#",
        "###.#.#####.#.#",
        "#...#.....#.#.#",
        "#.#.#.###.#.#.#",
        "#.....#...#.#.#",
        "#.###.#.#.#.#.#",
        "#S..#.....#...#",
        "###############",
    ))))
    val input = readInput("Day16")

    val result1 = part1(parse(input))
    println("part 1: $result1")
}

