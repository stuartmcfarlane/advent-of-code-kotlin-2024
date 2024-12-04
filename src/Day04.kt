
fun main() {

    fun part1(input: List<String>): Int {
        val rows = input
        val nRows = rows.size
        val nCols = rows.map{ it.length }.max()
        val cols = List(nCols) { iCol -> rows.map{ row -> row[iCol] }.joinToString(separator = "") }

        val diagsUp = (3..2*nRows-4)
            .map{ iRow ->
                (0..iRow).map{ it }.zip((0..iRow).map{ it }.reversed()).filter{ it.first < nRows && it.second < nCols }
                .map{ rows.get(it.first)[it.second]}
                .joinToString(separator = "")
            }
        val diagsDown = (-nCols+4..nCols-4)
            .map{ iCol ->
                (0..nRows-1).map{ it }.zip((iCol..nCols-1).map{ it }).filter{ 0 <= it.second && it.second < nCols }
                .map{ rows.get(it.first)[it.second]}
                .joinToString(separator = "")
            }
        val all = (rows + cols + diagsUp + diagsDown)
        val regexes = listOf(Regex("XMAS"), Regex("XMAS".reversed()))
        val count = all.map{ letters -> regexes.map{ regex -> regex.findAll(letters).count() }.sum() }.sum()
        return count
    }

    fun part2(input: List<String>): Int {
        // move a window of shape X over the grid and try to match MAS or SAM on the diagonals
        val rows = input
        val nRows = rows.size
        val nCols = rows.map{ it.length }.max()

        fun getDataPoint(rows: List<String>, p: Pair<Int, Int>): Char {
            val (a, b) = p
            return rows.get(a)[b]
        }
        fun getDiagUp(rows: List<String>, p: Pair<Int, Int>): String {
            val (a, b) = p
            return listOf(
                getDataPoint(rows, a+2 to b),
                getDataPoint(rows, a+1 to b+1),
                getDataPoint(rows, a   to b+2)
            ).joinToString(separator = "")
        }
        fun getDiagDown(rows: List<String>, p: Pair<Int, Int>): String {
            val (a, b) = p
            return listOf(
                getDataPoint(rows, a   to b),
                getDataPoint(rows, a+1 to b+1),
                getDataPoint(rows, a+2 to b+2)
            ).joinToString(separator = "")
        }
        fun testGridPoint(rows: List<String>, p: Pair<Int, Int>): Boolean {
            return (
                "MAS" == getDiagDown(rows, p)
                || "SAM" == getDiagDown(rows, p)
            ) && (
                "MAS" == getDiagUp(rows, p)
                || "SAM" == getDiagUp(rows, p)
            )
        }

        val searchGrid = (0..nRows-3).flatMap{ iRow -> (0..nCols-3).map{ iCol -> Pair(iRow, iCol)} }
        val count = searchGrid.filter{ testGridPoint(rows, it)}.count()

        return count
    }

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
