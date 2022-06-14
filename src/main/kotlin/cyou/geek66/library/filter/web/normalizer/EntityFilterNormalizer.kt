package cyou.geek66.library.filter.web.normalizer

import cyou.geek66.library.filter.core.EntityFilter

fun interface EntityFilterNormalizer {

	fun normalize(filter: EntityFilter): EntityFilter

}