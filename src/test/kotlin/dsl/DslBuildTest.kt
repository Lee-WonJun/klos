package dsl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.compilation.shouldCompile
import io.kotest.matchers.compilation.shouldNotCompile
import io.kotest.matchers.shouldBe

class DslBuildTest : FunSpec({
    data class Person(val name: String, val age: Int)

    test("Distinct Operator") {
        val query = Query(Person::class) {
            Select Distinct (Person::class)
            From (Person::class)
        }
        query.distinct shouldBe DistinctExpr.Distinct

        val query2 = Query(Person::class) {
            Select (Person::class)
            From (Person::class)
        }
        query2.distinct shouldBe DistinctExpr.All
    }

    test("dsl generate Expr") {
        val query =Query(Person::class) {
            Select Distinct (Person::class)
            From (Person::class)
            Where {
                ((Person::name `==` "John") And
                        (Person::age gt 10) And
                        (Person::age lt 20)) Or
                        (Person::name `==` "Jane")
            }
        }
        query shouldBe SelectExpr<Person>(
            columns = ColumnExpr.Asterisk(Person::class),
            distinct = DistinctExpr.Distinct,
            from = FromExpr(Person::class),
            where = WhereExpr.Where(
                BinaryOp.Logical(
                    left = BinaryOp.Logical(
                        left = BinaryOp.Logical(
                            left = BinaryOp.Compare(
                                left = ColumnExpr.Column(Person::name),
                                op = CompareOperator.EQ,
                                right = LiteralExpr("John")
                            ),
                            op = LogicalOperator.AND,
                            right = BinaryOp.Compare(
                                left = ColumnExpr.Column(Person::age),
                                op = CompareOperator.GT,
                                right = LiteralExpr(10)
                            )
                        ),
                        op = LogicalOperator.AND,
                        right = BinaryOp.Compare(
                            left = ColumnExpr.Column(Person::age),
                            op = CompareOperator.LT,
                            right = LiteralExpr(20)
                        )
                    ),
                    op = LogicalOperator.OR,
                    right = BinaryOp.Compare(
                        left = ColumnExpr.Column(Person::name),
                        op = CompareOperator.EQ,
                        right = LiteralExpr("Jane")
                    )
                )
            )
        )
    }
})
