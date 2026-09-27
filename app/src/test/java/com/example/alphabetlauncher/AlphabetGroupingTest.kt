package com.example.alphabetlauncher

import com.example.alphabetlauncher.model.AppInfo
import com.example.alphabetlauncher.utils.DOT_CHAR
import com.example.alphabetlauncher.utils.getAppsForLetter
import com.example.alphabetlauncher.utils.getAvailableLetters
import com.example.alphabetlauncher.utils.groupAppsByLetter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlphabetGroupingTest {

    @Test
    fun groupAppsByLetter_groupsAlphabetically() {
        val apps = listOf(
            AppInfo(name = "Gmail", packageName = "com.google.android.gm"),
            AppInfo(name = "Google Photos", packageName = "com.google.android.apps.photos"),
            AppInfo(name = "Chrome", packageName = "com.android.chrome"),
            AppInfo(name = "camera", packageName = "com.android.camera")
        )

        val grouped = groupAppsByLetter(apps)

        assertEquals(2, grouped['G']?.size)
        assertEquals("Gmail", grouped['G']?.get(0)?.name)
        assertEquals("Google Photos", grouped['G']?.get(1)?.name)

        assertEquals(2, grouped['C']?.size)
        assertEquals("camera", grouped['C']?.get(0)?.name)
        assertEquals("Chrome", grouped['C']?.get(1)?.name)
    }

    @Test
    fun groupAppsByLetter_handlesNumbersAndSpecialChars() {
        val apps = listOf(
            AppInfo(name = "1Password", packageName = "com.onepassword"),
            AppInfo(name = "#Hashtag", packageName = "com.hashtag"),
            AppInfo(name = "WhatsApp", packageName = "com.whatsapp")
        )

        val grouped = groupAppsByLetter(apps)

        assertEquals(2, grouped[DOT_CHAR]?.size)
        assertEquals(1, grouped['W']?.size)
    }

    @Test
    fun getAppsForLetter_returnsEmptyWhenNotFound() {
        val apps = listOf(
            AppInfo(name = "WhatsApp", packageName = "com.whatsapp")
        )
        val grouped = groupAppsByLetter(apps)

        val resultZ = getAppsForLetter(grouped, 'Z')
        assertTrue(resultZ.isEmpty())

        val resultW = getAppsForLetter(grouped, 'W')
        assertEquals(1, resultW.size)
        assertEquals("WhatsApp", resultW[0].name)
    }

    @Test
    fun getAvailableLetters_identifiesNonEmptyLetters() {
        val apps = listOf(
            AppInfo(name = "WhatsApp", packageName = "com.whatsapp"),
            AppInfo(name = "Instagram", packageName = "com.instagram"),
            AppInfo(name = "Calendar", packageName = "com.google.android.calendar")
        )

        val available = getAvailableLetters(apps)

        assertTrue(available.contains('W'))
        assertTrue(available.contains('I'))
        assertTrue(available.contains('C'))
        assertFalse(available.contains('A'))
        assertFalse(available.contains('Z'))
    }
}
