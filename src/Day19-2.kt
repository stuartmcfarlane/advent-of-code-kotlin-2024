import java.util.Collections
import kotlin.math.pow

fun main() {

    val EOL = 0.toChar()
    data class Node(
        val ch: Char = EOL,
        var left: Int  = 0,
        var right: Int = 0,
        var up: Int    = 0,
        var down: Int  = 0,
    )

    fun <T> MutableList<T>.push(item: T) = this.add(this.count(), item)
    fun <T> MutableList<T>.pop(): T? = if(this.count() > 0) this.removeAt(this.count() - 1) else null
    fun <T> MutableList<T>.peek(): T? = if(this.count() > 0) this[this.count() - 1] else null
    fun <T> MutableList<T>.hasMore() = this.count() > 0 

    fun debug(d: Any) {
        // println(d)
    }
    fun trace(d: Any) {
        println(d)
    }

    fun parse(lines: List<String>): Pair<List<String>, List<String>> {
        println(">parse")
        println(lines)
        val patterns = lines.filter{ it.contains(',') }.flatMap{ it.split(",") }.map{ it.trim() }
        println(patterns)
        val designs = lines.filter{ it.length > 0 && !it.contains(',') }
        println(lines)
        println("<parse")
        return patterns to designs
    }
    fun matchesOf(patterns: List<String>, design: String): Long {
        trace(design)
        trace(patterns)
        fun wrap(size: Int, n: Int): Int {
            if (n < 0) return n + size
            if (size <= n) return n - size
            return n
        }
        fun printNodes(nodes: Array<Node>) {
            // nodes.forEachIndexed{ i, it -> debug("$i | ${if (EOL == it.ch) '.' else it.ch} | ${it.left} :  ${it.right} | ${it.up} :  ${it.down}") }
        }
        fun traceNodes(nodes: Array<Node>) {
            nodes.forEachIndexed{ i, it -> trace("$i | ${if (EOL == it.ch) '.' else it.ch} | ${it.left} :  ${it.right} | ${it.up} :  ${it.down}") }
        }
        fun buildLinks(items: String, options: List<String>): Array<Node> {
            var nodes: MutableList<Node> = mutableListOf()
            fun addAfter(atIdx: Int, nodeIdx: Int) {
                // debug("addAfter $atIdx : $nodeIdx | ${if (nodes[nodeIdx].ch == EOL) '.' else nodes[nodeIdx].ch}")
                nodes[nodeIdx].right = nodes[atIdx].right
                nodes[nodeIdx].left = atIdx
                nodes[nodes[atIdx].right].left = nodeIdx
                nodes[atIdx].right = nodeIdx
            }
            fun addUnder(atIdx: Int, nodeIdx: Int) {
                // debug("addUnder $atIdx : $nodeIdx | ${if (nodes[nodeIdx].ch == EOL) '.' else nodes[nodeIdx].ch}")
                nodes[nodeIdx].down = nodes[atIdx].down
                nodes[nodeIdx].up = atIdx
                nodes[nodes[atIdx].down].up = nodeIdx
                nodes[atIdx].down = nodeIdx
            }
            fun addOver(atIdx: Int, nodeIdx: Int) {
                // debug("addOver $atIdx : $nodeIdx | ${if (nodes[nodeIdx].ch == EOL) '.' else nodes[nodeIdx].ch}")
                nodes[nodeIdx].up = nodes[atIdx].up
                nodes[nodeIdx].down = atIdx
                nodes[nodes[atIdx].up].down = nodeIdx
                nodes[atIdx].up = nodeIdx
            }
            val itemCount = items.length
            // root  null node
            nodes.add(Node())
            var nodeIdx = 1
            items.forEach{
                // item node
                nodes.add(Node(it))
                nodes[nodeIdx].up = nodeIdx
                nodes[nodeIdx].down = nodeIdx
                addAfter(nodeIdx-1, nodeIdx)
                nodeIdx++
            }
            var lastRowIdx = 0
            items.forEachIndexed{ itemIdx, itemCh -> 
                val item = itemIdx + 1
                options.filter{ items.substring(itemIdx).startsWith(it) }.forEach{ rowChars ->
                    var rowIdx = nodeIdx
                    // option null node
                    nodes.add(Node())
                    nodes[nodeIdx].left = nodeIdx
                    nodes[nodeIdx].right = nodeIdx
                    nodes[nodeIdx].up = nodeIdx
                    nodes[nodeIdx].down = nodeIdx
                    addUnder(lastRowIdx, rowIdx)
                    // debug("added")
                    printNodes(Array(nodes.size) { nodes[it] })
                    lastRowIdx = rowIdx
                    nodeIdx++
                    // add row nodes
                    rowChars.forEachIndexed{ colIdx, nodeCh ->
                        nodes.add(Node(nodeCh))
                        val colItem = item + colIdx
                        if (colItem <= items.length) {
                            addOver(colItem, nodeIdx)
                            printNodes(Array(nodes.size) { nodes[it] })
                            addAfter(nodeIdx-1, nodeIdx)
                            printNodes(Array(nodes.size) { nodes[it] })
                            nodeIdx++
                            // debug("added")
                            printNodes(Array(nodes.size) { nodes[it] })
                        }
                    }
                }
            }
            traceNodes(Array(nodes.size) { nodes[it] })
            return Array(nodes.size) { nodes[it] }
        }
        fun countExactCovers(nodes: Array<Node>): Long {
            var tracerCount = 0L
            var count = 0L
            var removed = mutableListOf<Pair<Char, Int>>()
            var tries = mutableListOf<Int>()

            fun fmtTries(): String {
                return tries.map{
                    var option = it
                    var str = mutableListOf<Char>()
                    do {
                        str.add(nodes[option].ch)
                        option = nodes[option].right
                    } while (nodes[option].ch != EOL)
                    str.joinToString("")
                }.joinToString(":")
            }

            fun tracer() {
                tracerCount++
                if (tracerCount % 1000000000 == 0L) {
                    println("$tracerCount: $count ${fmtTries()}")
                }
            }
            fun deleteOption(option: Int) {
                tracer()
                // debug("delete option $option | ${nodes[option].ch}")
                removed.push('o' to option)
                val up = nodes[option].up
                val down = nodes[option].down
                nodes[up].down = down
                nodes[down].up = up
                printNodes(nodes)
            }
            fun insertOption(option: Int) {
                tracer()
                // debug("insert option $option | ${nodes[option].ch}")
                val up = nodes[option].up
                val down = nodes[option].down
                nodes[up].down = option
                nodes[down].up = option
                printNodes(nodes)
            }
            fun deleteItem(item: Int) {
                tracer()
                // debug("delete item $item | ${nodes[item].ch}")
                removed.push('i' to item)
                val left = nodes[item].left
                val right = nodes[item].right
                nodes[left].right = right
                nodes[right].left = left
                printNodes(nodes)
            }
            fun insertItem(item: Int) {
                tracer()
                // debug("insert item $item | ${nodes[item].ch}")
                val left = nodes[item].left
                val right = nodes[item].right
                nodes[left].right = item
                nodes[right].left = item
                printNodes(nodes)
            }
            fun printTries() {
                // debug(fmtTries())
            }
            fun emitCover() {
                printTries()
                count++
            }
            fun countItems(): Int {
                var c = 0
                var n = 0
                do {
                    c++
                    n = nodes[n].right
                } while(n != 0)
                return c
            }

            val itemCount = countItems()
            // ITEM
            // debug("ITEM")
            do {
                var item = nodes[0].right
                var option = nodes[item].down
                if (item == 0) {
                    // emit covering
                    // debug("emit covering")
                    emitCover()
                    // and backtrack to last untried option
                    var done = false
                    while (!done && null != tries.peek()) {
                        option = tries.pop()!!
                        // debug("backtracking to $option")
                        while (! done && null != removed.peek()) {
                            val (type, node) = removed.pop()!!
                            // debug("backtrack $node?")
                            if (node == option) {
                                // debug("found option")
                                if (itemCount < nodes[option].down) {
                                    // debug("found more options ${nodes[option].down}")
                                    // untried option! stop backtracking
                                    removed.push(type to node)
                                    option = nodes[option].down
                                    item = nodes[option].up
                                    done = true
                                    break
                                }
                                if (type == 'i' ) insertItem(node)
                                else insertOption(node)
                                // debug("no alternatives keep backtracking")
                                break
                            }
                            if (type == 'i' ) insertItem(node)
                            else insertOption(node)
                        }
                    }
                }
                if (item != 0) {
                    // debug("item $item")
                    tries.push(option)
                    var optionCol = option
                    // delete columns of this option
                    // debug("delete columns of this option $option")
                    // debug("optionCol $optionCol")
                    while (nodes[optionCol].ch != EOL) {
                        deleteOption(optionCol)

                        // delete rows under this optionCol
                        // debug("delete rows under this optionCol")
                        var optionRow = nodes[optionCol].down
                        // debug("optionRow $optionRow")
                        while (optionRow > itemCount) {
                            // clear out this row
                            // debug("clear out this row")
                            var rowNode = optionRow

                            while (nodes[rowNode].ch != EOL) {
                                deleteOption(rowNode)
                                rowNode = nodes[rowNode].right
                            }

                            optionRow = nodes[optionRow].down
                            // debug("optionRow $optionRow")
                        }
                        deleteItem(optionRow)
                        optionCol = nodes[optionCol].right
                        // debug("optionCol $optionCol")
                    }
                }
                // debug("item $item")
                // debug("tries ${tries.size}")
            } while (item != 0 && 0 != tries.size)

            return count
        }
        var nodes = buildLinks(design, patterns)
        // debug("nodes ")
        printNodes(nodes)

        // return 0L
        return countExactCovers(nodes)
    }
    fun part2(patternsDesigns: Pair<List<String>, List<String>>): Long {
        val (patterns, designs) = patternsDesigns

        return designs.map{ matchesOf(patterns, it) }.sum()
    }

    // require(16L == 
    // part2(parse(listOf(
    //     "r, wr, b, g, bwu, rb, gb, br",
    //     "",
    //     "brwrr",
    //     "bggr",
    //     "gbbr",
    //     "rrbgbr",
    //     "ubwu",
    //     "bwurrg",
    //     "brgr",
    //     "bbrgwb",
    // )))
    // )
    val input = readInput("Day19")

    println(input)
    val result2 = part2(parse(input))
    println("part 2: $result2")
}

