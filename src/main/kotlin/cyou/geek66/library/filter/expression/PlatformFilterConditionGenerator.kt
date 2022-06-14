package cyou.geek66.library.filter.expression

import cyou.geek66.library.filter.core.EntityFilters

interface PlatformFilterConditionGenerator<T : Any, C> {

	fun generate(entityKlass: Class<T>, filters: EntityFilters): C

}

inline fun <reified T : Any, C> PlatformFilterConditionGenerator<T, C>.generate(filters: EntityFilters): C =
	generate(T::class.java, filters)