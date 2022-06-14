package cyou.geek66.library.filter.expression.querysdl

import com.querydsl.core.types.Ops
import cyou.geek66.library.filter.core.EntityFilterOperator

fun EntityFilterOperator.asOps(): Ops =
	when (this) {
		EntityFilterOperator.ILIKE -> Ops.LIKE_IC
		EntityFilterOperator.CONTAINS -> Ops.IN
		else -> Ops.valueOf(this.name)
	}