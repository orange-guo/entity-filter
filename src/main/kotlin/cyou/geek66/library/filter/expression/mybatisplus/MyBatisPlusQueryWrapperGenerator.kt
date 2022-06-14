package cyou.geek66.library.filter.expression.mybatisplus

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import cyou.geek66.library.filter.core.CombineMode
import cyou.geek66.library.filter.core.EntityFilter
import cyou.geek66.library.filter.core.EntityFilterOperator
import cyou.geek66.library.filter.core.EntityFilters
import cyou.geek66.library.filter.expression.FilterConditionGenerationException
import cyou.geek66.library.filter.expression.PlatformFilterConditionGenerator
import java.util.function.Consumer

interface MyBatisPlusQueryWrapperGenerator<T : Any> :
	PlatformFilterConditionGenerator<T, QueryWrapper<T>> {

	override fun generate(entityKlass: Class<T>, filters: EntityFilters): QueryWrapper<T> =
		filters
			.filters
			.map { mapConsumer<T>(it) }
			.fold(QueryWrapper<T>().setEntityClass(entityKlass)) { a, b ->
				when (filters.combineMode) {
					CombineMode.AND -> a.and(b)
					CombineMode.OR -> a.or(b)
				}
			}

	fun <T> mapConsumer(filter: EntityFilter): Consumer<QueryWrapper<T>> =
		Consumer { wrapper ->
			when (filter.operator) {
				EntityFilterOperator.EQ -> wrapper.eq(filter.path, filter.value)
				EntityFilterOperator.NE -> wrapper.ne(filter.path, filter.value)
				EntityFilterOperator.GT -> wrapper.gt(filter.path, filter.value)
				EntityFilterOperator.GOE -> wrapper.ge(filter.path, filter.value)
				EntityFilterOperator.LT -> wrapper.lt(filter.path, filter.value)
				EntityFilterOperator.LOE -> wrapper.le(filter.path, filter.value)
				EntityFilterOperator.IS_NULL -> wrapper.isNull(filter.path)
				EntityFilterOperator.IS_NOT_NULL -> wrapper.isNotNull(filter.path)
				EntityFilterOperator.LIKE -> wrapper.like(filter.path, filter.value)
				EntityFilterOperator.IN -> wrapper.`in`(filter.path, filter.value)
				EntityFilterOperator.ILIKE -> wrapper.like("LOWER(${filter.path})", filter.value.toString().lowercase())
				EntityFilterOperator.NOT_IN -> wrapper.notIn(filter.path, filter.value)
				EntityFilterOperator.CONTAINS -> throw FilterConditionGenerationException("CONTAINS is not supported.")
			}
		}

}