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
