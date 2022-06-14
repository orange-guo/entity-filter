package cyou.geek66.library.filter.web.expression.querysdl.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor

interface UserRepository : JpaRepository<User, Long>, QuerydslPredicateExecutor<User>