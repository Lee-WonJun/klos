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

        // 1) KProperty1 / KProperty1
        infix fun <E : Any, T : Any> KProperty1<E, T>.`==`(other: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.EQ, ColumnExpr.Column(other))

        infix fun <E : Any, T : Any> KProperty1<E, T>.ne(other: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.NEQ, ColumnExpr.Column(other))

        infix fun <E : Any, T : Any> KProperty1<E, T>.gt(other: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.GT, ColumnExpr.Column(other))

        infix fun <E : Any, T : Any> KProperty1<E, T>.gte(other: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.GTE, ColumnExpr.Column(other))

        infix fun <E : Any, T : Any> KProperty1<E, T>.lt(other: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.LT, ColumnExpr.Column(other))

        infix fun <E : Any, T : Any> KProperty1<E, T>.lte(other: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.LTE, ColumnExpr.Column(other))


        // 2) KProperty1 / Value
        infix fun <E : Any, T : Any> KProperty1<E, T>.`==`(value: T): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.EQ, LiteralExpr(value))

        infix fun <E : Any, T : Any> KProperty1<E, T>.ne(value: T): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.NEQ, LiteralExpr(value))

        infix fun <E : Any, T : Any> KProperty1<E, T>.gt(value: T): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.GT, LiteralExpr(value))

        infix fun <E : Any, T : Any> KProperty1<E, T>.gte(value: T): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.GTE, LiteralExpr(value))

        infix fun <E : Any, T : Any> KProperty1<E, T>.lt(value: T): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.LT, LiteralExpr(value))

        infix fun <E : Any, T : Any> KProperty1<E, T>.lte(value: T): BinaryOp.Compare =
            BinaryOp.Compare(ColumnExpr.Column(this), CompareOperator.LTE, LiteralExpr(value))


        // 3) Value / KProperty1
        infix fun <E, T : Any> T.`==`(prop: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.EQ, ColumnExpr.Column(prop))

        infix fun <E, T : Any> T.ne(prop: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.NEQ, ColumnExpr.Column(prop))

        infix fun <E, T : Any> T.gt(prop: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.GT, ColumnExpr.Column(prop))

        infix fun <E, T : Any> T.gte(prop: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.GTE, ColumnExpr.Column(prop))

        infix fun <E, T : Any> T.lt(prop: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.LT, ColumnExpr.Column(prop))

        infix fun <E, T : Any> T.lte(prop: KProperty1<E, T>): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.LTE, ColumnExpr.Column(prop))


        // 4) Value / Value
        infix fun <T : Any> T.`==`(other: T): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.EQ, LiteralExpr(other))

        infix fun <T : Any> T.ne(other: T): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.NEQ, LiteralExpr(other))

        infix fun <T : Any> T.gt(other: T): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.GT, LiteralExpr(other))

        infix fun <T : Any> T.gte(other: T): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.GTE, LiteralExpr(other))

        infix fun <T : Any> T.lt(other: T): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.LT, LiteralExpr(other))

        infix fun <T : Any> T.lte(other: T): BinaryOp.Compare =
            BinaryOp.Compare(LiteralExpr(this), CompareOperator.LTE, LiteralExpr(other))



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

