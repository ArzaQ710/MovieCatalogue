package arzaq.azmi.moviecatalogue.db


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.LANGUAGE
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.NAME
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.TABLE_NAME
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.ID
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns.Companion.TYPE
import java.sql.SQLException


class FavoriteHelper(context: Context) {

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private lateinit var dataBaseHelper: DatabaseHelper
        private lateinit var database: SQLiteDatabase

        private var INSTANCE: FavoriteHelper? = null

        fun getInstance(context: Context): FavoriteHelper {
            if (INSTANCE == null) {
                synchronized(SQLiteOpenHelper::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = FavoriteHelper(context)
                    }
                }
            }
            return INSTANCE as FavoriteHelper
        }
    }

    init {
        dataBaseHelper = DatabaseHelper(context)
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun query(selection: String, selectionArgs: Array<String>): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$NAME ASC"
        )
    }

    fun queryById(id: Int, type: String, language: String): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$ID = ? AND $TYPE = ? AND $LANGUAGE = ?",
            arrayOf(id.toString(), type, language),
            null,
            null,
            null,
            null
        )
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun deleteById(selection: String, selectionArgs: Array<String>): Int {
        return database.delete(
            DATABASE_TABLE,
            selection,
            selectionArgs
        )
    }
}