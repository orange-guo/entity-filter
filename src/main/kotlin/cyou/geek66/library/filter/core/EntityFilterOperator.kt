package cyou.geek66.library.filter.core

enum class EntityFilterOperator {
	EQ, NE,
	GT, GOE,
	LT, LOE,
	IS_NULL, IS_NOT_NULL,
	LIKE, ILIKE,
	IN, NOT_IN,
	CONTAINS;

	companion object {

		private val aliasMap: Map<String, EntityFilterOperator> =
			aliasMap(GT, ">") +
					aliasMap(GOE, ">=", "GE") +
					aliasMap(LT, "<") +
					aliasMap(LOE, "<=", "LE") +
					aliasMap(EQ, "=") +
					aliasMap(NE, "!=") +
					aliasMap(IN) +
					aliasMap(LIKE) +
					aliasMap(ILIKE) +
					aliasMap(CONTAINS, "HAS") +
					aliasMap(NOT_IN, "!IN")

		private fun aliasMap(operator: EntityFilterOperator, vararg alias: String): Map<String, EntityFilterOperator> =
			mapOf(operator.name to operator) + alias.associateWith { operator }

		operator fun get(alias: String): EntityFilterOperator? = aliasMap[alias.uppercase()]

	}

}