/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.raywenderlich.android.datadrop.model

import com.raywenderlich.android.datadrop.app.DataDropApplication
import com.raywenderlich.android.datadrop.model.DropDbSchema.DropTable
import android.content.ContentValues
import android.util.Log
import com.raywenderlich.android.datadrop.R.string.drop
import com.raywenderlich.android.datadrop.model.DropDbSchema.DB_NAME
import java.io.IOException


class SQLiteRepository : DropRepository {

    private val database = DropDbHelper(DataDropApplication.getAppContext()).writableDatabase

    /**
     * contentValue 에 key-value pair 로 담아서 insert 한다
     * */
    override fun addDrop(drop: Drop) {
        var contentValues = getDropContentValues(drop)
        database.insert(DB_NAME, null, contentValues)
    }

    /**
     * 데이터를 조회해서 쿼리에 담은 다음에
     * 한 개 씩 뽑아서 쓴다
     * */
    override fun getDrops(): List<Drop> {

        var drops = mutableListOf<Drop>()

        var cursors = queryDrops(null, null)

        try {
            cursors.moveToFirst()
            while (!cursors.isAfterLast) {
                drops.add(cursors.getDrop())
                cursors.moveToNext()
            }
        } catch (e: Exception) {

        }
        return drops
    }
//        val drops = mutableListOf<Drop>()
//
//        val cursor = queryDrops(null, null)
//
//        try {
//            cursor.moveToFirst()
//            while (!cursor.isAfterLast) {
//                drops.add(cursor.getDrop())
//                cursor.moveToNext()
//            }
//        } catch (e: IOException) {
//            Log.e("SQLiteRepository", "Error reading drops")
//        } finally {
//            cursor.close()
//        }
//
//        return drops


    override fun clearDrop(drop: Drop) {
        database.delete(DB_NAME, "id = ?", arrayOf(drop.id))
    }

    override fun clearAllDrops() {
        database.delete(DB_NAME, null, null)
    }


    private fun getDropContentValues(drop: Drop): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(DropTable.Columns.ID, drop.id)
        contentValues.put(DropTable.Columns.LATITUDE, drop.latLng.latitude)
        contentValues.put(DropTable.Columns.LONGITUDE, drop.latLng.longitude)
        contentValues.put(DropTable.Columns.DROP_MESSAGE, drop.dropMessage)
        contentValues.put(DropTable.Columns.MARKER_COLOR, drop.markerColor)
        return contentValues
    }

    private fun queryDrops(where: String?, whereArgs: Array<String>?): DropCursorWrapper {
        val cursor = database.query(
                DropTable.NAME,
                null, // select all columns
                where,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        )

        return DropCursorWrapper(cursor)
    }
}