package arzaq.azmi.moviecatalogue.db


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import arzaq.azmi.moviecatalogue.db.DatabaseContract.FilmColumns


internal class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_NAME = "dbmoviecatalogue"

        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE ${FilmColumns.TABLE_NAME}" +
                " (${FilmColumns.ID} INTEGER PRIMARY KEY," +
                " ${FilmColumns.NAME} TEXT NOT NULL," +
                " ${FilmColumns.DESCRIPTION} TEXT NOT NULL," +
                " ${FilmColumns.IMG} TEXT NOT NULL," +
                " ${FilmColumns.IMDB} TEXT NOT NULL," +
                " ${FilmColumns.RELEASE_DATE} TEXT NOT NULL," +
                " ${FilmColumns.CATEGORIES} TEXT NOT NULL," +
                " ${FilmColumns.LANGUAGE} TEXT NOT NULL," +
                " ${FilmColumns.TYPE} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_NOTE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${FilmColumns.TABLE_NAME}")
        onCreate(db)
    }
}