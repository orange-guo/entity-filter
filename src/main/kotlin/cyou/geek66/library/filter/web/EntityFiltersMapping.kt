package cyou.geek66.library.filter.web

import cyou.geek66.library.filter.core.CombineMode

@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class EntityFiltersMapping(

	val filtersName: String = "",

	val filtersIsRequired: Boolean = false,

	val combineModeName: String = "",

	val defaultCombineMode: CombineMode = CombineMode.AND

)