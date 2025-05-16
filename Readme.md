# KLOS

KLOS is a Kotlin (DSL) Layer Of SQL. 

This repository is created for educational and recreational purposes.

Kotlin provides a rich set of features that make it possible to write DSLs, and this project aims to create a SQL DSL in the Kotlin style. 

While there are already many SQL DSLs written in Java and Kotlin, most of them do written Java style Api (fluent api style or java builder pattern)

The implementation includes only the SELECT, DISTINCT, FROM, and WHERE clauses.

# Features Used for DSL
- Extension functions
- Infix functions
- Lambda expressions and receivers
- Sealed class for Algebraic Data Types (ADTs)

# Usage

## Expr
`Expr` is an Algebraic Data Type for SQL expressions, which is defined in `Expr.kt`

```kotlin
    SelectExpr<Person>(
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
```

## DSL
Helper functions and classes (Builder) to create Expr are defined in `DSLBuild.kt`. 

You can use the DSL to generate Expr objects.

The implementation of DslBuild is not good Kotlin code. It uses null frequently and has poor validation.

But it demonstrates how Kotlin features can be used to build a visually appealing (elegant) DSL.


If you don't want to use DISTINCT, you can simply omit it:

```kotlin
    Query(Person::class) {
        Select Distinct (Person::class)
        From (Person::class)
        Where {
            ((Person::name `==` "John") And
                    (Person::age gt 10) And
                    (Person::age lt 20)) Or
                    (Person::name `==` "Jane")
        }
    }
```

If you don't want to use DISTINCT, you can simply omit it:

```kotlin
    Query(Person::class) {
        Select (Person::class)
        From (Person::class)
        Where {
            ((Person::name `==` "John") And
                    (Person::age gt 10) And
                    (Person::age lt 20)) Or
                    (Person::name `==` "Jane")
        }
    }
```

How does it work?

When you use DISTINCT, the SELECT clause is a class, and it has a DISTINCT infix function. 

When you don't use DISTINCT, the SELECT clause is a function.


## Tests
Kotest is used to test.

The test code is located in the `test` directory and includes compile tests and DSL transformation tests to ensure that the DSL builder is correctly generate DSL Expr.

## Limitations
- Kotlin has some limitations in operator overloading.
- Kotlin does not support the use of the > or < characters in function names.
- Metaprogramming in Kotlin is difficult.
- The Kotlin style for Algebraic Data Types is verbose some functional programming languages.

## Interpreter

The KLOS implementation does not include an interpreter, and there are no plans to add one. The focus is on creating a DSL, not an interpreter.
