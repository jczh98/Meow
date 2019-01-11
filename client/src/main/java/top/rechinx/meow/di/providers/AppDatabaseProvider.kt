package top.rechinx.meow.di.providers

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import toothpick.ProvidesSingletonInScope
import top.rechinx.meow.data.AppDatabase
import top.rechinx.meow.global.Constants
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@ProvidesSingletonInScope
class AppDatabaseProvider @Inject constructor(
        val context: Application
) : Provider<AppDatabase> {

    override fun get(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
    }
}