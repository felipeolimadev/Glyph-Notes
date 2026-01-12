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

/**
 * ViewModel para a tela de detalhes da nota.
 *
 * Gerencia o estado da UI (`NoteDetailUiState`) e lida com todos os eventos do usuário
 * (`NoteDetailEvent`) para criar, ler, atualizar e deletar notas.
 *
 * @property noteDao O Data Access Object para interagir com o banco de dados de notas.
 */
class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {
    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())
    private val _lastNote = MutableStateFlow<Note?>(null)
    private val _uiState = MutableStateFlow(NoteDetailUiState())
    private val _allTags = MutableStateFlow<List<String>>(emptyList())

    /** Representa a lista de todas as notas no banco de dados. */
    val allNotes: StateFlow<List<Note>> = _allNotes.asStateFlow()

    /** Representa a última nota inserida no banco de dados. */
    val lastNote: StateFlow<Note?> = _lastNote.asStateFlow()

    /** O estado atual da UI para a tela de detalhes da nota, observável pela UI. */
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    /** Uma lista de todas as tags únicas usadas em todas as notas, ordenadas alfabeticamente. */
    val allTags: StateFlow<List<String>> = _allTags.asStateFlow()

    /** Armazena uma "fotografia" do estado da nota quando ela foi carregada. Usado para detectar se houve alterações. */
    private var originalState: NoteDetailUiState? = null

    init {
        // Coleta todas as notas e deriva a lista de tags únicas.
        viewModelScope.launch {
            noteDao.getAllNotes().collect { notes ->
                _allNotes.value = notes
                _allTags.value = notes.flatMap { it.tags }.filter { it.isNotBlank() }.distinct().sorted()
            }
        }
        // Coleta a nota mais recente.
        viewModelScope.launch {
            noteDao.getLastId().collect { note ->
                _lastNote.value = note
            }
        }
    }

    /**
     * Ponto de entrada único para todos os eventos da UI.
     * @param event O evento que a UI disparou (ex: mudança de texto, clique de botão).
     */
    fun onEvent(event: NoteDetailEvent) {
        when (event) {
            is NoteDetailEvent.LoadNote -> loadNote(event.id)
            is NoteDetailEvent.OnTitleChange -> updateTitle(event.title)
            is NoteDetailEvent.OnContentChange -> updateContent(event.content)
            is NoteDetailEvent.OnTagsChange -> updateTags(event.tags)
            is NoteDetailEvent.SaveNote -> saveNote()
            is NoteDetailEvent.DeleteNote -> deleteNote(event.note)
            is NoteDetailEvent.OnBackPressed -> {
                saveJob?.cancel() // Cancela qualquer salvamento automático pendente.
                // Salva apenas se o estado atual for diferente do original.
                if (_uiState.value != originalState) {
                    saveNote()
                }
            }
        }
    }

    /** Atualiza o título no estado da UI e aciona o salvamento automático. */
    private fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
        debouncedSave()
    }

    /** Atualiza a lista de tags no estado da UI e aciona o salvamento automático. */
    private fun updateTags(tags: List<String>) {
        _uiState.value = _uiState.value.copy(tags = tags)
        debouncedSave()
    }

    /** Retorna um Flow que emite uma nota específica pelo seu ID. */
    fun getNoteById(id: Int): Flow<Note?> {
        return noteDao.getNoteById(id)
    }

    /**
     * Carrega uma nota existente do banco de dados ou prepara o estado para uma nova nota.
     * @param id O ID da nota a ser carregada, ou -1 para criar uma nova nota.
     */
    private fun loadNote(id: Int) {
        if (id == -1) {
            // Modo Criação: Prepara um estado limpo.
            val newState = NoteDetailUiState(isNewNote = true)
            _uiState.value = newState
            originalState = newState
        } else {
            // Modo Edição: Coleta a nota do banco de dados.
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

    /** Atualiza o conteúdo no estado da UI e aciona o salvamento automático. */
    private fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
        debouncedSave()
    }

    private var saveJob: Job? = null
    /**
     * Aciona a função [saveNote] após um curto período de inatividade do usuário.
     * Isso evita escritas excessivas no banco de dados durante a digitação.
     */
    private fun debouncedSave() {
        saveJob?.cancel() // Cancela o salvamento anterior.
        saveJob = viewModelScope.launch {
            delay(500L) // Espera 500ms.
            saveNote()
        }
    }

    /** Deleta uma nota do banco de dados. */
    private fun deleteNote(note: Note){
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }

    /**
     * Salva a nota atual (cria ou atualiza) no banco de dados.
     * A função confia no Flow do Room para notificar a UI sobre a atualização.
     */
    private fun saveNote() {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Não salva notas novas que estão completamente vazias.
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

/**
 * Factory para criar uma instância de [NoteViewModel] com dependências.
 */
class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Representa o estado completo e observável da tela de detalhes da nota.
 *
 * @property id O ID único da nota no banco de dados. Nulo se for uma nota nova.
 * @property title O título da nota.
 * @property content O corpo do conteúdo da nota.
 * @property tags A lista de tags associadas à nota.
 * @property category A categoria da nota.
 * @property isPinned `true` se a nota estiver fixada.
 * @property creationDate A data em que a nota foi criada.
 * @property lastEditDate A data da última modificação da nota.
 * @property isNewNote `true` se a nota ainda não foi salva no banco de dados.
 */
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

/**
 * Representa todas as ações/eventos que o usuário pode realizar na tela de detalhes da nota.
 */
sealed interface NoteDetailEvent {
    /** Evento para carregar os dados de uma nota específica. */
    data class LoadNote(val id: Int) : NoteDetailEvent
    /** Evento disparado quando o texto do título é alterado. */
    data class OnTitleChange(val title: String) : NoteDetailEvent
    /** Evento disparado quando o texto do conteúdo é alterado. */
    data class OnContentChange(val content: String) : NoteDetailEvent
    /** Evento disparado quando a lista de tags da nota é alterada. */
    data class OnTagsChange(val tags: List<String>) : NoteDetailEvent
    /** Evento para forçar o salvamento da nota. */
    object SaveNote : NoteDetailEvent
    /** Evento disparado quando o usuário pressiona o botão de voltar. */
    object OnBackPressed : NoteDetailEvent
    /** Evento para deletar uma nota. */
    data class DeleteNote(val note: Note) : NoteDetailEvent
}
