import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenLibraryService {
    @GET("isbn/{isbn}.json")
    suspend fun getBookByISBN(@Path("isbn") isbn: String): Response<OpenLibraryResponse>
}