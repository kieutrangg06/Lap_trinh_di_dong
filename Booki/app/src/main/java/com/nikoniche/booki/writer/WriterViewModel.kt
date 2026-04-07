package com.nikoniche.booki.writer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikoniche.booki.Chapter
import com.nikoniche.booki.Story
import com.nikoniche.booki.data.FirebaseRepository
import kotlinx.coroutines.launch

class WriterViewModel : ViewModel() {
    private val repo = FirebaseRepository()
    val myStories = mutableStateOf<List<Story>>(emptyList())
    val chapters = mutableStateOf<List<Chapter>>(emptyList())
    val currentUserId = "test_user_123"

    fun loadMyStories() {
        viewModelScope.launch { myStories.value = repo.getStoriesByAuthor(currentUserId) }
    }

    fun saveStory(story: Story) {
        repo.saveStory(story.copy(authorId = currentUserId)) { loadMyStories() }
    }

    fun loadChapters(storyId: String) {
        viewModelScope.launch { chapters.value = repo.getChapters(storyId) }
    }

    fun addOrUpdateChapter(storyId: String, chapter: Chapter) {
        repo.saveChapter(storyId, chapter) {
            loadChapters(storyId)
            // Cập nhật số lượng chương vào Story (Logic thêm)
            val story = myStories.value.find { it.id == storyId }
            story?.let {
                it.chapterCount = chapters.value.size + (if(chapter.id.isEmpty()) 1 else 0)
                repo.saveStory(it) {}
            }
        }
    }

    fun deleteChapter(storyId: String, chapterId: String) {
        repo.deleteChapter(storyId, chapterId) { loadChapters(storyId) }
    }
}