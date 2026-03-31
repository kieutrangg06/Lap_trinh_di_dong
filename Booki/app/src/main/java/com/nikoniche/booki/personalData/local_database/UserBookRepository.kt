package com.nikoniche.booki.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.nikoniche.booki.Book
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserBookRepository {
    // Trong thực tế nên lấy từ Firebase.auth.currentUser?.uid
    private val userId = "default_user"
    private val db = FirebaseDatabase.getInstance().reference.child(userId).child("user_created_books")

    suspend fun addUserBook(book: Book) {
        withContext(Dispatchers.IO) {
            // Nếu là sách mới (id = -1), tạo key mới từ Firebase
            val key = if (book.id == -1L) db.push().key else book.id.toString()

            // Chuyển đổi key String của Firebase thành Long để khớp với model Book của bạn
            if (book.id == -1L) {
                book.id = key?.hashCode()?.toLong() ?: System.currentTimeMillis()
            }

            db.child(book.id.toString()).setValue(book).await()
            println("Firebase: Saving ${book.title}")
        }
    }

    suspend fun getUserBooks(): List<Book> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = db.get().await()
                val books = mutableListOf<Book>()
                for (child in snapshot.children) {
                    child.getValue<Book>()?.let {
                        // Firebase tự map JSON thành object Book
                        books.add(it)
                    }
                }
                println("Firebase: Loaded ${books.size} books")
                books
            } catch (e: Exception) {
                println("Firebase Error: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun updateUserBook(book: Book) {
        // Với Firebase, update và add đều dùng setValue tại ID cụ thể
        addUserBook(book)
    }

    suspend fun deleteUserBook(book: Book) {
        withContext(Dispatchers.IO) {
            db.child(book.id.toString()).removeValue().await()
            println("Firebase: Deleted ${book.title}")
        }
    }

    // Lưu ý: getUserBookEntityById thường không dùng trong Firebase
    // vì chúng ta lấy list hoặc dùng Listener real-time.
}