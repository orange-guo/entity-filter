package cyou.geek66.library.filter.web.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import cyou.geek66.library.filter.core.EntityFilter
import cyou.geek66.library.filter.core.EntityFilterOperator
import cyou.geek66.library.filter.web.normalizer.EntityFilterNormalizer

class DefaultEntityFiltersToJsonTwoDimensionalArrayConverter(
	private val mapper: ObjectMapper,
	private val normalizers: List<EntityFilterNormalizer>
) : EntityFiltersToJsonTwoDimensionalArrayConverter {

	override fun convertToEntityFilters(jsonTwoDimensionalArray: String): Set<EntityFilter> =
		runCatching {
			mapper.readValue(jsonTwoDimensionalArray, object : TypeReference<List<List<Any>>>() {})
				.map {
					EntityFilter(
						path = it[0].toString(),
						operator = parseOperator(it[1].toString()),
						value = if (it.size > 2) it[2] else null
					)
				}
		}.getOrElse {
			throw EntityFiltersConversionException(
				"Failed to read from given json two dimensional array $jsonTwoDimensionalArray",
				it
			)
		}.map { normalizers.foldRight(it, EntityFilterNormalizer::normalize) }.toSet()

	private fun parseOperator(opsStr: String): EntityFilterOperator =
		EntityFilterOperator[opsStr] ?: throw EntityFiltersConversionException("The given filter operator $opsStr is not support")

	override fun convertToJsonTwoDimensionalArray(filters: Set<EntityFilter>): String =
		runCatching {
			filters.map { filter -> arrayOf(filter.path, filter.operator, filter.value) }
				.let(mapper::writeValueAsString)
		}.getOrElse {
			throw EntityFiltersConversionException("Failed to write filters $filters as json two dimensional array")
		}

}