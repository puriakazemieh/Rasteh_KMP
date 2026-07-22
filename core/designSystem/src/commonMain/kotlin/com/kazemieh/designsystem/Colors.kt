package com.kazemieh.designsystem

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

// =====================================================================================
//  Rasteh Design System · Color Tokens
//  منبع حقیقت: design_handoff_unified_app/README.md (پالت oklch بنفشِ برند).
//  مقادیر oklch به sRGB تبدیل شده‌اند؛ نامِ توکن‌ها برای سازگاری با کد فعلی حفظ شده است.
//  گرادیانِ اصلی: linear-gradient(135deg, oklch(0.56 0.22 300) → oklch(0.4 0.19 288)).
// =====================================================================================

// --- توکن‌های پایه‌ی تم روشن (Light) ---
val AccentLight = Color(0xFF763EBD)        // برند/اکشن اصلی · oklch(0.5 0.19 300)
val Accent2Light = Color(0xFF8B46DF)       // سرِ گرادیان/حالت ثانویه · oklch(0.56 0.22 300)
val AccentSoftLight = Color(0xFFEBE4FA)    // کانتینر ملایمِ برند
val GoldLight = Color(0xFFD18E35)          // سطحِ وفاداری/طلایی · oklch(0.7 0.13 70)
val GoldSoftLight = Color(0xFFF6ECDD)      // کانتینر ملایم طلایی
val BgLight = Color(0xFFFAF9FD)            // پس‌زمینه‌ی اپ · oklch(0.985 0.005 300)
val SurfaceLight = Color(0xFFFFFFFF)       // سطحِ کارت · white
val SurfaceVariantLight = Color(0xFFF3F0F8) // ورودی/چیپ خاکستری · oklch(0.96 0.01 300)
val LineLight = Color(0xFFDFDDE4)          // خط/بوردر · oklch(0.9 0.01 300)
val OutlineVariantLight = Color(0xFFCBC7D6) // نسخه‌ی پررنگ‌تر خط
val InkLight = Color(0xFF1D1E29)           // متنِ اصلی/تیتر · oklch(0.24 0.02 280)
val InkSoftLight = Color(0xFF6F717D)       // متنِ ثانویه · oklch(0.55 0.02 280)

// --- توکن‌های پایه‌ی تم تاریک (Dark) · مشتق از پالتِ بنفش ---
val AccentDark = Color(0xFFA87EEB)         // برند · oklch(0.68 0.16 300)
val Accent2Dark = Color(0xFF886ACF)        // ثانویه · oklch(0.60 0.15 295)
val AccentSoftDark = Color(0xFF302844)     // کانتینر ملایمِ برند (دارک)
val GoldDark = Color(0xFFC9A86A)
val GoldSoftDark = Color(0xFF3A2F1C)
val BgDark = Color(0xFF17151A)             // پس‌زمینه‌ی دارک
val SurfaceDark = Color(0xFF201E24)        // سطحِ کارت (دارک)
val SurfaceVariantDark = Color(0xFF2A272F) // سطحِ فرورفته (دارک)
val LineDark = Color(0xFF393641)           // خط/بوردر (دارک)
val OutlineVariantDark = Color(0xFF44404E)
val InkDark = Color(0xFFEFEDF3)            // متنِ اصلی (دارک)
val InkSoftDark = Color(0xFFA3A3B1)        // متنِ ثانویه (دارک)

// --- رنگ‌های معنایی (Semantic) · منطبق بر بخشِ رنگِ طراحی ---
val SaleLight = Color(0xFFDF202E)          // حراج/زنده/خطا · oklch(0.58 0.22 25)
val SaleDark = Color(0xFFE85862)
val StarLight = Color(0xFFD18E35)          // امتیاز/ستاره · طلایی
val StarDark = Color(0xFFE0B15A)
val OkLight = Color(0xFF137738)            // موفق/«خرید آنلاین» · oklch(0.5 0.13 150)
val OkDark = Color(0xFF33B57F)
val AmberLight = Color(0xFFC26300)         // هشدارِ ملایم · oklch(0.6 0.16 60)
val AmberDark = Color(0xFFE08A2E)
val MapBlueLight = Color(0xFF006AA5)       // نقشه/پارکینگ · oklch(0.5 0.13 240)
val MapBlueDark = Color(0xFF4C9BD1)

// --- رنگ‌های دسته‌بندی (حفظ‌شده برای سازگاری با کد فعلی) ---
val CategoryYellow = Color(0xFFC26300)
val CategoryBlue = Color(0xFF006AA5)
val CategoryGreen = Color(0xFF137738)
val CategoryPurple = Color(0xFF8B46DF)
val CategoryRed = Color(0xFFDF202E)

