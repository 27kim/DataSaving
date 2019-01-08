package com.raywenderlich.android.datadrop.model

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.raywenderlich.android.datadrop.app.DataDropApplication

/**
 * Created by LGCNS on 2019-01-04.
 */
object SharedPrefsRepository : DropRepository {

    private const val SHARED_PREFS_REPOSITORY = "SHARED_PREFS_REPOSITORY"
    private val gson = Gson()

    fun sharedPrefs() = DataDropApplication.getAppContext().getSharedPreferences(
            SHARED_PREFS_REPOSITORY, Context.MODE_PRIVATE
    )

    override fun addDrop(drop: Drop) {
        sharedPrefs().edit().putString(drop.id, gson.toJson(drop)).apply()
    }

    /**
     * key 를 뽑에서 key 로 밸류를 가지고 온 다음에
     * null 을 filter 하고
     * 밸류를 json 으로 바꿔서 가져온다
     */
    override fun getDrops(): List<Drop> {
        return sharedPrefs().all.keys
                .map{ sharedPrefs().getString(it,"")}
                .filterNot { it.isNullOrBlank() }
                .map { gson.fromJson(it, Drop::class.java) }
    }

    override fun clearDrop(drop: Drop) {
        sharedPrefs().edit().remove(drop.id).apply()
    }

    override fun clearAllDrops() {
        sharedPrefs().edit().clear().apply()
    }
}
