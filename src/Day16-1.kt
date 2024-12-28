import java.util.Collections
import kotlin.math.min
import java.util.PriorityQueue

fun main() {

    fun <T> MutableCollection<T>.removeFirst() = first().also{ remove(it) }
    // fun <T> MutableCollection<T>.popFirst() {
    //     val f = first()
    //     first().also{ remove(it) }
    //     return f
    // }

    data class Vec2(val x: Int, val y: Int)

    fun Vec2.add(v: Vec2): Vec2 {
        return Vec2(x + v.x, y + v.y)
    }
    fun Vec2.subtract(v: Vec2): Vec2 {
        return Vec2(x - v.x, y - v.y)
    }

    val N = Vec2(0, -1)
    val E = Vec2(1, 0)
    val S = Vec2(0, 1)
    val W = Vec2(-1, 0)

    class MutableGrid <T> {
        var cells: MutableList<MutableList<T>>
        val height: Int
        val width: Int

        constructor(input: List<String>, cellInitializer: (cellInput: Char) -> T) {
            cells = input.map{ it.map( cellInitializer).toMutableList() }.toMutableList()
            height = input.size
            width = input.map{ it.length }.max()
        }
        constructor(_width: Int, _height: Int, cellInitializer: (c: Vec2) -> T) {
            width = _width
            height = _height
            cells = (0..height-1).map{
                y -> (0..width-1).map{
                    x -> cellInitializer(Vec2(x, y))
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
        fun isInRange(c: Vec2): Boolean {
            return 0 <= c.x && c.x < width && 0 <= c.y && c.y < height
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
        fun values(): Set<T> {
            return cells.flatten().toSet()
        }
        fun adjacentOf(c: Vec2): List<Vec2> {
            return listOf(N, S, W, E).map{ c.add(it) }.filter{ isInRange(it) }
        }
        fun forEach( visit: (cell: Vec2, v: T) -> Unit) {
            for (y in 0..height-1) {
                for (x in 0..width-1) {
                    visit(Vec2(x, y), get(x, y))
                }
            }
        }
        fun <R> map( transform: (Vec2) -> R): MutableGrid<R> {
            return MutableGrid<R>(width, height, transform)
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
    
    fun fmtNode(n: Int): Char {
        val symbols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return symbols[n % symbols.length]
    }
    fun printGraph(graph: MutableMap<Int, List<Pair<Int, Int>>>) {
        println( graph.entries.map{
            (s, ends) -> fmtNode(s) to ends.map{
                (e, w ) -> fmtNode(e) to w
            }
        }.toMap())
    }
    fun printPath(path: Map<Int, Int>) {
        println(path.entries.map{ (n, w) -> fmtNode(n) to w}.toMap())
    }
    fun printNodes(nodes: List<Int>) {
        println(nodes.map{ fmtNode(it) })
    }
    fun printNumberedStraights(numberedStraights: MutableGrid<Int>) {
        numberedStraights.println{ c, v -> if (v == -1) '.' else fmtNode(v)}
    }

    fun parse(input: List<String>): MutableGrid<Char> {
        return MutableGrid<Char>(input) { it }
    }
    val MOVES = listOf('^', '>', 'v', '<')
    val move2vec = mapOf<Char, Vec2>('v' to S, '>' to E, '<' to W, '^' to N)
    val vec2move = mapOf<Vec2, Char>(S to 'v', E to '>', W to '<', N to '^')

    fun dijkstraWithLoops(graph: Map<Int, List<Pair<Int, Int>>>, start: Int): Map<Int, Int> {
        val distances = mutableMapOf<Int, Int>().withDefault { Int.MAX_VALUE }
        val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { it.second })
        val visited = mutableSetOf<Pair<Int, Int>>()

        priorityQueue.add(start to 0)
        distances[start] = 0

        while (priorityQueue.isNotEmpty()) {
            val (node, currentDist) = priorityQueue.poll()
            if (visited.add(node to currentDist)) {
                graph[node]?.forEach { (adjacent, weight) ->
                    val totalDist = currentDist + weight
                    if (totalDist < distances.getValue(adjacent)) {
                        distances[adjacent] = totalDist
                        priorityQueue.add(adjacent to totalDist)
                    }
                }
            }
        }
        return distances
    }

    fun isWall(maze: MutableGrid<Char>, c: Vec2): Boolean {
        return (maze.getOr(c, '?') == '#')
    }
    fun isStartCell(maze: MutableGrid<Char>, c: Vec2): Boolean {
        return (maze.getOr(c, '?') == 'S')
    }
    fun isEndCell(maze: MutableGrid<Char>, c: Vec2): Boolean {
        return (maze.getOr(c, '?') == 'E')
    }
    fun isStraightCell(maze: MutableGrid<Char>, c: Vec2): Boolean {
        if (isWall(maze, c)) return false
        val adjacent = maze.adjacentOf(c)
        val adjacentWalls = adjacent.filter{ isWall(maze, it) }
        if (adjacentWalls.size == 4) return true
        if (adjacentWalls.size == 3) return true
        if (adjacentWalls.size == 1) return false
        if (isWall(maze, adjacent[0]) && isWall(maze, adjacent[1])) return true
        if (isWall(maze, adjacent[2]) && isWall(maze, adjacent[3])) return true
        return false
    }

    fun numberStraightsOf(straights: MutableGrid<Char>, startCell: Vec2, endCell: Vec2): MutableGrid<Int> {
        var todo = straights.find{ it == 's' }.toMutableSet()
        var numberedStraights = MutableGrid<Int>(straights.width, straights.height) { c -> if (isWall(straights, c)) -1 else 0 }
        var n = 3
        while (todo.size > 0) {
            val doing = todo.removeFirst()
            numberedStraights.set(doing, n)
            for (direction in listOf(N, S, E, W)) {
                var cursor = doing.add(direction)
                while (todo.contains(cursor)) {
                    todo.remove(cursor)
                    numberedStraights.set(cursor, n)
                    cursor = cursor.add(direction)
                }
            }
            n++
        }
        numberedStraights.set(startCell, 1)
        numberedStraights.set(endCell, 2)
        return numberedStraights
    }
    
    fun addStartEdges(
        graph: MutableMap<Int, List<Pair<Int, Int>>>,
        numberedStraights: MutableGrid<Int>,
        startCell: Vec2
    )
    {
        val adjacentNodes = numberedStraights.adjacentOf(startCell).filter{ numberedStraights.get(it) > 0 }
        var edges: MutableMap<Int, List<Pair<Int, Int>>> = mutableMapOf()
        val startNode = numberedStraights.get(startCell)
        var ends = mutableListOf<Pair<Int, Int>>()
        for (end in adjacentNodes) {
            val endNode = numberedStraights.get(end)
            ends.add(endNode to if (startCell.y != end.y) 1000 else 0)
        }
        val oldEnds = edges.getOrPut(startNode) { listOf() }
        edges[startNode] = oldEnds + ends
        edges.entries.forEach{ (start, ends) ->
            val oldEnds = graph.getOrPut(start) { listOf() }
            graph[start] = oldEnds + ends
        }
    
    }
    fun addEndEdges(
        graph: MutableMap<Int, List<Pair<Int, Int>>>,
        numberedStraights: MutableGrid<Int>,
        endCell: Vec2
    )
    {
        val adjacentNodes = numberedStraights.adjacentOf(endCell).filter{ numberedStraights.get(it) > 0 }
        var edges: MutableMap<Int, List<Pair<Int, Int>>> = mutableMapOf()
        for (start in adjacentNodes) {
            val startNode = numberedStraights.get(start)
            val startLength = numberedStraights.find{it == startNode }.size
            var ends = mutableListOf<Pair<Int, Int>>()

            val endNode = numberedStraights.get(endCell)
            if (startNode == endNode) continue
            ends.add(endNode to if (start.x == endCell.x || start.y == endCell.y) startLength + 1 else startLength + 1001)

            val oldEnds = edges.getOrPut(startNode) { listOf() }
            edges[startNode] = oldEnds + ends
        }
        edges.entries.forEach{ (start, ends) ->
            val oldEnds = graph.getOrPut(start) { listOf() }
            graph[start] = oldEnds + ends
        }
    }
    fun addNormalEdges(
        graph: MutableMap<Int, List<Pair<Int, Int>>>,
        numberedStraights: MutableGrid<Int>,
        edgeCell: Vec2
    )
    {
        val adjacentNodes = numberedStraights.adjacentOf(edgeCell).filter{ numberedStraights.get(it) > 0 }
        var edges: MutableMap<Int, List<Pair<Int, Int>>> = mutableMapOf()
        for (start in adjacentNodes) {
            val startNode = numberedStraights.get(start)
            val startLength = numberedStraights.find{it == startNode }.size
            var ends = mutableListOf<Pair<Int, Int>>()
            for (end in adjacentNodes) {
                val endNode = numberedStraights.get(end)
                if (startNode == endNode) continue
                ends.add(endNode to if (start.x == end.x || start.y == end.y) startLength + 1 else startLength + 1001)
            }
            val oldEnds = edges.getOrPut(startNode) { listOf() }
            edges[startNode] = oldEnds + ends
        }
        edges.entries.forEach{ (start, ends) ->
            val oldEnds = graph.getOrPut(start) { listOf() }
            graph[start] = oldEnds + ends
        }
    }
    fun traverse(maze: MutableGrid<Char>): Int {
        // construct a graph of straight sections
        // nodes are straight sections also start and end
        // edges are weighted by the length of the the start node and the whether or not the joint is straight
        val straights = maze.map{ c -> if (isStraightCell(maze, c)) 's' else if (isWall(maze, c)) '#' else '.'}
        val startCell = maze.find{ it == 'S' }.first()
        val endCell = maze.find{ it == 'E' }.first()

        val numberedStraights = numberStraightsOf(straights, startCell, endCell)
        var nodes = numberedStraights.values().filter{ it > 0 }
        var graph = mutableMapOf<Int, List<Pair<Int, Int>>>()

        addStartEdges(graph, numberedStraights, startCell)
        addEndEdges(graph, numberedStraights, endCell)
        numberedStraights.find{ it == 0 }.forEach{
            addNormalEdges(graph, numberedStraights, it)
        }

        var startNode = numberedStraights.get(startCell)
        var endNode = numberedStraights.get(endCell)
        val path = dijkstraWithLoops(graph, startNode)

        val cost = path[endNode]!!
        println(cost)
        return cost
    }

    fun printPath(maze: MutableGrid<Char>, path: List<Vec2>) {
        maze.println{ c, v -> if (v == 'S') 'S' else if (path.contains(c)) '+' else v }
    }
    fun part1(maze: MutableGrid<Char>): Int {
        return traverse(maze)
    }

    require(11048 == 
    part1(parse(listOf(
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
    )))
    )
    require(7036 == 
    part1(parse(listOf(
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
    )))
    )
    val input = readInput("Day16")

    val result1 = part1(parse(input))
    println("part 1: $result1")
}

