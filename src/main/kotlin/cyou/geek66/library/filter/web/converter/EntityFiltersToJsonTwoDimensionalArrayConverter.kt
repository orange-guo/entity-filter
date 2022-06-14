package cyou.geek66.library.filter.web.converter

import cyou.geek66.library.filter.core.EntityFilter

interface EntityFiltersToJsonTwoDimensionalArrayConverter {

	fun convertToEntityFilters(jsonTwoDimensionalArray: String): Set<EntityFilter>

	fun convertToJsonTwoDimensionalArray(filters: Set<EntityFilter>): String

}