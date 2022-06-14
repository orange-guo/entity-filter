package cyou.geek66.library.filter.core

data class EntityFilter(
	val path: String,
	val operator: EntityFilterOperator,
	val value: Any?,
)