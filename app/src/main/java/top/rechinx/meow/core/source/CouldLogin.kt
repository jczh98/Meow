package top.rechinx.meow.core.source

import io.reactivex.Observable
import okhttp3.Response

interface CouldLogin {

    fun isLogged(): Boolean

    fun login(username: String, password: String): Observable<Boolean>

    fun isAuthenticationSuccessful(response: Response): Boolean

}