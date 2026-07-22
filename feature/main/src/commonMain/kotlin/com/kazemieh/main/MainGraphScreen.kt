package com.kazemieh.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kazemieh.bazaar.RastehHomeScreen
import com.kazemieh.cart.CartScreen
import com.kazemieh.catalog.CategorySearchScreen
import com.kazemieh.common.Screen
import androidx.compose.foundation.layout.Row
import com.kazemieh.designsystem.AppFont
import com.kazemieh.designsystem.AppTheme
import com.kazemieh.designsystem.FontSize
import com.kazemieh.designsystem.messagebar.ContentWithMessageBar
import com.kazemieh.designsystem.messagebar.rememberMessageBarState
import com.kazemieh.designsystem.windowSizeClass
import com.kazemieh.main.component.BottomBar
import com.kazemieh.main.component.BottomBarDestination
import com.kazemieh.main.component.SideNavRail
import com.kazemieh.main.component.TitleTopBar
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MainGraphScreen(
    showCart: Boolean = false,
    viewModel: MainViewModel = koinViewModel(),
    navigateToAuth: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToContactUs: () -> Unit,
    navigateToAdminPanel: () -> Unit,
    navigateToBlog: () -> Unit,
    navigateToBlogDetail: (String) -> Unit,
    navigateToDetails: (String) -> Unit,
    navigateToCategorySearch: (Long, String) -> Unit,
    navigateToCheckout: (Double) -> Unit,
    navigateToMyOrders: () -> Unit,
    navigateToWallet: () -> Unit,
    navigateToFavorites: () -> Unit,
    navigateToCustomerClub: () -> Unit,
    navigateToRastehSearch: (rastehId: Long, rastehLabel: String, locationId: Long, locationName: String) -> Unit,
    navigateToBecomeVendor: () -> Unit,
    navigateToAdminShops: () -> Unit,
    navigateToChats: () -> Unit,
    navigateToChatThread: (conversationId: Long, title: String) -> Unit,
    navigateToBookmarks: () -> Unit,
    navigateToShopDetail: (Long) -> Unit,
    navigateToProduct: (Long) -> Unit,
    navigateToMarketOrders: () -> Unit,
    navigateToDeals: () -> Unit,
    navigateToActivity: () -> Unit,
    navigateToReferral: () -> Unit,
    navigateToPriceAlerts: () -> Unit,
    navigateToGiftCards: () -> Unit,
    navigateToCommunity: () -> Unit,
    navigateToNotifications: () -> Unit,
    navigateToFeatures: () -> Unit,
    navigateToVendorPanel: () -> Unit,
    navigateToAdminManage: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val navController = rememberNavController()

    // Switch to cart if needed when screen is loaded
    LaunchedEffect(showCart) {
        if (showCart) {
            navController.navigate(Screen.Cart) {
                popUpTo(Screen.ProductsOverview) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    val currentRoute = navController.currentBackStackEntryAsState()
    val selectedDestination by remember {
        derivedStateOf {
            val route = currentRoute.value?.destination?.route.toString()
            when {
                route.contains(BottomBarDestination.Search.screen.toString()) -> BottomBarDestination.Search
                route.contains(BottomBarDestination.Messages.screen.toString()) -> BottomBarDestination.Messages
                route.contains(BottomBarDestination.Bookmarks.screen.toString()) -> BottomBarDestination.Bookmarks
                route.contains(BottomBarDestination.Profile.screen.toString()) -> BottomBarDestination.Profile
                route.contains(BottomBarDestination.Home.screen.toString()) -> BottomBarDestination.Home
                else -> BottomBarDestination.Home
            }
        }
    }

    val messageBarState = rememberMessageBarState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MainEffect.NavigateToAuth -> navigateToAuth()
                is MainEffect.ShowError -> {
                    effect.message.let { messageBarState.addError(it) }
                }
            }
        }
    }

    val sizeClass = windowSizeClass()
    val isLarge = sizeClass.isLarge

    val onSelectDestination: (BottomBarDestination) -> Unit = { destination ->
        navController.navigate(destination.screen) {
            launchSingleTop = true
            popUpTo<Screen.ProductsOverview> {
                saveState = true
                inclusive = false
            }
            restoreState = true
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .systemBarsPadding()
    ) {
        if (isLarge) {
            SideNavRail(
                cartItemCount = state.cartItemCount,
                selected = selectedDestination,
                onSelect = onSelectDestination,
                expandedLabels = sizeClass.isExpanded,
            )
        }
        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                when (selectedDestination) {
                    // خانهٔ «search-first» است؛ نوارِ جستجوی خودش را دارد و هدرِ سنگین ندارد.
                    BottomBarDestination.Home -> {}
                    // این صفحه‌ها Scaffold و هدرِ اختصاصیِ خودشان را دارند.
                    BottomBarDestination.Search -> {}
                    BottomBarDestination.Messages -> {}
                    BottomBarDestination.Bookmarks -> {}
                    // پروفایل (More) هدرِ خودش را ندارد.
                    BottomBarDestination.Profile -> TitleTopBar(title = selectedDestination.faLabel)
                }
            }
        ) { padding ->
            ContentWithMessageBar(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    ),
                messageBarState = messageBarState,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    NavHost(
                        modifier = Modifier.weight(1f),
                        navController = navController,
                        startDestination = Screen.ProductsOverview
                    ) {
                        composable<Screen.ProductsOverview> {
                            RastehHomeScreen(
                                navigateToRastehSearch = navigateToRastehSearch,
                                navigateToShop = navigateToShopDetail,
                                navigateToProduct = navigateToProduct,
                                navigateToBookmarks = navigateToBookmarks,
                                navigateToSearch = {
                                    navController.navigate(Screen.Search) { launchSingleTop = true }
                                }
                            )
                        }
                        composable<Screen.Search> {
                            com.kazemieh.bazaar.SearchScreen(
                                navigateToShop = navigateToShopDetail
                            )
                        }
                        composable<Screen.ChatList> {
                            com.kazemieh.bazaar.ChatListScreen(
                                navigateBack = {
                                    navController.navigate(Screen.ProductsOverview) {
                                        launchSingleTop = true
                                        popUpTo(Screen.ProductsOverview) { inclusive = false }
                                    }
                                },
                                navigateToChat = navigateToChatThread,
                            )
                        }
                        composable<Screen.Bookmarks> {
                            com.kazemieh.bazaar.BookmarksScreen(
                                navigateBack = {
                                    navController.navigate(Screen.ProductsOverview) {
                                        launchSingleTop = true
                                        popUpTo(Screen.ProductsOverview) { inclusive = false }
                                    }
                                },
                                navigateToShop = navigateToShopDetail,
                            )
                        }
                        composable<Screen.MarketOrders> {
                            com.kazemieh.bazaar.MyOrdersScreen(
                                navigateBack = {
                                    navController.navigate(Screen.ProductsOverview) {
                                        launchSingleTop = true
                                        popUpTo(Screen.ProductsOverview) { inclusive = false }
                                    }
                                },
                                navigateToShop = navigateToShopDetail,
                            )
                        }
                        composable<Screen.Cart> {
                            CartScreen(navigateToCheckout = navigateToCheckout)
                        }
                        composable<Screen.Categories> {
                            MoreScreen(
                                isLoggedIn = state.isLoggedIn,
                                isAdmin = state.isAdmin,
                                isVendor = state.isVendor,
                                userName = state.userName,
                                userPhone = state.userPhone,
                                onLoginClick = navigateToAuth,
                                onLogout = { viewModel.handleIntent(MainIntent.SignOut) },
                                onEditProfileClick = navigateToProfile,
                                onCustomerClubClick = navigateToCustomerClub,
                                onSupportClick = navigateToContactUs,
                                onSettingsClick = navigateToSettings,
                                onAdminPanelClick = navigateToAdminPanel,
                                onBecomeVendorClick = navigateToBecomeVendor,
                                onAdminShopsClick = navigateToAdminShops,
                                onChatsClick = navigateToChats,
                                onBookmarksClick = navigateToBookmarks,
                                onMarketOrdersClick = navigateToMarketOrders,
                                onDealsClick = navigateToDeals,
                                onActivityClick = navigateToActivity,
                                onWalletClick = navigateToWallet,
                                onReferralClick = navigateToReferral,
                                onFeaturesClick = navigateToFeatures,
                                onVendorPanelClick = navigateToVendorPanel,
                                onAdminManageClick = navigateToAdminManage,
                            )
                        }
                    }

                    // نوارِ پایین فقط روی موبایل؛ روی نمایشگرهای بزرگ نوارِ کناری جایگزین است.
                    if (!isLarge) {
                        HorizontalDivider(color = AppTheme.colors.line)
                        BottomBar(
                            cartItemCount = state.cartItemCount,
                            selected = selectedDestination,
                            onSelect = onSelectDestination
                        )
                    }
                }
            }
        }
        }
    }
}
