package com.kazemieh.bazaar

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bazaarModule = module {
    viewModel { RastehHomeViewModel(repository = get()) }
    viewModel { BecomeVendorViewModel(repository = get()) }
    viewModel { AdminShopsViewModel(repository = get()) }
}
