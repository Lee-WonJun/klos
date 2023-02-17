package dsl

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

sealed interface Expr

sealed interface Comparable : Expr

sealed class ColumnExpr<T> : Comparable {
    data class Column<T>(val col:KProperty1<T,Any>) : ColumnExpr<T>()
    data class Columns<T>(val cols: List<KProperty1<T, Any>>) : ColumnExpr<T>()
    data class Asterisk<T : Any>(val entity:KClass<T>) : ColumnExpr<T>()
}

data class LiteralExpr(val value: Any) : Comparable

enum class CompareOperator : Expr {
    EQ, NEQ, GT, GTE, LT, LTE
}

enum class LogicalOperator: Expr  {
    AND, OR
}

sealed class BinaryOp : Expr {
    data class Compare(val left: Comparable, val op: CompareOperator, val right: Comparable) : BinaryOp()
    data class Logical(val left: BinaryOp, val op: LogicalOperator, val right: BinaryOp) : BinaryOp()
}

sealed class WhereExpr : Expr {
    data class Where(val expr: BinaryOp) : WhereExpr()
    object Empty : WhereExpr()
}

sealed class DistinctExpr : Expr {
    object Distinct : DistinctExpr()
    object All : DistinctExpr()
}
data class FromExpr<T:Any>(val entity: KClass<T>)

data class SelectExpr<T:Any>(val columns: ColumnExpr<T>, val distinct: DistinctExpr, val from: FromExpr<T>, val where: WhereExpr)