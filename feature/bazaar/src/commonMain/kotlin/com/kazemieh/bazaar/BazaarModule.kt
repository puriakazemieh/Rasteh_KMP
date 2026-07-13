package com.kazemieh.bazaar

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bazaarModule = module {
    viewModel { RastehHomeViewModel(repository = get()) }
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
}
