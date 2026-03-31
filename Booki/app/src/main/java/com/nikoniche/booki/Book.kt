//package com.nikoniche.booki
//
//import android.net.Uri
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.platform.LocalContext
//import coil.compose.rememberAsyncImagePainter
//import coil.request.ImageRequest
//import com.nikoniche.booki.architecture.book_details_views.createImageLoader
//import com.google.gson.Gson
//import com.google.firebase.database.Exclude
//import com.google.firebase.database.IgnoreExtraProperties
//
//@IgnoreExtraProperties
//class Book(
//    var id: Long = -1L,
//    var title: String = "",
//    var authors: List<String> = emptyList(),
//    var numberOfPages: Int = 0,
//    var publishDate: String = "",
//    var isbn10: String = "",
//    var isbn13: String = "",
//    var coverUrl: String = "",
//    var subtitle: String = "",
//    var description: String = "",
//    var publisher: String = "",
//    var genres: List<String> = emptyList(),
//    var language: String = "",
//    var source: String = "" // "OpenLibrary" hoặc "User"
//) {
//    // Constructor mặc định bắt buộc cho Firebase
//    constructor() : this(-1L, "", emptyList(), 0)
//
//    // Lấy ISBN khả dụng (Firebase sẽ bỏ qua thuộc tính này)
//    @get:Exclude
//    val displayISBN: String
//        get() = if (isbn10.isNotEmpty()) isbn10 else if (isbn13.isNotEmpty()) isbn13 else ""
//
//    // Chuyển đối tượng sang JSON (Firebase sẽ bỏ qua hàm này)
//    @Exclude
//    fun textify(): String = Gson().toJson(this)
//
//    // Chuyển List Authors thành String để hiển thị (Sửa lỗi Conflicting Getters)
//    @get:Exclude
//    val authorsText: String
//        get() {
//            if (authors.isEmpty()) return "Unknown Author"
//            return authors.joinToString(", ")
//        }
//
//    @Exclude
//    @Composable
//    fun getCoverPainter(): Painter {
//        val context = LocalContext.current
//        val imageLoader = createImageLoader(context)
//
//        val imageUrl = coverUrl.ifEmpty { "https://lgimages.s3.amazonaws.com/nc-md.gif" }
//
//        return rememberAsyncImagePainter(
//            model = ImageRequest.Builder(context)
//                .data(if (source == "User") Uri.parse(imageUrl) else imageUrl)
//                .build(),
//            imageLoader = imageLoader
//        )
//    }
//}
//
//@IgnoreExtraProperties
//data class PersonalBook(
//    var id: Long = -1L,
//    var book: Book = Book(),
//    var status: Status = Status.PlanToRead,
//    var readPages: Int = 0,
//    var rating: Int = 1,
//    var review: String = "",
//    var bookNotes: String = ""
//) {
//    // Constructor mặc định cho Firebase
//    constructor() : this(-1L, Book(), Status.PlanToRead, 0)
//}
//
//enum class Status(val id: Int, val inText: String, val hexColor: String) {
//    PlanToRead(0, "Plan to read", "#808080"),
//    Reading(1, "Reading", "#00FFFF"),
//    Finished(2, "Finished", "#008000"),
//    Dropped(3, "Dropped", "#FF0000");
//
//    // Chỉ định @get:Exclude để Firebase không lưu kiểu Color (gây lỗi)
//    @get:Exclude
//    val color: Color
//        get() = Color(android.graphics.Color.parseColor(hexColor))
//}