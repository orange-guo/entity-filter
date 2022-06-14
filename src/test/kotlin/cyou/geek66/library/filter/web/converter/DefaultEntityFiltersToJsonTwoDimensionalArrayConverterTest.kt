package cyou.geek66.library.filter.web.converter

import cyou.geek66.library.filter.core.EntityFilter
import cyou.geek66.library.filter.core.EntityFilterOperator
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class DefaultEntityFiltersToJsonTwoDimensionalArrayConverterTest {

	@SpringBootApplication
	class TestApplication

	@Autowired
	private lateinit var converter: EntityFiltersToJsonTwoDimensionalArrayConverter

	@Test
	fun convertToEntityFilters() {
		converter.convertToEntityFilters(
			"""
			|[
			|	["name", "like", "j"],
			|	["age", ">", 30],
			|	["money", "ne", null]
			|]
			""".trimMargin()
		).toList().apply {
			size shouldBe 3
			this[0].apply {
				path shouldBe "name"
				operator shouldBe EntityFilterOperator.LIKE
				value shouldBe "j"
			}
			this[1].apply {
				path shouldBe "age"
				operator shouldBe EntityFilterOperator.GT
				value shouldBe 30
			}
			this[2].apply {
				path shouldBe "money"
				operator shouldBe EntityFilterOperator.IS_NOT_NULL
			}
		}
	}

	@Test
	fun convertToJsonTwoDimensionalArray() {
		converter.convertToJsonTwoDimensionalArray(
			setOf(
				EntityFilter(
					path = "age",
					operator = EntityFilterOperator.GT,
					value = 2
				)
			)
		) shouldBe """[["age","GT",2]]"""
	}

}