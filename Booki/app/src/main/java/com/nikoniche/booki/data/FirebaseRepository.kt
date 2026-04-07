package com.nikoniche.booki.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.nikoniche.booki.Chapter
import com.nikoniche.booki.Story
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storiesRef = db.collection("stories")

    suspend fun getStoriesByAuthor(authorId: String): List<Story> = withContext(Dispatchers.IO) {
        try {
            storiesRef.whereEqualTo("authorId", authorId)
                .get().await().toObjects<Story>()
        } catch (e: Exception) { emptyList() }
    }

    fun saveStory(story: Story, onSuccess: () -> Unit) {
        val docRef = if (story.id.isEmpty()) storiesRef.document() else storiesRef.document(story.id)
        if (story.id.isEmpty()) story.id = docRef.id
        docRef.set(story).addOnSuccessListener { onSuccess() }
    }

    fun deleteStory(storyId: String, onSuccess: () -> Unit) {
        storiesRef.document(storyId).delete().addOnSuccessListener { onSuccess() }
    }

    fun saveChapter(storyId: String, chapter: Chapter, onSuccess: () -> Unit) {
        val chaptersRef = storiesRef.document(storyId).collection("chapters")
        val docRef = if (chapter.id.isEmpty()) chaptersRef.document() else chaptersRef.document(chapter.id)
        if (chapter.id.isEmpty()) chapter.id = docRef.id
        docRef.set(chapter).addOnSuccessListener { onSuccess() }
    }

    suspend fun getChapters(storyId: String): List<Chapter> = withContext(Dispatchers.IO) {
        try {
            storiesRef.document(storyId).collection("chapters")
                .orderBy("order", Query.Direction.ASCENDING)
                .get().await().toObjects<Chapter>()
        } catch (e: Exception) { emptyList() }
    }

    fun deleteChapter(storyId: String, chapterId: String, onSuccess: () -> Unit) {
        storiesRef.document(storyId).collection("chapters").document(chapterId)
            .delete().addOnSuccessListener { onSuccess() }
    }
}