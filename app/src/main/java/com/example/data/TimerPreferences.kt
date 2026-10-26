package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ColorThemeConfig
import com.example.model.IndicatorShape
import com.example.model.WorkoutPreset
import com.example.model.WorkoutSessionRecord
import org.json.JSONArray
import org.json.JSONObject

class TimerPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("body_tone_boxing_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_NAME = "theme_name"
        private const val KEY_WORK_COLOR = "theme_work_color"
        private const val KEY_REST_COLOR = "theme_rest_color"
        private const val KEY_PREP_COLOR = "theme_prep_color"
        private const val KEY_WARN_COLOR = "theme_warn_color"
        private const val KEY_ACTIVE_IND_COLOR = "theme_active_ind_color"
        private const val KEY_COMPL_IND_COLOR = "theme_compl_ind_color"
        private const val KEY_REMAIN_IND_COLOR = "theme_remain_ind_color"
        private const val KEY_IND_SHAPE = "theme_ind_shape"
        private const val KEY_SOUND_PROFILE = "theme_sound_profile"

        private const val KEY_SOUND_ENABLED = "setting_sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "setting_vibrate_enabled"
        private const val KEY_KEEP_SCREEN_ON = "setting_keep_screen_on"
        private const val KEY_SELECTED_PRESET_ID = "selected_preset_id"
        private const val KEY_CUSTOM_PRESETS = "custom_presets_json"
        private const val KEY_HISTORY = "history_json"
    }

    fun saveColorTheme(theme: ColorThemeConfig) {
        prefs.edit()
            .putString(KEY_THEME_NAME, theme.name)
            .putLong(KEY_WORK_COLOR, theme.workColorHex)
            .putLong(KEY_REST_COLOR, theme.restColorHex)
            .putLong(KEY_PREP_COLOR, theme.prepareColorHex)
            .putLong(KEY_WARN_COLOR, theme.warningColorHex)
            .putLong(KEY_ACTIVE_IND_COLOR, theme.activeIndicatorHex)
            .putLong(KEY_COMPL_IND_COLOR, theme.completedIndicatorHex)
            .putLong(KEY_REMAIN_IND_COLOR, theme.remainingIndicatorHex)
            .putString(KEY_IND_SHAPE, theme.indicatorShape.name)
            .apply()
    }

    fun loadColorTheme(): ColorThemeConfig {
        val name = prefs.getString(KEY_THEME_NAME, ColorThemeConfig.BodyToneSignature.name) ?: ColorThemeConfig.BodyToneSignature.name
        val work = prefs.getLong(KEY_WORK_COLOR, ColorThemeConfig.BodyToneSignature.workColorHex)
        val rest = prefs.getLong(KEY_REST_COLOR, ColorThemeConfig.BodyToneSignature.restColorHex)
        val prep = prefs.getLong(KEY_PREP_COLOR, ColorThemeConfig.BodyToneSignature.prepareColorHex)
        val warn = prefs.getLong(KEY_WARN_COLOR, ColorThemeConfig.BodyToneSignature.warningColorHex)
        val activeInd = prefs.getLong(KEY_ACTIVE_IND_COLOR, ColorThemeConfig.BodyToneSignature.activeIndicatorHex)
        val complInd = prefs.getLong(KEY_COMPL_IND_COLOR, ColorThemeConfig.BodyToneSignature.completedIndicatorHex)
        val remainInd = prefs.getLong(KEY_REMAIN_IND_COLOR, ColorThemeConfig.BodyToneSignature.remainingIndicatorHex)
        val shapeStr = prefs.getString(KEY_IND_SHAPE, IndicatorShape.GLOVES.name) ?: IndicatorShape.GLOVES.name

        val shape = try {
            IndicatorShape.valueOf(shapeStr)
        } catch (_: Exception) {
            IndicatorShape.GLOVES
        }

        return ColorThemeConfig(
            name = name,
            workColorHex = work,
            restColorHex = rest,
            prepareColorHex = prep,
            warningColorHex = warn,
            activeIndicatorHex = activeInd,
            completedIndicatorHex = complInd,
            remainingIndicatorHex = remainInd,
            indicatorShape = shape
        )
    }

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    var isKeepScreenOn: Boolean
        get() = prefs.getBoolean(KEY_KEEP_SCREEN_ON, true)
        set(value) = prefs.edit().putBoolean(KEY_KEEP_SCREEN_ON, value).apply()

    var selectedPresetId: String
        get() = prefs.getString(KEY_SELECTED_PRESET_ID, "pro_boxing") ?: "pro_boxing"
        set(value) = prefs.edit().putString(KEY_SELECTED_PRESET_ID, value).apply()

    fun saveCustomPresets(presets: List<WorkoutPreset>) {
        val array = JSONArray()
        for (p in presets) {
            val obj = JSONObject().apply {
                put("id", p.id)
                put("name", p.name)
                put("desc", p.description)
                put("rounds", p.rounds)
                put("work", p.roundDurationSec)
                put("rest", p.restDurationSec)
                put("prep", p.prepareDurationSec)
                put("warn", p.warningSeconds)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CUSTOM_PRESETS, array.toString()).apply()
    }

    fun loadCustomPresets(): List<WorkoutPreset> {
        val raw = prefs.getString(KEY_CUSTOM_PRESETS, null) ?: return emptyList()
        val list = mutableListOf<WorkoutPreset>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WorkoutPreset(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        description = obj.optString("desc", ""),
                        rounds = obj.getInt("rounds"),
                        roundDurationSec = obj.getInt("work"),
                        restDurationSec = obj.getInt("rest"),
                        prepareDurationSec = obj.optInt("prep", 10),
                        warningSeconds = obj.optInt("warn", 10),
                        colorTheme = loadColorTheme()
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun addHistoryRecord(record: WorkoutSessionRecord) {
        val history = loadHistory().toMutableList()
        history.add(0, record)
        if (history.size > 50) history.removeAt(history.lastIndex)

        val array = JSONArray()
        for (item in history) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("time", item.timestamp)
                put("name", item.workoutName)
                put("done", item.roundsCompleted)
                put("total", item.totalRounds)
                put("dur", item.totalDurationSeconds)
                put("cal", item.caloriesBurned)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    fun loadHistory(): List<WorkoutSessionRecord> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<WorkoutSessionRecord>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WorkoutSessionRecord(
                        id = obj.getString("id"),
                        timestamp = obj.getLong("time"),
                        workoutName = obj.getString("name"),
                        roundsCompleted = obj.getInt("done"),
                        totalRounds = obj.getInt("total"),
                        totalDurationSeconds = obj.getInt("dur"),
                        caloriesBurned = obj.getInt("cal")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}
