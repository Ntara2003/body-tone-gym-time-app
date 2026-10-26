package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TimerPreferences
import com.example.model.ColorThemeConfig
import com.example.model.IndicatorShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Body Tone Boxing", appName)
  }

  @Test
  fun `test color theme preferences persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = TimerPreferences(context)
    val customTheme = ColorThemeConfig(
      name = "Custom Boxing Test",
      workColorHex = 0xFFFF1838L,
      restColorHex = 0xFF00E5FFL,
      prepareColorHex = 0xFFFF9800L,
      warningColorHex = 0xFFFFEB3BL,
      activeIndicatorHex = 0xFFFF1838L,
      completedIndicatorHex = 0xFF00E676L,
      remainingIndicatorHex = 0xFF2A2A2EL,
      indicatorShape = IndicatorShape.GLOVES
    )
    prefs.saveColorTheme(customTheme)
    val loaded = prefs.loadColorTheme()

    assertEquals("Custom Boxing Test", loaded.name)
    assertEquals(0xFFFF1838L, loaded.workColorHex)
    assertEquals(0xFF00E5FFL, loaded.restColorHex)
    assertEquals(IndicatorShape.GLOVES, loaded.indicatorShape)
  }
}
