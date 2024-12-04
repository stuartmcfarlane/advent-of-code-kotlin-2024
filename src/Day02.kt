import kotlin.math.abs


fun main() {

    fun isSafe(report: List<Int>): Boolean {
        val steps = report.zipWithNext{ a, b -> b - a}
        val onlyIncreasing = steps.min() > 0
        val onlyDecreasing = steps.max() < 0

        if (!onlyDecreasing && !onlyIncreasing) return false

        val stepSizes = steps.map{ abs(it) }

        val stepsJustRight = 1 <= stepSizes.min() && stepSizes.max() <= 3

        return stepsJustRight
    }

    fun part1(input: List<String>): Int {
        val reports = input.map{ it.split(Regex("\\s+")).map{ it.toInt()} }
        val safeReports = reports.filter{ isSafe(it) }
        return safeReports.size
    }

    fun part2(input: List<String>): Int {
        fun makePermutations(report: List<Int>): List<List<Int>> {
            return (0..report.size).map{ maskIdx -> report.filterIndexed{ idx, n -> maskIdx != idx }}
        }

        val reports = input.map{ it.split(Regex("\\s+")).map{ it.toInt()} }
        val unsafeReports = reports.filter{ !isSafe(it) }
        val permutedReports = unsafeReports.map{ makePermutations(it)}
        val eventuallySafe = permutedReports.filter{
            reportPermutations -> null != reportPermutations.find{
                permutedReport -> isSafe(permutedReport)
            }
        }
        return reports.size - unsafeReports.size + eventuallySafe.size
    }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
