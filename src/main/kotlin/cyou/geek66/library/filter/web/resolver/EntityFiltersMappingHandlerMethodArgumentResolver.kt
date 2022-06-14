package cyou.geek66.library.filter.web.resolver

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.or
import arrow.core.toOption
import cyou.geek66.library.filter.core.EntityFilters
import cyou.geek66.library.filter.web.EntityFiltersMapping
import cyou.geek66.library.filter.web.converter.EntityFiltersToJsonTwoDimensionalArrayConverter
import org.springframework.core.MethodParameter
import org.springframework.lang.NonNull
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class EntityFiltersMappingHandlerMethodArgumentResolver(
	private val converter: EntityFiltersToJsonTwoDimensionalArrayConverter
) : HandlerMethodArgumentResolver {

	override fun supportsParameter(@NonNull parameter: MethodParameter): Boolean =
		parameter
			.takeIf { it.parameter.getAnnotation(EntityFiltersMapping::class.java) != null }
			?.parameterType
			?.let { EntityFilters::class != it }
			?: false

	override fun resolveArgument(
		@NonNull parameter: MethodParameter,
		mavContainer: ModelAndViewContainer?,
		@NonNull webRequest: NativeWebRequest,
		binderFactory: WebDataBinderFactory?
	): EntityFilters {
		val mapping = parameter.parameter.getAnnotation(EntityFiltersMapping::class.java)

		val filters = getRequestParameterAndMapIt(
			request = webRequest,
			parameterName = mapping.filtersName,
			defaultParameterName = "filters",
			mapRequestParameter = converter::convertToEntityFilters,
			defaultMapResult = ::emptySet
		).also { filters ->
			if (filters.isEmpty() && mapping.filtersIsRequired)
				throw EntityFiltersResolvingException("filters is required")
		}

		val combineMode = getRequestParameterAndMapIt(
			request = webRequest,
			parameterName = mapping.combineModeName,
			defaultParameterName = "combineMode",
			mapRequestParameter = { enumValueOf(it) },
			defaultMapResult = mapping::defaultCombineMode
		)

		return EntityFilters(
			filters = filters,
			combineMode = combineMode
		)
	}

	private fun <T> getRequestParameterAndMapIt(
		request: NativeWebRequest,
		parameterName: String,
		defaultParameterName: String,
		mapRequestParameter: (String) -> T,
		defaultMapResult: () -> T
	): T =
		Option.fromNullable(parameterName)
			.filter { it.isNotBlank() }
			.or(defaultParameterName.toOption())
			.flatMap { request.getParameter(it).toOption() }
			.filter { it.isNotBlank() }
			.map(mapRequestParameter)
			.getOrElse(defaultMapResult)

}