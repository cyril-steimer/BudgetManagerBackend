package ch.cyril.budget.manager.backend.util

interface Identifiable {
    val identifier: String

    companion object {
        inline fun <reified T> byIdentifier(identifier: String): T
                where T : Enum<T>, T : Identifiable {

            val enumType = T::class.java
            val values = enumType.enumConstants
            for (value in values) {
                if ((value as Identifiable).identifier == identifier) {
                    return value
                }
            }
            throw IllegalArgumentException("No enum constant with identifier '$identifier'")
        }

        inline fun <reified T> byIdentifierOrDefault(identifier: String, dflt: T): T
                where T: Enum<T>, T: Identifiable {

            try {
                return byIdentifier(identifier)
            } catch (e: Exception) {
                return dflt
            }
        }
    }
}