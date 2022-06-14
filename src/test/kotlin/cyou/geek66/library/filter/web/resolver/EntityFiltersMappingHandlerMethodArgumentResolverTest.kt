package cyou.geek66.library.filter.web.resolver

import com.fasterxml.jackson.databind.ObjectMapper
import cyou.geek66.library.filter.core.CombineMode
import cyou.geek66.library.filter.core.EntityFilterOperator
import cyou.geek66.library.filter.core.EntityFilters
import cyou.geek66.library.filter.web.EntityFiltersMapping
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class EntityFiltersMappingHandlerMethodArgumentResolverTest {

	@LocalServerPort
	private var serverPort: Int = -1

	@Autowired
	private lateinit var builder: RestTemplateBuilder

	@Autowired
	private lateinit var mapper: ObjectMapper

	private lateinit var restTemplate: RestTemplate

	@BeforeEach
	fun before() {
		restTemplate = builder.build()
	}

	@RestController
	@SpringBootApplication
	class HelloApplication {

		@GetMapping("/filter")
		fun getHelloByToken(
			@EntityFiltersMapping filters: EntityFilters
		) {
			filters.filters.first().apply {
				path shouldBe "name"
				operator shouldBe EntityFilterOperator.EQ
				value shouldBe "2"
			}
			filters.combineMode shouldBe CombineMode.OR
		}

	}

	@Test
	fun test() {
		restTemplate.getForObject(
			"http://localhost:$serverPort/filter?combineMode=OR&filters=" + mapper.writeValueAsString(
				listOf(listOf("name", "EQ", "2"))
			),
			String::class.java
		)
	}

}