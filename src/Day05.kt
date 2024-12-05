import java.util.Collections

fun main() {

    data class Rule(val a: Int, val b: Int)

    fun Rule.isApplicable(update: List<Int>): Boolean {
        return update.contains(this.a) && update.contains(this.b)
    }
    fun Rule.isSatisfied(update: List<Int>): Boolean {
        return update.lastIndexOf(this.a) < update.indexOf(this.b)
    }
    fun Rule.apply(update: MutableList<Int>): MutableList<Int> {
        if (this.isSatisfied(update)) return update
        val ib = update.indexOf(this.b)
        val ia = update.indexOf(this.a)
        Collections.swap(update, ib, ia)
        return update
    }
    fun makeRule(rule: String): Rule {
        val (a, b) = rule.split("|")
        return Rule(a.toInt(), b.toInt())
    }

    fun parseInput(input: List<String>): Pair<List<Rule>, List<List<Int>>> {
        val rules = input.filter{ it.contains("|") }.map{ makeRule(it) }
        val updates = input.filter{ it.contains(",") }.map{ it.split(",").map{ it.toInt() } }
        return Pair(rules, updates)
    }
    fun isValidUpdate(rules: List<Rule>, update: List<Int>): Boolean {
        val applicableRules = rules.filter{ it.isApplicable(update) }
        val failedRules = applicableRules.filter{ ! it.isSatisfied(update) }
        return failedRules.isEmpty()
    }
    fun fixUpdate(rules: List<Rule>, update: List<Int>): List<Int> {
        val applicableRules = rules.filter{ it.isApplicable(update) }
        var mutableUpdate = update.toMutableList()
        while (!isValidUpdate(applicableRules, mutableUpdate)) {
            mutableUpdate = applicableRules.fold(
                mutableUpdate, 
                { mutableUpdate, rule -> rule.apply(mutableUpdate) }
            )
        }
        return mutableUpdate.toList()
    }
    fun middleValue(update: List<Int>): Int {
        return update.elementAt(update.size/2)
    }
    fun part1(input: List<String>): Int {
        val (rules, updates) = parseInput(input)
        val validUpdates = updates.filter{ isValidUpdate(rules, it) }
        val result = validUpdates.map{ middleValue(it) }.sum()
        return result
    }

    fun part2(input: List<String>): Int {
        val (rules, updates) = parseInput(input)
        val inValidUpdates = updates.filter{ ! isValidUpdate(rules, it) }
        val fixedUpdates = inValidUpdates.map{ fixUpdate(rules, it) }
        val result = fixedUpdates.map{ middleValue(it) }.sum()
        return result
    }

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
