package com.busanit.subway_project.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "station_points.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "stations"
        const val COLUMN_TITLE = "scode"
        const val COLUMN_X1 = "x1"
        const val COLUMN_Y1 = "y1"
        const val COLUMN_X2 = "x2"
        const val COLUMN_Y2 = "y2"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_X1 INTEGER, " +
                "$COLUMN_Y1 INTEGER, " +
                "$COLUMN_X2 INTEGER, " +
                "$COLUMN_Y2 INTEGER)"
        db?.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}