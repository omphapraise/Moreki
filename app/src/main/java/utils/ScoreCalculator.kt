package com.cubiccode.moreki.utils

import com.cubiccode.moreki.models.Expense

object ScoreCalculator {

    fun calculateScore(expenses: List<Expense>, monthlyBudget: Double): Int {
        if (expenses.isEmpty()) return 100 // no spending yet = perfect score

        val safeBudget = if (monthlyBudget > 0) monthlyBudget else 5000.0
        val total = expenses.sumOf { it.amount }
        val spendRatio = (total / safeBudget).coerceIn(0.0, 2.0)

        // Deduct up to 70 points based on how much of the budget was used
        val spendPenalty = (spendRatio * 70).coerceAtMost(70.0)

        // Bonus up to 10 points for category diversity (spreading spend around)
        val categoryCount = expenses.map { it.category }.distinct().size
        val diversityBonus = (categoryCount * 2).coerceAtMost(10)

        val score = (100 - spendPenalty + diversityBonus).coerceIn(0.0, 100.0)
        return score.toInt()
    }
}