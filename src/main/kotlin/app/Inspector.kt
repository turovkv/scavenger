package app

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.visitor.ModifierVisitor
import com.github.javaparser.ast.visitor.Visitable
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.printer.DotPrinter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter


class Inspector {
    fun inspect(file: File) {
        val cu = StaticJavaParser.parse(file)

        val countVarUsages = mutableMapOf<Pair<Int, String>, Int>()

        cu.accept(object : VoidVisitorAdapter<Void>() {
            var scopeNumber = 0
            val scopeNumberStack = mutableListOf<Int>()
            val scopeVarSetStack = mutableListOf<MutableSet<String>>()

            override fun visit(n: BlockStmt, arg: Void?) {
                scopeNumber++
                scopeNumberStack.add(scopeNumber)
                scopeVarSetStack.add(mutableSetOf())
                super.visit(n, arg)
                scopeVarSetStack.removeLast()
                scopeNumberStack.removeLast()
            }

            override fun visit(n: VariableDeclarator, arg: Void?) {
                val name = n.name.identifier
                scopeVarSetStack.last().add(name)
                super.visit(n, arg)
            }

            override fun visit(n: NameExpr, arg: Void?) {
                val name = n.name.identifier
                for (i in scopeVarSetStack.indices.reversed()) {
                    if (scopeVarSetStack[i].contains(name)) {
                        val curCount = countVarUsages[Pair(scopeNumberStack[i], name)]
                        if (curCount != null) {
                            countVarUsages[Pair(scopeNumberStack[i], name)] = curCount + 1
                        } else {
                            countVarUsages[Pair(scopeNumberStack[i], name)] = 1
                        }
                    }
                }
                super.visit(n, arg)
            }
        }, null)

        countVarUsages.forEach { println("k=${it.key} v=${it.value}") }
        println(cu.toString())

        cu.accept(object : ModifierVisitor<Void>() {
            var scopeNumber = 0

            override fun visit(n: BlockStmt, arg: Void?): Visitable? {
                scopeNumber++
                return super.visit(n, arg)
            }

            override fun visit(n: VariableDeclarator, arg: Void?): Visitable? {
                if (!countVarUsages.contains(Pair(scopeNumber, n.name.identifier))) {
                    return null
                }
                return super.visit(n, arg)
            }
        }, null)
        println("---------------------------------------------------------------")
        println(cu.toString())
//        val printer = DotPrinter(true)
//        FileWriter("ast.dot").use { fileWriter ->
//            PrintWriter(fileWriter).use { printWriter ->
//                printWriter.print(printer.output(cu))
//            }
//        }
    }
}
