package top.rechinx.meow.manager

import top.rechinx.meow.dao.AppDatabase
import top.rechinx.meow.model.Login

class LoginManager {

    private var mDatabaseHelper: AppDatabase = AppDatabase.getInstance()

    fun insert(login: Login) {
        mDatabaseHelper.loginDao().insert(login)
    }

    fun isLogin(name: String): Boolean {
        if(mDatabaseHelper.loginDao().isLogin(name) == null) return false
        else return mDatabaseHelper.loginDao().isLogin(name)!! == 1
    }

    fun update(login: Login) {
        mDatabaseHelper.loginDao().update(login)
    }

    fun getAuth(name: String): String {
        return mDatabaseHelper.loginDao().auth(name)!!
    }

    fun identify(name: String): Login? {
        return mDatabaseHelper.loginDao().identify(name)
    }

    companion object {

        private var instance:LoginManager ?= null

        fun getInstance(): LoginManager {
            return instance ?: synchronized(this) {
                instance ?: LoginManager().also { instance = it }
            }
        }
    }
}