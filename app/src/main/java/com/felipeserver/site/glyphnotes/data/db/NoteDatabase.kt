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

            noteDao.upsertNote(
                Note(
                    title = "Bem-vindo ao GlyphNotes!",
                    content = """**Bem-vindo ao GlyphNotes!**

Este é o seu espaço para anotações rápidas e pensamentos. Use **Markdown** para formatar seu texto.

* Crie listas
* **Negrito** e *itálico*
* E muito mais!""",
                    tags = listOf("tutorial", "markdown"),
                    category = "Geral",
                    isPinned = true,
                    creationDate = currentDate,
                    lastEditDate = currentDate
                )
            )
            noteDao.upsertNote(
                Note(
                    title = "Ideias para o Jantar",
                    content = """* Comprar ingredientes para a lasanha
* Fazer a salada
* Não esquecer a sobremesa!""",
                    tags = listOf("compras", "casa"),
                    category = "Tarefas",
                    isPinned = false,
                    creationDate = currentDate,
                    lastEditDate = currentDate
                )
            )
            noteDao.upsertNote(
                Note(
                    title = "Lembretes Rápidos",
                    content = """* Ligar para o dentista
* Enviar o relatório até as 17h
* Comprar ração para o gato""",
                    tags = listOf("lembretes", "trabalho"),
                    category = "Trabalho",
                    isPinned = true,
                    creationDate = currentDate,
                    lastEditDate = currentDate
                )
            )
            noteDao.upsertNote(
                Note(
                    title = "Inspiração do Dia",
                    content = """*A criatividade é a inteligência se divertindo.*

– Albert Einstein""",
                    tags = listOf("citações", "inspiração"),
                    category = "Pessoal",
                    isPinned = false,
                    creationDate = currentDate,
                    lastEditDate = currentDate
                )
            )
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
