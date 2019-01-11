package top.rechinx.meow.core.source

import top.rechinx.meow.core.network.Http
import javax.inject.Inject

class Dependencies @Inject constructor(
        val http: Http
)