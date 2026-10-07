package com.gempatrack.app.ui.theme

import androidx.compose.ui.graphics.Color

/*
 * Palet GempaTrack — oranye bertema gempa (burnt orange / terakota) yang
 * nyaman dilihat mata: saturasi rendah-sedang, bukan oranye neon.
 * Kontras latar terang memenuhi WCAG AA (>= 4.5:1).
 */

// ----- Light color scheme -----
val PrimaryLight = Color(0xFFA8481B)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFFFDBC4)
val OnPrimaryContainerLight = Color(0xFF2B0F00)

val SecondaryLight = Color(0xFF77574A)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFF5DED3)
val OnSecondaryContainerLight = Color(0xFF2B170E)

val BackgroundLight = Color(0xFFFFF8F4)
val OnBackgroundLight = Color(0xFF241913)
val SurfaceLight = Color(0xFFFFF8F4)
val OnSurfaceLight = Color(0xFF241913)
val SurfaceVariantLight = Color(0xFFF5DED3)
val OnSurfaceVariantLight = Color(0xFF53433C)
val OutlineLight = Color(0xFF85736C)
val ErrorLight = Color(0xFF9C4146)
val OnErrorLight = Color(0xFFFFFFFF)

// ----- Dark color scheme -----
val PrimaryDark = Color(0xFFFFB68D)
val OnPrimaryDark = Color(0xFF5A1B00)
val PrimaryContainerDark = Color(0xFF7C3312)
val OnPrimaryContainerDark = Color(0xFFFFDBC4)

val SecondaryDark = Color(0xFFE7BDB0)
val OnSecondaryDark = Color(0xFF442A20)
val SecondaryContainerDark = Color(0xFF5B4036)
val OnSecondaryContainerDark = Color(0xFFF5DED3)

val BackgroundDark = Color(0xFF201A17)
val OnBackgroundDark = Color(0xFFEDE1DB)
val SurfaceDark = Color(0xFF2A211D)
val OnSurfaceDark = Color(0xFFEDE1DB)
val SurfaceVariantDark = Color(0xFF51443E)
val OnSurfaceVariantDark = Color(0xFFD7C2BA)
val OutlineDark = Color(0xFF9F8C84)
val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)

// ----- Badge magnitudo: gradasi hangat rendah -> tinggi, tetap redup -----
val MagnitudeCalm = Color(0xFFE8E0D8)      // M < 4.0 — abu hangat
val MagnitudeCalmOn = Color(0xFF4A403A)
val MagnitudeModerate = Color(0xFFF2D9C4)  // M 4.0 - 4.9 — peach redup
val MagnitudeModerateOn = Color(0xFF5A2E10)
val MagnitudeStrong = Color(0xFFEFC4A3)    // M 5.0 - 5.9
val MagnitudeStrongOn = Color(0xFF5A2508)
val MagnitudeSevere = Color(0xFFE4A883)    // M >= 6.0 — tetap oranye lembut
val MagnitudeSevereOn = Color(0xFF4A1B04)
