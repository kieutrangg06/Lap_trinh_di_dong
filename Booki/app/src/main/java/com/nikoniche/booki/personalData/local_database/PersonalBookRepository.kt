package com.nikoniche.booki.personalData.local_database

import com.google.firebase.database.FirebaseDatabase
import com.nikoniche.booki.Book
import com.nikoniche.booki.PersonalBook
import com.nikoniche.booki.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PersonalBookRepository {
    // Lưu ý: userId nên lấy từ Firebase Auth (ví dụ: Firebase.auth.currentUser?.uid)
    private val userId = "default_user"
    private val db = FirebaseDatabase.getInstance().reference
        .child(userId)
        .child("personal_books")

    suspend fun addPersonalBook(personalBook: PersonalBook) {
        withContext(Dispatchers.IO) {
            // Nếu sách chưa có ID, tạo key mới từ Firebase
            val key = if (personalBook.id == -1L) {
                db.push().key ?: System.currentTimeMillis().toString()
            } else {
                personalBook.id.toString()
            }

            // Đồng nhất ID trong object và Key trên Database
            val finalBook = if (personalBook.id == -1L) {
                personalBook.copy(id = key.hashCode().toLong())
            } else {
                personalBook
            }

            db.child(finalBook.id.toString()).setValue(finalBook).await()
        }
    }

    suspend fun getPersonalBooks(): List<PersonalBook> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = db.get().await()
                val books = mutableListOf<PersonalBook>()

                for (child in snapshot.children) {
                    // Firebase tự động chuyển đổi JSON thành object PersonalBook
                    child.getValue(PersonalBook::class.java)?.let {
                        books.add(it)
                    }
                }
                books
            } catch (e: Exception) {
                println("Firebase Error: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun updatePersonalBook(personalBook: PersonalBook) {
        // Trong Firebase, update và add đều dùng setValue tại ID cụ thể
        addPersonalBook(personalBook)
    }

    suspend fun deletePersonalBook(personalBook: PersonalBook) {
        withContext(Dispatchers.IO) {
            db.child(personalBook.id.toString()).removeValue().await()
        }
    }

    // Firebase trả về dữ liệu Snapshot, không dùng Flow kiểu Room trực tiếp
    // Nếu bạn muốn lấy 1 cuốn sách cụ thể:
    suspend fun getPersonalBookById(id: Long): PersonalBook? {
        return withContext(Dispatchers.IO) {
            db.child(id.toString()).get().await().getValue(PersonalBook::class.java)
        }
    }
}