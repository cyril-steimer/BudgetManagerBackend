package ch.cyril.budget.manager.backend.model

interface Validatable {

    @Throws(ValidationException::class)
    fun validate(): Unit
}

class ValidationException(message: String?) : Exception(message)