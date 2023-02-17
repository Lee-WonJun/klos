package dsl

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.compilation.shouldCompile
import io.kotest.matchers.compilation.shouldNotCompile
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class ExprTest : FunSpec({
    test ("compile will fail if the type is not specified") {
        val code = """
         package dsl
         
        data class Person(val name: String, val age: Int)
        data class Animal(val name: String, val age: Int)
        
         val select = SelectExpr(
             columns = ColumnExpr.Columns(listOf(Person::name, Animal::age)),
             distinct = DistinctExpr.Distinct,
             from = FromExpr(Person::class),
             where = WhereExpr.Where(
                 BinaryOp.Compare(
                     left = LiteralExpr("John"),
                     op = CompareOperator.EQ,
                     right = ColumnExpr.Column(Person::name)
                 )
             )
         )
         """
        code.shouldNotCompile()
    }

    test ("compile will succeed if the type is specified") {
        val code = """
         package dsl
         
         data class Person(val name: String, val age: Int)
         data class Animal(val name: String, val age: Int)
        
         val select = SelectExpr<Person>(
             columns = ColumnExpr.Columns(listOf(Person::name, Person::age)),
             distinct = DistinctExpr.Distinct,
             from = FromExpr(Person::class),
             where = WhereExpr.Where(
                 BinaryOp.Compare(
                     left = LiteralExpr("John"),
                     op = CompareOperator.EQ,
                     right = ColumnExpr.Column(Person::name)
                 )
             )
         )
         """
        code.shouldCompile()
    }

})
