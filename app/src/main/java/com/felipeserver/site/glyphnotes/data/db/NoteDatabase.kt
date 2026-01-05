package com.felipeserver.site.glyphnotes.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Database(entities = [Note::class], version = 2, exportSchema = false) // Incremented version
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    private class NoteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.noteDao())
                }
            }
        }

        suspend fun populateDatabase(noteDao: NoteDao) {
            val currentDate = Date()

            noteDao.upsertNote(Note(
                title = "Bem-vindo ao GlyphNotes!",
                content = "Este é um aplicativo de anotações simples e elegante. Sinta-se à vontade para explorar e criar suas próprias notas.",
                tags = listOf("tutorial", "bem-vindo"),
                category = "Geral",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(title = "Recursos",
                content = "Você pode criar, editar, excluir e organizar suas notas em pastas.",
                tags = listOf("recursos"),
                category = "Geral",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Começando",
                content = "Toque no botão '+' para criar uma nova nota.",
                tags = listOf("tutorial"),
                category = "Geral",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Lista de Compras",
                content = "Leite, Pão, Ovos, Frutas",
                tags = listOf("compras", "casa"),
                category = "Tarefas",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Ideias para o projeto",
                content = "Implementar login com Google, Adicionar tema escuro, Criar tela de estatísticas.",
                tags = listOf("projeto", "ideias", "desenvolvimento"),
                category = "Trabalho",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Lembrete Reunião",
                content = "Reunião de equipe amanhã às 10h.",
                tags = listOf("trabalho", "reunião"),
                category = "Trabalho",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Filmes para assistir",
                content = "O Poderoso Chefão, Pulp Fiction, O Senhor dos Anéis.",
                tags = listOf("filmes", "lazer"),
                category = "Pessoal",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Receita de Bolo",
                content = "Farinha, açúcar, ovos, leite, fermento. Misturar tudo e assar por 40 minutos.",
                tags = listOf("receitas", "comida"),
                category = "Pessoal",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Aniversário da Maria",
                content = "Comprar presente e ligar para parabenizar.",
                tags = listOf("aniversário", "amigos"),
                category = "Social",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Estudar para a prova",
                content = "Revisar os capítulos 4 e 5 do livro de matemática.",
                tags = listOf("estudos", "prova"),
                category = "Estudos",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Playlist para corrida",
                content = "Eye of the Tiger, Don't Stop Me Now, Born to Run.",
                tags = listOf("música", "corrida", "esporte"),
                category = "Lazer",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Presente para o João",
                content = "Livro de ficção científica ou um jogo novo.",
                tags = listOf("presente", "amigos"),
                category = "Social",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Citações Inspiradoras",
                content = "A persistência é o caminho do êxito. - Charles Chaplin",
                tags = listOf("citações", "inspiração"),
                category = "Pessoal",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Lugares para viajar",
                content = "Japão, Itália, Nova Zelândia.",
                tags = listOf("viagem", "sonhos"),
                category = "Lazer",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Metas para 2025",
                content = "Aprender a tocar violão, ler 12 livros, viajar para um país novo.",
                tags = listOf("metas", "2025"),
                category = "Pessoal",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Ideias de posts para o blog",
                content = "Review do novo celular, Guia de viagem para o Rio de Janeiro, Receitas fáceis para o dia a dia.",
                tags = listOf("blog", "ideias"),
                category = "Trabalho",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Lembrete médico",
                content = "Consulta com o dentista na próxima terça-feira.",
                tags = listOf("saúde", "lembrete"),
                category = "Saúde",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Curso de fotografia",
                content = "Pesquisar cursos online de fotografia para iniciantes.",
                tags = listOf("curso", "fotografia", "hobby"),
                category = "Estudos",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Restaurantes para experimentar",
                content = "Restaurante italiano 'La Trattoria', Comida japonesa no 'Sushi Place'.",
                tags = listOf("comida", "restaurantes"),
                category = "Lazer",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Pagar contas",
                content = "Conta de luz, água e internet vencem no dia 10.",
                tags = listOf("finanças", "contas"),
                category = "Tarefas",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Contato do Eletricista",
                content = "José - (11) 99999-8888",
                tags = listOf("contatos", "serviços"),
                category = "Casa",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Livros para ler",
                content = "1984, O Apanhador no Campo de Centeio, Cem Anos de Solidão.",
                tags = listOf("livros", "leitura"),
                category = "Lazer",
                isPinned = false,
                creationDate = currentDate,
                lastEditDate = currentDate))
            noteDao.upsertNote(Note(
                title = "Arrumar o armário",
                content = "Separar roupas para doação e organizar o restante.",
                tags = listOf("casa", "organização"),
                category = "Tarefas",
                isPinned = true,
                creationDate = currentDate,
                lastEditDate = currentDate))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .addCallback(NoteDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
