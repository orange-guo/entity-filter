package cyou.geek66.library.filter.expression.querysdl

import arrow.core.curried
import com.querydsl.core.types.Constant
import com.querydsl.core.types.ConstantImpl
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Ops
import com.querydsl.core.types.Path
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CollectionPath
import com.querydsl.core.types.dsl.CollectionPathBase
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.Expressions
import cyou.geek66.library.filter.core.CombineMode
import cyou.geek66.library.filter.core.EntityFilter
import cyou.geek66.library.filter.core.EntityFilterOperator
import cyou.geek66.library.filter.core.EntityFilters
import cyou.geek66.library.filter.expression.FilterConditionGenerationException
import cyou.geek66.library.filter.expression.PlatformFilterConditionGenerator
import java.lang.reflect.Modifier
import java.time.ZonedDateTime
import org.springframework.core.convert.support.DefaultConversionService

interface QuerydslBooleanExpressionGenerator<T : Any> : PlatformFilterConditionGenerator<T, BooleanExpression> {

	override fun generate(entityKlass: Class<T>, filters: EntityFilters): BooleanExpression =
		filters
			.filters
			.map(this::doGenerateExpression.curried()(getEntityInstance(entityKlass)))
			.reduceOrNull(if (filters.combineMode == CombineMode.AND) BooleanExpression::and else BooleanExpression::or)
			?: Expressions.booleanOperation(Ops.IS_NOT_NULL, ConstantImpl.create(1))

	private fun getEntityInstance(entityClass: Class<*>): EntityPathBase<*> =
		entityClass.run { `package`.name + "." + simpleName.let { "Q$it" } }
			.let { Class.forName(it) }
			.let { klass ->
				klass.declaredFields
					.filter { Modifier.isStatic(it.modifiers) && it.type == klass }
					.map { it[klass] }
					.map(EntityPathBase::class.java::cast)
					.first()
			}

	private fun doGenerateExpression(rootEntity: EntityPathBase<*>, filter: EntityFilter): BooleanExpression =
		filter
			.path
			.split(".")
			.toList()
			.fold(pathMustBeRoot(rootEntity), this::getSubPath)
			.let { newExpression(it, filter) }

	private fun pathMustBeRoot(entityPath: Path<*>): Path<*> =
		when (entityPath.root) {
			entityPath -> entityPath
			else -> throw FilterConditionGenerationException("Path $entityPath is not root")
		}

	private fun newExpression(target: Path<*>, filter: EntityFilter) =
		Expressions::booleanOperation.curried()(filter.operator.asOps())(
			when (filter.operator) {
				EntityFilterOperator.IS_NULL, EntityFilterOperator.IS_NOT_NULL -> listOf<Expression<*>>()
				EntityFilterOperator.CONTAINS -> listOf(convertToConstant(filter.value!!, target), target)
				else -> listOf(target, convertToConstant(filter.value!!, target))
			}.toTypedArray()
		)

	private fun getSubPath(parentEntity: Path<*>, propertyName: String): Path<*> =
		parentEntity.javaClass.let { parentEntityClass ->
			try {
				parentEntityClass.getField(propertyName)[parentEntity] as Path<*>
			} catch (ex: Exception) {
				throw FilterConditionGenerationException(
					when (ex) {
						is NoSuchFieldException -> "No such property $propertyName in class $parentEntityClass"
						is IllegalAccessException -> "Private property $propertyName in class $parentEntityClass"
						is ClassCastException -> "Wrong property $propertyName in class $parentEntityClass, It must be instance of Path"
						else -> "Unknown exception"
					},
					ex
				)
			}
		}

	private fun convertToConstant(rawValue: Any, target: Path<*>): Constant<Any> {
		fun doConvert(rawValue: Any, targetType: Class<*>) =
			if (targetType.isInstance(rawValue)) rawValue
			else when (targetType) {
				ZonedDateTime::class.java -> ZonedDateTime.parse(rawValue.toString())
				else -> DefaultConversionService.getSharedInstance().convert(rawValue, targetType)!!
			}

		return when {
			rawValue is Collection<*> && target !is CollectionPath<*, *> -> rawValue.filterNotNull().map { doConvert(it, target.type) } // in
			target is CollectionPathBase<*, *, *> && rawValue !is Collection<*> -> doConvert(rawValue, target.elementType as Class<*>) // contains
			else -> doConvert(rawValue, target.type)
		}.let { ConstantImpl.create(it) }
	}

}