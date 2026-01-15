package com.felipeserver.site.glyphnotes.ui.viewmodel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.felipeserver.site.glyphnotes.data.db.Note
import com.felipeserver.site.glyphnotes.data.db.NoteDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())
    private val _lastNote = MutableStateFlow<Note?>(null)
    private val _uiState = MutableStateFlow(NoteDetailUiState())
    private val _allTags = MutableStateFlow<List<String>>(emptyList())

    val allNotes: StateFlow<List<Note>> = _allNotes.asStateFlow()
    val lastNote: StateFlow<Note?> = _lastNote.asStateFlow()
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()
    val allTags: StateFlow<List<String>> = _allTags.asStateFlow()

    private var originalState: NoteDetailUiState? = null

    init {
        viewModelScope.launch {
            noteDao.getAllNotes().collect { notes ->
                _allNotes.value = notes
                _allTags.value = notes.flatMap { it.tags }.filter { it.isNotBlank() }.distinct().sorted()
            }
        }
        viewModelScope.launch {
            noteDao.getLastId().collect { note ->
                _lastNote.value = note
            }
        }
    }

    fun onEvent(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.LoadNote -> loadNote(event.id)
            is NoteDetailEvent.OnTitleChange -> updateTitle(event.title)
            is NoteDetailEvent.OnContentChange -> updateContent(event.content)
            is NoteDetailEvent.OnTagsChange -> updateTags(event.tags)
            is NoteDetailEvent.SaveNote -> saveNote()
            is NoteDetailEvent.TogglePin -> togglePin()
            is NoteDetailEvent.DeleteNote -> deleteNote(event.note)
            is NoteDetailEvent.OnDeleteTag -> deleteTag(event.tag)
            is NoteDetailEvent.OnBackPressed -> {
                saveJob?.cancel()
                if (_uiState.value != originalState) {
                    saveNote()
                }
            }
        }
    }

    private fun togglePin() {
        _uiState.value = _uiState.value.copy(isPinned = !_uiState.value.isPinned)
        saveNote()
    }

    private fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
        debouncedSave()
    }

    private fun updateTags(tags: List<String>) {
        _uiState.value = _uiState.value.copy(tags = tags)
        debouncedSave()
    }

    fun getNoteById(id: Int): Flow<Note?> {
        return noteDao.getNoteById(id)
    }

    private fun loadNote(id: Int) {
        if (id == -1) {
            val newState = NoteDetailUiState(isNewNote = true)
            _uiState.value = newState
            originalState = newState
        } else {
            viewModelScope.launch {
                noteDao.getNoteById(id).collect { note ->
                    if (note != null) {
                        val loadedState = NoteDetailUiState(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            tags = note.tags,
                            category = note.category,
                            isPinned = note.isPinned,
                            creationDate = note.creationDate,
                            lastEditDate = note.lastEditDate,
                            isNewNote = false
                        )
                        _uiState.value = loadedState
                        originalState = loadedState
                    }
                }
            }
        }
    }

    private fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
        debouncedSave()
    }

    private var saveJob: Job? = null
    private fun debouncedSave() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(500L)
            saveNote()
        }
    }

    private fun deleteNote(note: Note){
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }
    
    private fun deleteTag(tagToDelete: String) {
        viewModelScope.launch {
            val notesWithTag = _allNotes.value.filter { it.tags.contains(tagToDelete) }
            val updatedNotes = notesWithTag.map { note ->
                note.copy(tags = note.tags - tagToDelete)
            }
            updatedNotes.forEach { note ->
                noteDao.upsertNote(note)
            }
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState.isNewNote && currentState.title.isBlank() && currentState.content.isBlank()) {
                return@launch
            }

            val noteToSave = if (currentState.isNewNote) {
                Note(
                    title = currentState.title,
                    content = currentState.content,
                    tags = currentState.tags,
                    category = currentState.category,
                    isPinned = currentState.isPinned,
                    creationDate = currentState.creationDate,
                    lastEditDate = Date()
                )
            } else {
                Note(
                    id = currentState.id!!,
                    title = currentState.title,
                    content = currentState.content,
                    tags = currentState.tags,
                    category = currentState.category,
                    isPinned = currentState.isPinned,
                    creationDate = currentState.creationDate,
                    lastEditDate = Date()
                )
            }
            noteDao.upsertNote(noteToSave)
        }
    }
}

class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class NoteDetailUiState(
    val id: Int? = null,
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    val category: String = "Geral",
    val isPinned: Boolean = false,
    val creationDate: Date = Date(),
    val lastEditDate: Date = Date(),
    val isNewNote: Boolean = true
)

sealed interface NoteDetailEvent {
    data class LoadNote(val id: Int) : NoteDetailEvent
    data class OnTitleChange(val title: String) : NoteDetailEvent
    data class OnContentChange(val content: String) : NoteDetailEvent
    data class OnTagsChange(val tags: List<String>) : NoteDetailEvent
    data class OnDeleteTag(val tag: String) : NoteDetailEvent
    object SaveNote : NoteDetailEvent
    object TogglePin : NoteDetailEvent
    object OnBackPressed : NoteDetailEvent
    data class DeleteNote(val note: Note) : NoteDetailEvent
}