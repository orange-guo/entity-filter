package cyou.geek66.library.filter.web.expression.querysdl.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	var id: Long? = null

	var name: String? = null

	@OneToMany
	var users: List<User>? = null

}