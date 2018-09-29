package top.rechinx.meow.module.login

import top.rechinx.meow.module.base.BaseView

interface LoginView: BaseView {

    fun onLoginSuccess()

    fun onLoginFailured()
}