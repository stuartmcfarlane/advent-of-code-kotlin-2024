import java.util.Collections
import kotlin.math.pow

fun main() {

    fun debug(d: Any) {
        // println(d)
    }

    class Computer {
        val program: List<Int>
        var A: Int
        var B: Int
        var C: Int
        var IP: Int
        var output: MutableList<Int>

        constructor(_A: Int, _B: Int, _C: Int, _program: List<Int>) {
            A = _A
            B = _B
            C = _C
            program = _program
            IP = 0
            output = mutableListOf()
        }
        fun operand(): Int {
            return program[IP+1]
        }
        fun literal(): Int {
            return operand()
        }
        fun combo(): Int {
            return when {
                operand() <= 3 -> operand()
                operand() == 4 -> A
                operand() == 5 -> B
                operand() == 6 -> C
                else -> 0
            }
        }
        fun adv() {
            debug("adv ${A / (1 shl combo())} = $A / (1 shl ${combo()})")
            A = A / (1 shl combo())
            IP += 2
        }
        fun bdv() {
            debug("bdv ${A / (1 shl combo())} = $A / (1 shl ${combo()})")
            B = A / (1 shl combo())
            IP += 2
        }
        fun cdv() {
            debug("cdv ${A / (1 shl combo())} = $A / (1 shl ${combo()})")
            C = A / (1 shl combo())
            IP += 2
        }
        fun bxl() {
            debug("bxl ${B xor literal()} = $B xor ${literal()}")
            B = B xor literal()
            IP += 2
        }
        fun bst() {
            debug("bst ${combo() % 8} = ${combo()} % 8")
            B = combo() % 8
            IP += 2
        }
        fun jnz() {
            debug("jnz $A ${literal()}")
            if (A != 0) {
                IP = literal()
                return
            }
            IP += 2
        }
        fun bxc() {
            debug("bxc ${B xor C} = $B xor $C")
            B = B xor C
            IP += 2
        }
        fun out() {
            debug("out ${output.joinToString(",")}, ${combo() % 8}")
            output.add(combo() % 8)
            IP += 2
        }
        fun opcode(): Int {
            return program[IP]
        }
        fun printComputer() {
            println("=============")
            println(A)
            println(B)
            println(C)
            println(IP)
            println(program.joinToString(","))
            println("------------")
            println(output.joinToString(","))
        }
        fun run(): List<Int> {
            while (IP < program.size) {
                // printComputer()
                when (opcode()) {
                    0 -> adv()
                    1 -> bxl()
                    2 -> bst()
                    3 -> jnz()
                    4 -> bxc()
                    5 -> out()
                    6 -> bdv()
                    7 -> cdv()
                }
            }
            return output
        }
    }
    fun parse(input: List<String>): Computer {
        var A: Int = 0
        var B: Int = 0
        var C: Int = 0
        var program: List<Int> = listOf()
        input.forEach{
            when {
                it.matches("""Register A:.*""".toRegex()) -> A = it.substringAfter(":").trim().toInt()
                it.matches("""Register B:.*""".toRegex()) -> B = it.substringAfter(":").trim().toInt()
                it.matches("""Register C:.*""".toRegex()) -> C = it.substringAfter(":").trim().toInt()
                it.matches("""Program:.*""".toRegex()) -> program = it.substringAfter(":").trim().split(",").map{ it.toInt() }
            }
        }
        return Computer(A, B, C, program)
    }
    fun part1(computer: Computer): String {
        return computer.run().joinToString(",")
    }

    require("4,6,3,5,6,3,5,2,1,0" == 
    part1(parse(listOf(
        "Register A: 729",
        "Register B: 0",
        "Register C: 0",
        "",
        "Program: 0,1,5,4,3,0",
    )))
    )
    val input = readInput("Day17")

    val result1 = part1(parse(input))
    println("part 1: $result1")
}

