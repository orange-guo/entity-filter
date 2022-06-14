package cyou.geek66.library.filter.spring

import com.fasterxml.jackson.databind.ObjectMapper
import cyou.geek66.library.filter.core.EntityFilterOperator
import cyou.geek66.library.filter.web.converter.DefaultEntityFiltersToJsonTwoDimensionalArrayConverter
import cyou.geek66.library.filter.web.converter.EntityFiltersToJsonTwoDimensionalArrayConverter
import cyou.geek66.library.filter.web.normalizer.EntityFilterNormalizer
import cyou.geek66.library.filter.web.resolver.EntityFiltersMappingHandlerMethodArgumentResolver
import org.springframework.context.annotation.Bean
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

class SpringAutoConfiguration {

	@Bean
	fun defaultEntityFiltersToJsonTwoDimensionalArrayConverter(mapper: ObjectMapper, normalizers: List<EntityFilterNormalizer>): EntityFiltersToJsonTwoDimensionalArrayConverter =
		DefaultEntityFiltersToJsonTwoDimensionalArrayConverter(mapper, normalizers)

	@Bean
	fun entityFiltersMappingHandlerMethodArgumentResolver(converter: EntityFiltersToJsonTwoDimensionalArrayConverter) =
		EntityFiltersMappingHandlerMethodArgumentResolver(converter)

	@Bean
	fun myConfigurer(resolver: EntityFiltersMappingHandlerMethodArgumentResolver): WebMvcConfigurer =
		object : WebMvcConfigurer {
			override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
				resolvers.add(resolver)
			}
		}

	@Bean
	fun normalizeNullOperator(): EntityFilterNormalizer =
		EntityFilterNormalizer { filter ->
			when (filter.value) {
				null -> filter.copy(
					operator = when (filter.operator) {
						EntityFilterOperator.EQ -> EntityFilterOperator.IS_NULL
						EntityFilterOperator.NE -> EntityFilterOperator.IS_NOT_NULL
						else -> filter.operator
					}
				)
				else -> filter
			}
		}

}