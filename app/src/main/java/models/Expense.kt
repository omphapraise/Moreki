package com.cubiccode.moreki.models

data class Expense(
    val amount: Double,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)