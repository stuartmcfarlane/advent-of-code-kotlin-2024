import java.util.Collections

fun main() {

    fun part1(input: List<String>): Long {
        val diskMap = input.first()
        val blocks = diskMap.mapIndexed{ i, n -> Pair( if (i % 2 == 0) i / 2 else -1, "$n".toInt())}
        var into = blocks.flatMap{ (i, n) -> (1..n).map{ i } }.toMutableList()
        var from = into.filter{ it != -1 }.toMutableList()
        var compressed = mutableListOf<Int>()
        while (from.size > 0) {
            if (into.removeFirst() != -1) compressed.add(from.removeFirst())
            else compressed.add(from.removeLast())
        }
        return compressed .map{ it.toLong() } .mapIndexed{ i, n -> i * n } .sum()
    }

    fun printBlocks( blocks: List<Pair<Int, Int>>) {
        println( blocks.flatMap{ (i, n) -> (1..n).map{ if (-1 == i) "." else "$i" } }.joinToString("") )
    }
    fun part2(input: List<String>): Long {
        val diskMap = input.first()
        val blocks = diskMap.mapIndexed{ i, n -> Pair( if (i % 2 == 0) i / 2 else -1, "$n".toInt())}
        var compressed = blocks.toMutableList()
        var done = false
        var putCursor = 0
        var getCursor = blocks.size - 1
        for (sourceBlock in blocks.filter{ (i, _) -> i != -1 }.reversed()) {
            val sourceIdx = compressed.indexOf(sourceBlock)
            val (iSource, nSource) = sourceBlock
            for (targetIdx in (1..sourceIdx - 1)) {
                val (iTarget, nTarget) = compressed.get(targetIdx)
                if (iTarget == -1 && nTarget >= nSource) {
                    compressed[sourceIdx] = Pair(-1, nSource)
                    compressed[targetIdx] = sourceBlock
                    if (nTarget > nSource) compressed.add(targetIdx+1, Pair(-1, nTarget - nSource))
                    break
                }
            }
        }
        val result = compressed.flatMap{ (i, n) -> (1..n).map{ i } }.map{ if (it == -1) 0 else it } .mapIndexed{ i, n -> i * n }.map{ it.toLong() }.sum()
        return result
    }


    val input = readInput("Day09")
    val result1 = part1(input)
    val result2 = part2(input)

    print("part 1: ")
    println(result1)
    print("part 2: ")
    println(result2)
}
