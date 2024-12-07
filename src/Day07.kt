import java.util.Collections

fun main() {

    class Calibration {
        val result: Long
        val terms: List<Long>
        val operations: List<(Long, Long) -> Long>

        constructor(_operations: List<(Long, Long) -> Long>, line: String) {
            val (_result, _terms) = line.split(":").map{ it.trim() }
            result = _result.toLong()
            terms = _terms.split(" ").map{ it.trim().toLong() }
            operations = _operations
        }
    }
    fun Calibration.validate(): Boolean {
        fun applyOperations(a: Long, b: Long): List<Long> {
            return operations.map{ it(a, b) }
        }
        fun calcPossibilities(done: List<Long>, todo: List<Long>): List<Long> {
            // println(">calcPossibilities([${done.map{it}.joinToString(", ")}], [${todo.map{it}.joinToString(", ")}])")
            if (todo.size == 0) return done
            val b = todo.first()
            val sofar: List<Long> = done.flatMap{ applyOperations(it, b) }
            val results = calcPossibilities(
                sofar,
                todo.drop(1)
            )
            // println("<calcPossibilities [${results.map{it}.joinToString(", ")}]")
            return results
        }
        // println(">validate $result : [${terms.map{it}.joinToString(", ")}]")
        val results = calcPossibilities(listOf(terms.first()), terms.drop(1))
        // println("<validate ${results.any{ it == result }} $result [${results.filter{it == result}.map{it}.joinToString(", ")}]")
        return results.any{ it == result }
    }
    fun add(a: Long, b: Long): Long { return a + b }
    fun mul(a: Long, b: Long): Long { return a * b }
    fun concat(a: Long, b: Long): Long { return "$a$b".toLong() }

    fun part1(input: List<String>): Long {
        val operations = listOf(::add, ::mul)
        return input.map{ Calibration(operations, it) }.filter{ it.validate() }.map{ it.result }.sum()
    }

    fun part2(input: List<String>): Long {
        val operations = listOf(::add, ::mul, ::concat)
        return input.map{ Calibration(operations, it) }.filter{ it.validate() }.map{ it.result }.sum()
    }

    val input = readInput("Day07")
    print("part 1: ")
    part1(input).println()
    print("part 2: ")
    part2(input).println()
}
