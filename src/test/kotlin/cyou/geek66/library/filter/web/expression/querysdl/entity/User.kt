package cyou.geek66.library.filter.web.expression.querysdl.entity

import cyou.geek66.library.filter.web.expression.querysdl.entity.Country
import cyou.geek66.library.filter.web.expression.querysdl.entity.Job
import javax.persistence.CascadeType
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	var id: Long? = null

	@ManyToOne(cascade = [CascadeType.ALL])
	var job: Job? = null

	var name: String? = null

	var age: Int? = null

	@Enumerated(EnumType.STRING)
	var country: Country? = null

	@ElementCollection
	var labels: List<String>? = null

}