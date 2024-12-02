package it.puntoettore.fidelity.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import it.puntoettore.fidelity.data.dao.AppSettingsDao
import it.puntoettore.fidelity.data.dao.BookDao
import it.puntoettore.fidelity.data.dao.UserDao
import it.puntoettore.fidelity.domain.AppSettings
import it.puntoettore.fidelity.domain.Book
import it.puntoettore.fidelity.domain.User
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

@Database(
    entities = [Book::class, User::class, AppSettings::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(BookDatabaseConstructor::class)
@TypeConverters(BookTypeConverter::class)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun userDao(): UserDao
    abstract fun appSettingsDao(): AppSettingsDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object BookDatabaseConstructor : RoomDatabaseConstructor<BookDatabase> {
    override fun initialize(): BookDatabase
}

class BookTypeConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return Json.decodeFromString(
            ListSerializer(String.serializer()), value
        )
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Json.encodeToString(
            ListSerializer(String.serializer()), list
        )
    }
}