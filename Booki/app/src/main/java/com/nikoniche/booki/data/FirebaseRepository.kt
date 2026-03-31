//package com.nikoniche.booki.data
//
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.toObject
//import com.google.firebase.firestore.toObjects
//import com.nikoniche.booki.Book
//import com.nikoniche.booki.PersonalBook
//import kotlinx.coroutines.tasks.await
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class FirebaseRepository {
//    private val db = FirebaseFirestore.getInstance()
//    private val userId = "default_user"
//
//    // Truy cập collection: users -> default_user -> personal_books
//    private val personalBooksRef = db.collection("users").document(userId).collection("personal_books")
//    // Truy cập collection: users -> default_user -> user_created_books
//    private val userCreatedBooksRef = db.collection("users").document(userId).collection("user_created_books")
//
//    suspend fun getPersonalBooks(): List<PersonalBook> = withContext(Dispatchers.IO) {
//        return@withContext try {
//            val snapshot = personalBooksRef.get().await()
//            snapshot.toObjects<PersonalBook>()
//        } catch (e: Exception) {
//            println("Firestore Error Load Personal: ${e.message}")
//            emptyList()
//        }
//    }
//
//    fun addPersonalBook(personalBook: PersonalBook) {
//        // Firestore cho phép dùng String ID. Nếu id = -1, tự tạo ID mới
//        val docRef = if (personalBook.id == -1L) personalBooksRef.document()
//        else personalBooksRef.document(personalBook.id.toString())
//
//        val finalBook = if (personalBook.id == -1L) {
//            personalBook.copy(id = docRef.id.hashCode().toLong())
//        } else personalBook
//
//        docRef.set(finalBook)
//            .addOnSuccessListener { println("Firestore: Saved Personal Book") }
//            .addOnFailureListener { e -> println("Firestore Error: ${e.message}") }
//    }
//
//    fun deletePersonalBook(book: PersonalBook) {
//        personalBooksRef.document(book.id.toString()).delete()
//    }
//
//    suspend fun getUserBooks(): List<Book> = withContext(Dispatchers.IO) {
//        return@withContext try {
//            val snapshot = userCreatedBooksRef.get().await()
//            snapshot.toObjects<Book>()
//        } catch (e: Exception) {
//            println("Firestore Error Load UserBooks: ${e.message}")
//            emptyList()
//        }
//    }
//
//    fun addUserBook(book: Book) {
//        val docRef = if (book.id == -1L) userCreatedBooksRef.document()
//        else userCreatedBooksRef.document(book.id.toString())
//
//        if (book.id == -1L) {
//            book.id = docRef.id.hashCode().toLong()
//        }
//
//        docRef.set(book)
//            .addOnSuccessListener { println("Firestore: Saved User Book") }
//            .addOnFailureListener { e -> println("Firestore Error: ${e.message}") }
//    }
//}

package com.nikoniche.booki.data

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // Giao diện quản lý tài khoản
    val usersRef = db.collection("users")

    // Giao diện truyện
    val storiesRef = db.collection("stories")

    // Giao diện thư viện cá nhân
    fun getLibraryRef(userId: String) = db.collection("users").document(userId).collection("library")

    // Hàm lấy danh sách chương của 1 truyện cụ thể
    fun getChaptersRef(storyId: String) = db.collection("stories").document(storyId).collection("chapters")

    // Giao diện thông báo
    fun getNotificationRef(userId: String) = db.collection("users").document(userId).collection("notifications")

    /* Sau này bạn chỉ cần viết các hàm:
       - addStory(story: Story)
       - addChapter(storyId: String, chapter: Chapter)
       - updateReadingPosition(userId: String, storyId: String, chapterId: String)
    */
}