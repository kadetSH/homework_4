package com.example.lesson03.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lesson03.viewmodel.FavoritesFilmsViewModel
import com.example.lesson03.viewmodel.ReminderFilmsViewModel
import com.example.lesson03.viewmodel.RepoListFilmsViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(RepoListFilmsViewModel::class)
    abstract fun bindFirstFragmentViewModel(fragmentViewModel: RepoListFilmsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesFilmsViewModel::class)
    abstract fun bindFavoritesFragmentViewModel(favoritesFilmsViewModel: FavoritesFilmsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReminderFilmsViewModel::class)
    abstract fun bindReminderFragmentViewModel(favoritesFilmsViewModel: ReminderFilmsViewModel): ViewModel



    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
