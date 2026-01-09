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



    val allNotes: StateFlow<List<Note>> = _allNotes.asStateFlow()
    val lastNote: StateFlow<Note?> = _lastNote.asStateFlow()
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()



    init {
        viewModelScope.launch {
            noteDao.getAllNotes().collect { notes ->
                _allNotes.value = notes
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
            is NoteDetailEvent.SaveNote -> saveNote()
            is NoteDetailEvent.DeleteNote -> deleteNote(event.note)
            is NoteDetailEvent.OnBackPressed -> {
                saveJob?.cancel()

                saveNote()
            }
        }
    }

    private fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
        // Dispara o salvamento automático após um tempo
        debouncedSave()
    }

    fun getNoteById(id: Int): Flow<Note?> {
        return noteDao.getNoteById(id)
    }

    private fun loadNote(id: Int) {
        if (id == -1) {
            // Modo Criação: Garante que o estado é o inicial e limpo.
            _uiState.value = NoteDetailUiState(isNewNote = true)
        } else {
            // Modo Edição: Busca a nota e preenche o estado.
            viewModelScope.launch {
                noteDao.getNoteById(id).collect { note ->
                    // O 'collect' será chamado sempre que a nota mudar no DB.
                    if (note != null) {
                        _uiState.value = NoteDetailUiState(
                            id = note.id,
                            title = note.title,
                            content = note.content,
                            tags = note.tags,
                            category = note.category,
                            isPinned = note.isPinned,
                            creationDate = note.creationDate,
                            lastEditDate = note.lastEditDate,
                            isNewNote = false // Crucial: informa que estamos editando uma nota existente.
                        )
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
        saveJob?.cancel() // Cancela o salvamento anterior
        saveJob = viewModelScope.launch {
            delay(500L) // Espera 500ms
            saveNote()
        }
    }

    private fun deleteNote(note: Note){
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // LÓGICA CENTRALIZADA: decide se cria ou atualiza
            if (currentState.title.isBlank() && currentState.content.isBlank()) {
                // Não salva notas completamente vazias
                return@launch
            }

            if (currentState.isNewNote) {
                // CRIAR
                val newNote = Note(
                    title = currentState.title,
                    content = currentState.content,
                    tags = emptyList(),          // Começa sem tags
                    category = "Geral",          // Uma categoria padrão
                    isPinned = false,            // Novas notas não são fixadas
                    creationDate = Date(),       // Data de agora
                    lastEditDate = Date()        // Data de agora

                )
                val newId = noteDao.upsertNote(newNote)
                // Atualiza o estado para refletir que a nota agora existe
                _uiState.value = _uiState.value.copy(id = newId.toInt(), isNewNote = false)
            } else {
                // ATUALIZAR
                val noteToUpdate = Note(
                    id = currentState.id!!,
                    title = currentState.title,
                    content = currentState.content,
                    tags = currentState.tags,
                    category = currentState.category,
                    isPinned = currentState.isPinned,
                    creationDate = currentState.creationDate,
                    lastEditDate = Date()


                )
                noteDao.upsertNote(noteToUpdate)
            }
        }
    }
}


class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return NoteViewModel(noteDao) as T
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
    object SaveNote : NoteDetailEvent
    object OnBackPressed : NoteDetailEvent

    data class DeleteNote(val note: Note) : NoteDetailEvent

}