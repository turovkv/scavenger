package app

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.visitor.ModifierVisitor
import com.github.javaparser.ast.visitor.Visitable
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import java.io.PrintStream

class Scavenger {
    fun removeUnusedVarDecls(code: String, log: PrintStream, notDeep: Boolean, quiet: Boolean): String {
        val compilationUnit = StaticJavaParser.parse(code)

        var iteration = 1
        while (true) {
            val unusedVarDecls = getUnusedAstVarDecls(compilationUnit)
            if (unusedVarDecls.isEmpty()) {
                if (!quiet && iteration == 1) {
                    log.println("No declarations removed!")
                }
                break
            }
            removeUnusedAstVarDecls(compilationUnit, unusedVarDecls)
            if (!quiet) {
                log.println("removed on iteration $iteration :")
                unusedVarDecls.forEach {
                    log.println("variable declaration on line ${it.line} with name ${it.name}  (scopeId ${it.scopeId})")
                }
            }
            if (notDeep) {
                break
            }
            iteration++
        }

        return compilationUnit.toString()
    }

    private fun removeUnusedAstVarDecls(compilationUnit: CompilationUnit, unusedVarDecls: Set<VariableDeclaration>) {
        val removed = mutableSetOf<VariableDeclaration>()

        compilationUnit.accept(
            object : ModifierVisitor<Void>() {
                var curScopeId = 0

                override fun visit(n: BlockStmt, arg: Void?): Visitable? {
                    curScopeId++
                    return super.visit(n, arg)
                }

                override fun visit(n: VariableDeclarator, arg: Void?): Visitable? {
                    val name = n.name.identifier
                    val declaration = VariableDeclaration(name, curScopeId, n.range.get().begin.line)
                    if (unusedVarDecls.contains(declaration)) {
                        removed.add(declaration)
                        return null
                    }
                    return super.visit(n, arg)
                }
            },
            null
        )

        check(unusedVarDecls == removed) { "Not all unused declarations removed" }
    }

    private fun getUnusedAstVarDecls(compilationUnit: CompilationUnit): Set<VariableDeclaration> {
        val allDeclarations = mutableSetOf<VariableDeclaration>()
        val usedDeclarations = mutableSetOf<VariableDeclaration>()

        compilationUnit.accept(
            object : VoidVisitorAdapter<Void>() {
                var curScopeId = 0
                val scopeStack = mutableListOf<Scope>()

                override fun visit(n: BlockStmt, arg: Void?) {
                    curScopeId++
                    scopeStack.add(Scope(mutableMapOf()))
                    super.visit(n, arg)
                    scopeStack.removeLast()
                }

                override fun visit(n: VariableDeclarator, arg: Void?) {
                    val name = n.name.identifier
                    val declaration = VariableDeclaration(name, curScopeId, n.range.get().begin.line)
                    scopeStack.last().nameToDecl[name] = declaration
                    allDeclarations.add(declaration)
                    super.visit(n, arg)
                }

                override fun visit(n: NameExpr, arg: Void?) {
                    val name = n.name.identifier
                    for (scope in scopeStack.reversed()) {
                        val decl = scope.nameToDecl[name]
                        if (decl != null) {
                            usedDeclarations.add(decl)
                            break
                        }
                    }
                    super.visit(n, arg)
                }
            },
            null
        )

        allDeclarations.removeAll(usedDeclarations)

        return allDeclarations
    }
}

data class Scope(
    val nameToDecl: MutableMap<String, VariableDeclaration>,
)

data class VariableDeclaration(
    val name: String,
    val scopeId: Int,
    val line: Int,
)
