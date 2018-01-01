package ch.cyril.budget.manager.backend.service.filebased.expense

import ch.cyril.budget.manager.backend.model.*
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate

class ExpenseParser() {

    fun load(file: Path): List<Expense> {
        val lines = Files.readAllLines(file)
        return lines
                .map { l -> lineToExpense(l) }
    }

    fun store(file: Path, expenses: List<Expense>) {
        val lines = expenses
                .map { e -> expenseToLine(e) }
        Files.write(file, lines)
    }

    private fun expenseToLine(expense: Expense): String {
        return listOf(
                expense.id.id,
                expense.name.name,
                expense.amount,
                expense.category.name,
                expense.date)
                .joinToString(",")
    }

    private fun lineToExpense(line: String): Expense {
        val split = line.split(",")
        val id = Id(split[0].toInt())
        val name = Name(split[1])
        val amount = Amount(split[2].toBigDecimal())
        val category = Category(split[3])
        val date = LocalDate.parse(split[4])
        return Expense(id, name, amount, category, date)
    }
}