// =====================================================================================
//  Material3 ColorScheme · مصرف‌کننده‌های فعلی از طریق MaterialTheme.colorScheme
// =====================================================================================

val LightAppColorScheme = lightColorScheme(
    primary = AccentLight,
    onPrimary = Color.White,
    primaryContainer = AccentSoftLight,
    onPrimaryContainer = AccentLight,

    secondary = GoldLight,
    onSecondary = Color.White,
    secondaryContainer = GoldSoftLight,
    onSecondaryContainer = Color(0xFF5C4325),

    tertiary = Accent2Light,
    onTertiary = Color.White,
    tertiaryContainer = AccentSoftLight,
    onTertiaryContainer = AccentLight,

    background = BgLight,
    onBackground = InkLight,

    surface = SurfaceLight,
    onSurface = InkLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = InkSoftLight,

    outline = LineLight,
    outlineVariant = OutlineVariantLight,

    error = SaleLight,
    onError = Color.White,
    errorContainer = Color(0xFFFDEAE8),
    onErrorContainer = Color(0xFF5C1411)
)

val DarkAppColorScheme = darkColorScheme(
    primary = AccentDark,
    onPrimary = Color(0xFF0F1320),
    primaryContainer = AccentSoftDark,
    onPrimaryContainer = Color(0xFFDCE3F6),

    secondary = GoldDark,
    onSecondary = Color(0xFF2A2113),
    secondaryContainer = GoldSoftDark,
    onSecondaryContainer = Color(0xFFF3E6CF),

    tertiary = Accent2Dark,
    onTertiary = Color(0xFF0F1320),
    tertiaryContainer = AccentSoftDark,
    onTertiaryContainer = Color(0xFFDCE3F6),

    background = BgDark,
    onBackground = InkDark,

    surface = SurfaceDark,
    onSurface = InkDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = InkSoftDark,

    outline = LineDark,
    outlineVariant = OutlineVariantDark,

    error = SaleDark,
    onError = Color(0xFF2A0A08),
    errorContainer = Color(0xFF4A0E0A),
    onErrorContainer = Color(0xFFFFD9D5)
)

// =====================================================================================
//  AppColors · توکن‌های گسترده‌ی برند (شامل معنایی‌هایی که در Material نمی‌گنجند)
//  برای کامپوننت‌های کارمیلا (Badge/Chip/ProductCard/...) در فازهای بعد استفاده می‌شود.
// =====================================================================================

data class AppColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val error: Color,
    val onError: Color,

    // توکن‌های برند کارمیلا
    val accent2: Color,
    val accentSoft: Color,
    val gold: Color,
    val line: Color,

    // معنایی
    val sale: Color,
    val star: Color,
    val ok: Color,

    // رنگ‌های دسته‌بندی
    val categoryYellow: Color,
    val categoryBlue: Color,
    val categoryGreen: Color,
    val categoryPurple: Color,
    val categoryRed: Color
)

val LocalAppColors = compositionLocalOf<AppColors> {
    error("No AppColors provided")
}

@Composable
fun provideAppColors(darkTheme: Boolean): AppColors {
    val colors = if (darkTheme) DarkAppColorScheme else LightAppColorScheme
    return remember(darkTheme) {
        AppColors(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            primaryContainer = colors.primaryContainer,
            onPrimaryContainer = colors.onPrimaryContainer,
            secondary = colors.secondary,
            onSecondary = colors.onSecondary,
            secondaryContainer = colors.secondaryContainer,
            onSecondaryContainer = colors.onSecondaryContainer,
            background = colors.background,
            onBackground = colors.onBackground,
            surface = colors.surface,
            onSurface = colors.onSurface,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.onSurfaceVariant,
            outline = colors.outline,
            error = colors.error,
            onError = colors.onError,
            // توکن‌های برند کارمیلا
            accent2 = if (darkTheme) Accent2Dark else Accent2Light,
            accentSoft = if (darkTheme) AccentSoftDark else AccentSoftLight,
            gold = if (darkTheme) GoldDark else GoldLight,
            line = if (darkTheme) LineDark else LineLight,
            // معنایی
            sale = if (darkTheme) SaleDark else SaleLight,
            star = if (darkTheme) StarDark else StarLight,
            ok = if (darkTheme) OkDark else OkLight,
            // دسته‌بندی‌ها
            categoryYellow = CategoryYellow,
            categoryBlue = CategoryBlue,
            categoryGreen = CategoryGreen,
            categoryPurple = CategoryPurple,
            categoryRed = CategoryRed
        )
    }
}
