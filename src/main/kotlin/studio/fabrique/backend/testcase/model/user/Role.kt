package studio.fabrique.backend.testcase.model.user

import studio.fabrique.backend.testcase.model.user.Authority.*

enum class Role(vararg authorities: Authority) {
    ADMIN(*Authority.values()),
    ANONYMOUS(WATCH_SURVEYS, COMPLETE_SURVEYS, WATCH_COMPLETED_SURVEYS),
    ;

    val authorities: Set<Authority> = authorities.toSet()
}