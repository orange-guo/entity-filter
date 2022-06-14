package cyou.geek66.library.filter.web.expression.querysdl

import arrow.core.curried
import cyou.geek66.library.filter.core.CombineMode
import cyou.geek66.library.filter.core.EntityFilter
import cyou.geek66.library.filter.core.EntityFilterOperator
import cyou.geek66.library.filter.core.EntityFilters
import cyou.geek66.library.filter.expression.querysdl.QuerydslBooleanExpressionGenerator
import cyou.geek66.library.filter.web.expression.querysdl.entity.Country
import cyou.geek66.library.filter.web.expression.querysdl.entity.Job
import cyou.geek66.library.filter.web.expression.querysdl.entity.User
import cyou.geek66.library.filter.web.expression.querysdl.entity.UserRepository
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest(properties = ["spring.jpa.show-sql=true"])
internal class QuerydslBooleanExpressionGeneratorTest {

	@SpringBootApplication
	class TestApplication

	@Autowired
	private lateinit var repository: UserRepository

	val generate = (object : QuerydslBooleanExpressionGenerator<User> {})::generate.curried()(User::class.java)

	fun EntityFilters.findAll() = generate(this).let(repository::findAll).toList()

	@BeforeTest
	fun before() {
		listOf(
			User().apply {
				name = "Jack"
				age = 30
				country = Country.CN
			},
			User().apply {
				name = "Ben"
				age = 22
				country = Country.JP
			}
		).forEach(repository::save)
	}

	@Test
	fun like() {
		EntityFilters(setOf(EntityFilter(path = "name", operator = EntityFilterOperator.LIKE, value = "%J%")))
			.findAll()
			.apply {
				size shouldBe 1
			}
	}

	@Test
	fun ilike() {
		EntityFilters(setOf(EntityFilter(path = "name", operator = EntityFilterOperator.ILIKE, value = "%j%")))
			.findAll()
			.apply {
				size shouldBe 1
			}
	}

	@Test
	fun gt() {
		EntityFilters(setOf(EntityFilter(path = "id", operator = EntityFilterOperator.GT, value = 0)))
			.findAll()
			.apply {
				size shouldBe 2
			}
	}

	@Test
	fun eq() {
		EntityFilters(setOf(EntityFilter(path = "country", operator = EntityFilterOperator.EQ, value = "CN")))
			.findAll()
			.apply {
				size shouldBe 1
			}
	}

	@Test
	fun `in`() {
		EntityFilters(
			setOf(
				EntityFilter(
					path = "country",
					operator = EntityFilterOperator.IN,
					value = listOf(Country.CN)
				)
			)
		).findAll()
			.apply {
				size shouldBe 1
			}
	}

	@Test
	fun notIn() {
		EntityFilters(
			setOf(
				EntityFilter(
					path = "country",
					operator = EntityFilterOperator.NOT_IN,
					value = listOf(Country.CN)
				)
			)
		).findAll()
			.apply {
				size shouldBe 1
			}
	}

	@Test
	fun cascade() {
		User().also { user ->
			user.job = Job().apply {
				name = "JAVA"
				users = listOf(user)
			}
		}.let(repository::save)

		generate(
			EntityFilters(
				filters = setOf(
					EntityFilter(
						"job.name",
						EntityFilterOperator.EQ,
						"JAVA"
					)
				),
				combineMode = CombineMode.AND
			)
		).let(repository::findAll).toList().apply {
			size shouldBe 1
		}
	}

	@Test
	fun testContains() {
		User().apply {
			name = "123"
			labels = listOf("0", "2", "3")
		}.let(repository::save)
		User().apply {
			name = "456"
			labels = listOf("0", "5", "6")
		}.let(repository::save)
		generate(
			EntityFilters(
				filters = setOf(
					EntityFilter(
						path = "labels",
						operator = EntityFilterOperator.CONTAINS,
						value = "0"
					)
				),
				combineMode = CombineMode.AND
			)
		).let(repository::findAll).toList().apply {
			size shouldBe 2
		}

		generate(
			EntityFilters(
				filters = setOf(
					EntityFilter(
						path = "labels",
						operator = EntityFilterOperator.CONTAINS,
						value = "2"
					)
				),
				combineMode = CombineMode.AND
			)
		).let(repository::findAll).toList().apply {
			size shouldBe 1
		}
		generate(
			EntityFilters(
				filters = setOf(
					EntityFilter(
						path = "labels",
						operator = EntityFilterOperator.CONTAINS,
						value = "8"
					)
				),
				combineMode = CombineMode.AND
			)
		).let(repository::findAll).toList().apply {
			size shouldBe 0
		}
	}

}