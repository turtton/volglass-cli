package io

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpStatement
import io.response.ReleaseData

interface GithubApi {
    @Headers(USER_AGENT, "Accept: application/vnd.github.v3+json")
    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(@Path("owner") owner: String, @Path("repo") repo: String): ReleaseData

    @Headers(USER_AGENT)
    @GET("repos/{owner}/{repo}/zipball/{tag}")
    suspend fun downloadZipBall(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("tag") tag: String,
    ): HttpStatement

    companion object {
        const val USER_AGENT = "User-Agent: Volglass-Cli"
    }
}
