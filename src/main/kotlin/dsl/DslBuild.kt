package dsl


/*
DSL Grammar

Query(Person::class) {
    Select Distinct (Person::Class)
    From (Person::Class)
    Where { (col(Person::name) == "John") And ((col(Person::age) > 10) }
}
* */

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

class QueryBuilder<T : Any>(private val entity: KClass<T>) {
    class SelectBuilder<T : Any>(private val entity: KClass<T>) {
        var selectColumnExpr : ColumnExpr<T>? = null

        infix fun Distinct(entity: KClass<T>) {
            selectColumnExpr = ColumnExpr.Asterisk(entity)
        }

        infix fun Distinct(columns: List<KProperty1<T, Any>>) {
            selectColumnExpr = ColumnExpr.Columns(columns)
        }

    }

    class WhereBuilder<T : Any> {
        var binaryOp: BinaryOp? = null
        fun col(prop: KProperty1<T, Any>): ColumnExpr.Column<T> = ColumnExpr.Column(prop)
        fun lit(value: Any): LiteralExpr = LiteralExpr(value)

        infix fun ColumnExpr.Column<T>.`==`(lit: LiteralExpr): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(this, CompareOperator.EQ, lit)
            return binaryOp as BinaryOp.Compare
        }

        infix fun ColumnExpr.Column<T>.`!=`(lit: LiteralExpr): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(this, CompareOperator.NEQ, lit)
            return binaryOp as BinaryOp.Compare
        }

        infix fun ColumnExpr.Column<T>.`gt=`(lit: LiteralExpr): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(this, CompareOperator.GTE, lit)
            return binaryOp as BinaryOp.Compare
        }

        infix fun ColumnExpr.Column<T>.`lt=`(lit: LiteralExpr): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(this, CompareOperator.LTE, lit)
            return binaryOp as BinaryOp.Compare
        }

        infix fun ColumnExpr.Column<T>.gt(lit: LiteralExpr): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(this, CompareOperator.GT, lit)
            return binaryOp as BinaryOp.Compare
        }

        infix fun ColumnExpr.Column<T>.lt(lit: LiteralExpr): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(this, CompareOperator.LT, lit)
            return binaryOp as BinaryOp.Compare
        }

        infix fun LiteralExpr.`==`(col: ColumnExpr.Column<T>): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(col, CompareOperator.EQ, this)
            return binaryOp as BinaryOp.Compare
        }
        infix fun LiteralExpr.`!=`(col: ColumnExpr.Column<T>): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(col, CompareOperator.NEQ, this)
            return binaryOp as BinaryOp.Compare
        }
        infix fun LiteralExpr.`g=`(col: ColumnExpr.Column<T>): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(col, CompareOperator.GTE, this)
            return binaryOp as BinaryOp.Compare
        }
        infix fun LiteralExpr.`l=`(col: ColumnExpr.Column<T>): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(col, CompareOperator.LTE, this)
            return binaryOp as BinaryOp.Compare
        }
        infix fun LiteralExpr.gt(col: ColumnExpr.Column<T>): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(col, CompareOperator.GT, this)
            return binaryOp as BinaryOp.Compare
        }
        infix fun LiteralExpr.lt(col: ColumnExpr.Column<T>): BinaryOp.Compare {
            binaryOp = BinaryOp.Compare(col, CompareOperator.LT, this)
            return binaryOp as BinaryOp.Compare
        }


        infix fun BinaryOp.Compare.And(other: BinaryOp.Compare): BinaryOp.Logical {
            binaryOp = BinaryOp.Logical(this, LogicalOperator.AND, other)
            return binaryOp as BinaryOp.Logical
        }
        infix fun BinaryOp.Compare.Or(other: BinaryOp.Compare): BinaryOp.Logical {
            binaryOp = BinaryOp.Logical(this, LogicalOperator.OR, other)
            return binaryOp as BinaryOp.Logical
        }
        infix fun BinaryOp.Logical.And(other: BinaryOp): BinaryOp.Logical {
            binaryOp = BinaryOp.Logical(this, LogicalOperator.AND, other)
            return binaryOp as BinaryOp.Logical
        }
        infix fun BinaryOp.Logical.Or(other: BinaryOp): BinaryOp.Logical {
            binaryOp = BinaryOp.Logical(this, LogicalOperator.OR, other)
            return binaryOp as BinaryOp.Logical
        }

        fun build(): BinaryOp? = binaryOp

    }


    private var selectExpr: SelectExpr<T>? = null

    val Select: SelectBuilder<T> = SelectBuilder(entity)


    var distinct: DistinctExpr = DistinctExpr.All
    var selectColumnExpr : ColumnExpr<T>? = null
    var fromExpr: FromExpr<T>? = null
    var whereExpr: WhereExpr = WhereExpr.Empty

    fun Select(entity: KClass<T>) {
        selectColumnExpr = ColumnExpr.Asterisk(entity)
    }

    fun Select(columns: List<KProperty1<T, Any>>) {
        selectColumnExpr = ColumnExpr.Columns(columns)
    }

    fun From (entity: KClass<T>) {
        fromExpr = FromExpr(entity)
    }


    fun build(): SelectExpr<T> {
        if (this.Select.selectColumnExpr != null) {
            this.selectColumnExpr = this.Select.selectColumnExpr
            this.distinct = DistinctExpr.Distinct
        }
        return SelectExpr(selectColumnExpr!!, distinct, fromExpr!!, whereExpr)
    }

    fun Where(block: WhereBuilder<T>.() -> Unit) {
        val builder = WhereBuilder<T>()
        builder.block()
        whereExpr = WhereExpr.Where(builder.build()!!)
    }
}

fun <T : Any> Query(entity: KClass<T>, block: QueryBuilder<T>.() -> Unit): SelectExpr<T> {
    val builder = QueryBuilder(entity)
    builder.block()
    return builder.build()
}

