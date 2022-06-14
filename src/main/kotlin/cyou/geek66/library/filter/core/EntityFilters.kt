package cyou.geek66.library.filter.core

data class EntityFilters(
	val filters: Set<EntityFilter> = emptySet(),
	val combineMode: CombineMode = CombineMode.AND
)