package id.forum.core.network

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.cache.normalized.CacheKeyResolver
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory
import com.google.firebase.iid.FirebaseInstanceId
import id.forum.core.data.Token
import id.forum.core.type.CustomType
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun getRealmClient(): App = App(AppConfiguration.Builder("gowesforumandroid-klwks").build())

fun getFileApolloCache(context: Context) =
    File(context.applicationContext.filesDir, "apolloCache")

fun getHttpClient(token: Token, context: Context) =
    OkHttpClient.Builder()
        .authenticator(AccessTokenAuthenticator(token, context))
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder().method(
                original.method,
                original.body
            )
            builder.header("Authorization", "Bearer " + token.accessToken)
            chain.proceed(builder.build())
        }
        .build()

fun getApolloClientBuilder(
    context: Context
): ApolloClient.Builder {
    return ApolloClient.builder()
        .serverUrl(SERVER_URL)
        .normalizedCache(
            LruNormalizedCacheFactory(
//                EvictionPolicy.NO_EVICTION
                EvictionPolicy.builder().maxSizeBytes(10 * 1024 * 1024).build()
            )
                .chain(SqlNormalizedCacheFactory(context, "apollo.db"))
        )
        .addCustomTypeAdapter(CustomType.OBJECTID, objectIdTypeAdapter)
        .addCustomTypeAdapter(CustomType.DATETIME, dateTimeTypeAdapter)
}

//val resolver: CacheKeyResolver = object: CacheKeyResolver() {
//    override fun fromFieldArguments(
//        field: ResponseField,
//        variables: Operation.Variables
//    ): CacheKey {
//        return CacheKey.from(field.resolveArgument("_id", variables).toString())
//    }
//
//    override fun fromFieldRecordSet(field: ResponseField, recordSet: Map<String, Any>): CacheKey {
//        return CacheKey.from(recordSet["_id"].toString())
//    }
//}

fun getApolloClient(
    httpClient: OkHttpClient,
    apolloClientBuilder: ApolloClient.Builder
): ApolloClient = apolloClientBuilder.okHttpClient(httpClient).build()

private const val SERVER_URL =
    "https://realm.mongodb.com/api/client/v2.0/app/gowesforumandroid-klwks/graphql"
private val objectIdTypeAdapter = object : CustomTypeAdapter<String> {
    override fun encode(value: String) = CustomTypeValue.GraphQLString(value)
    override fun decode(value: CustomTypeValue<*>) = value.value.toString()
}

private val dateTimeTypeAdapter = object : CustomTypeAdapter<Date> {
    override fun encode(value: Date) =
        CustomTypeValue.GraphQLString(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(value))

    override fun decode(value: CustomTypeValue<*>) =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(value.value.toString())
}


private const val baseUrl =
    "https://stitch.mongodb.com/api/client/v2.0/app/gowesforumandroid-klwks/auth/providers/local-userpass/login"

fun getTokenStitchRequest(email: String, password: String): Call {
    val request = Request.Builder()
        .url(baseUrl)
        .post(
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", email)
                .addFormDataPart("password", password)
                .build()
        )
        .build()

    return OkHttpClient().newCall(request)
}

fun getFirebaseAccessToken() = FirebaseInstanceId.getInstance().token.toString()