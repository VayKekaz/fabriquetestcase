package studio.fabrique.backend.testcase.model.user

import org.springframework.security.core.GrantedAuthority

enum class Authority : GrantedAuthority {
    EDIT_SURVEYS,
    WATCH_SURVEYS,
    WATCH_COMPLETED_SURVEYS,
    COMPLETE_SURVEYS,
    ;

    override fun getAuthority() = this.toString()

    /**
     * Used as annotation arguments.
     */
    companion object {
        const val EDIT_SURVEYS_AUTHORITY = "EDIT_SURVEYS"
        const val WATCH_SURVEYS_AUTHORITY = "WATCH_SURVEYS"
        const val WATCH_COMPLETED_SURVEYS_AUTHORITY = "WATCH_COMPLETED_SURVEYS"
        const val COMPLETE_SURVEYS_AUTHORITY = "COMPLETE_SURVEYS"
    }
}
