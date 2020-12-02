package id.forum.core.data

import com.google.android.gms.tasks.Task
import id.forum.core.user.domain.model.User

data class UserData(
//    val stitch: Task<StitchUser>?,
    val user: User = User(),
    val token: Token = Token()
)