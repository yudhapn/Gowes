package id.forum.core.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import id.forum.core.data.Token
import id.forum.core.util.SHARED_TKN
import com.google.gson.Gson
import okhttp3.*
import java.net.HttpURLConnection

class AccessTokenAuthenticator(private var oldToken: Token, private val context: Context) :
    Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val prefEditor = context.getSharedPreferences(SHARED_TKN, MODE_PRIVATE).edit()
        var newToken = Token()
        synchronized(this) {
            if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                Log.d("AccessTokenAuth", "token refreshing")
                val refreshResponse = OkHttpClient().newCall(refreshToken()).execute()
                newToken = Gson().fromJson(refreshResponse.body?.string(), Token::class.java)
            }
            return if (oldToken.accessToken != newToken.accessToken) {
                oldToken.accessToken = newToken.accessToken
                Log.d("AccessTokenAuth", "new access token: ${oldToken.accessToken}")
                val tokenJson = Gson().toJson(oldToken)
                prefEditor.putString(SHARED_TKN, tokenJson)
                prefEditor.apply()
                createResponse(response.request, oldToken.accessToken)
            } else {
                return null
            }
        }
    }

    private fun refreshToken() = Request.Builder()
        .url("https://stitch.mongodb.com/api/client/v2.0/auth/session")
        .header("Authorization", "Bearer " + oldToken.refreshToken)
        .post(
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "test").build()
        ).build()

    private fun createResponse(request: Request, token: String) = request.newBuilder()
        .header("Authorization", "Bearer $token").build()
}