package com.kazemieh.bazaar

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bazaarModule = module {
    viewModel { RastehHomeViewModel(repository = get(), interaction = get(), recentlyViewed = get()) }
    viewModel { BecomeVendorViewModel(repository = get()) }
    viewModel { AdminShopsViewModel(repository = get()) }
    viewModel { RastehSearchViewModel(repository = get()) }
    viewModel { ShopDetailViewModel(repository = get(), interaction = get()) }
    viewModel { ChatThreadViewModel(interaction = get(), profileRepository = get()) }
    viewModel { ChatListViewModel(interaction = get()) }
    viewModel { BookmarksViewModel(interaction = get()) }
    viewModel { SearchViewModel(repository = get()) }
    viewModel { MyOrdersViewModel(repository = get()) }
    viewModel { DealsViewModel(repository = get()) }
    viewModel { PriceAlertsViewModel(repository = get()) }
    viewModel { GiftCardsViewModel(repository = get()) }
    viewModel { CommunityViewModel(repository = get()) }
    viewModel { NotificationsViewModel(repository = get()) }
    viewModel { AppointmentsViewModel(repository = get()) }
    viewModel { ReturnsViewModel(repository = get()) }
    viewModel { VipSubViewModel(repository = get()) }
    viewModel { WayfindViewModel(repository = get()) }
    viewModel { EventsViewModel(repository = get()) }
    viewModel { ReferralViewModel(repository = get()) }
    viewModel { WarrantyViewModel(repository = get()) }
    viewModel { ActivityViewModel(repository = get()) }
    viewModel { ParkingViewModel(repository = get()) }
    viewModel { LiveViewModel(repository = get()) }
    viewModel { EscrowViewModel(repository = get()) }
    viewModel { ProductDetailViewModel(repository = get(), interaction = get(), recentlyViewed = get()) }
    viewModel { CompareViewModel(repository = get()) }
    viewModel { VendorPanelViewModel(repository = get(), interaction = get(), advanced = get()) }
    viewModel { AdminManageViewModel(repository = get()) }
}
