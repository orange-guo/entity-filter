package cyou.geek66.library.filter.web.expression.mybatisplus

import arrow.core.curried
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.baomidou.mybatisplus.extension.activerecord.Model
import cyou.geek66.library.filter.core.CombineMode
import cyou.geek66.library.filter.core.EntityFilter
import cyou.geek66.library.filter.core.EntityFilterOperator
import cyou.geek66.library.filter.core.EntityFilters
import cyou.geek66.library.filter.expression.mybatisplus.MyBatisPlusQueryWrapperGenerator
import io.kotest.matchers.shouldBe
import javax.sql.DataSource
import kotlin.test.BeforeTest
import org.junit.jupiter.api.Test
import org.mybatis.spring.annotation.MapperScan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
internal class MyBatisPlusQueryWrapperGeneratorTest {

	@MapperScan
	@SpringBootApplication
	class TestApplication

	@TableName
	class User : Model<User>() {

		@TableId
		var id: Long? = null

		@TableField
		var name: String? = null

		@TableField
		var age: Int? = null

	}

	interface UserMapper : BaseMapper<User>

	@Autowired
	private lateinit var mapper: UserMapper

	@Autowired
	private lateinit var dataSource: DataSource

	@BeforeTest
	fun before() {
		dataSource.connection.createStatement().execute(
			"""
				CREATE TABLE IF NOT EXISTS USER (
					ID INT8 PRIMARY KEY,
					NAME VARCHAR(255),
					AGE INT8
				)
			"""
		)

		listOf(
			User().apply {
				name = "Jack"
				age = 32

			},
			User().apply {
				name = "Alice"
				age = 22
			},
			User().apply {
				name = "Andrew"
				age = 10
			}
		).forEach(mapper::insert)
	}

	val generate = (object : MyBatisPlusQueryWrapperGenerator<User> {})::generate.curried()(User::class.java)

	fun EntityFilters.selectList(): List<User> = generate(this).let(mapper::selectList)

	@Test
	fun empty() {
		EntityFilters()
			.selectList()
			.apply {
				size shouldBe 3
			}
	}

	@Test
	fun eq() {
		// (name eq Jack)
		EntityFilters(setOf(EntityFilter(path = "name", operator = EntityFilterOperator.EQ, value = "Jack")))
			.selectList()
			.apply {
				size shouldBe 1
				first().name shouldBe "Jack"
				first().age shouldBe 32
			}
	}

	@Test
	fun like() {
		// (name like %J%)
		EntityFilters(setOf(EntityFilter(path = "name", operator = EntityFilterOperator.LIKE, value = "%J%")))
			.selectList()
			.first()
			.apply {
				name shouldBe "Jack"
			}
	}

	@Test
	fun ilike() {
		// (lower(name) like a)
		EntityFilters(setOf(EntityFilter(path = "name", operator = EntityFilterOperator.ILIKE, value = "a")))
			.selectList()
			.apply {
				size shouldBe 3
			}
	}

	@Test
	fun gtAndLike() {
		// (age gt 20) and (name like %J%)
		EntityFilters(
			filters = setOf(
				EntityFilter(
					"age",
					EntityFilterOperator.GT,
					20
				),
				EntityFilter(
					path = "name",
					operator = EntityFilterOperator.LIKE,
					value = "%J%"
				)
			),
			combineMode = CombineMode.AND
		).selectList().apply {
			size shouldBe 1
		}
	}

	@Test
	fun gtOrLike() {
		// (age gt 20) or (name like %J%)
		EntityFilters(
			filters = setOf(
				EntityFilter(
					"age",
					EntityFilterOperator.GT,
					20
				),
				EntityFilter(
					path = "name",
					operator = EntityFilterOperator.LIKE,
					value = "%J%"
				)
			),
			combineMode = CombineMode.OR
		).selectList().apply {
			size shouldBe 2
		}
	}

	@Test
	fun gtAndIlike() {
		EntityFilters(
			setOf(
				EntityFilter(path = "age", operator = EntityFilterOperator.GT, value = 20),
				EntityFilter(path = "name", operator = EntityFilterOperator.ILIKE, value = "a")
			)
		).selectList().apply {
			size shouldBe 2
		}
	}

}