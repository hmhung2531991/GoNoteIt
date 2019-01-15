package eu.napcode.gonoteit.app

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

import eu.napcode.gonoteit.dao.note.NoteDao
import eu.napcode.gonoteit.dao.note.NoteEntity
import eu.napcode.gonoteit.dao.user.UserEntity
import eu.napcode.gonoteit.utils.ReadAccessConverter
import eu.napcode.gonoteit.utils.WriteAccessConverter

@Database(
        entities = arrayOf(NoteEntity::class, UserEntity::class),
        version = 1)
@TypeConverters(ReadAccessConverter::class, WriteAccessConverter::class)
abstract class NotesDataBase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        val NOTES_DATA_BASE_NAME = "gonoteit.db"
    }

    //public abstract UserDao userDao();
}
