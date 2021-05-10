package com.example.lesson03.dagger


import com.example.lesson03.fragments.FavoritesFragment
import com.example.lesson03.fragments.FilmsFragment
import com.example.lesson03.fragments.ReminderAddFragment
import com.example.lesson03.fragments.ReminderListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeFirstFragment(): FilmsFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoritesFirstFragment(): FavoritesFragment

    @ContributesAndroidInjector
    abstract fun contributeReminderFirstFragment(): ReminderListFragment

    @ContributesAndroidInjector
    abstract fun contributeReminderAddFragment(): ReminderAddFragment
}