package top.rechinx.meow.module.login

import android.content.Context
import top.rechinx.meow.manager.LoginManager
import top.rechinx.meow.manager.PreferenceManager
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.module.base.BasePresenter
import top.rechinx.meow.support.log.L

class LoginPresenter(var mContext: Context): BasePresenter<LoginView>() {

    private lateinit var mSourceManager: SourceManager
    private lateinit var mPreferences: PreferenceManager
    private lateinit var mLoginManager: LoginManager

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
        mLoginManager = LoginManager.getInstance()
        mPreferences = PreferenceManager(mContext)
    }

    fun login(source: String, username: String, password: String) {
        mCompositeDisposable.add(mSourceManager.getSource(source).login(username, password).subscribe ({
            L.d(it.auth)
            mLoginManager.insert(it)
            mPreferences.putBoolean(source, true)
            mView?.onLoginSuccess()
        }, {
            L.d(it.message)
            mPreferences.putBoolean(source, false)
            mView?.onLoginFailured()
        }))
    }
}