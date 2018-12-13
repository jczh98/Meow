package top.rechinx.meow.ui.extension

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_extension.*
import kotlinx.android.synthetic.main.custom_progress_bar.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.meow.R
import top.rechinx.meow.core.extension.model.Extension
import top.rechinx.meow.ui.base.BaseMvpActivity
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.mvp.MvpAppCompatActivity
import top.rechinx.rikka.mvp.factory.RequiresPresenter

@RequiresPresenter(ExtensionPresenter::class)
class ExtensionActivity: BaseMvpActivity<ExtensionPresenter>() {

    private lateinit var adapter: ExtensionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extension)


        setSupportActionBar(customToolbar)
        supportActionBar?.title = getString(R.string.drawer_extension)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        customToolbar.setNavigationOnClickListener { finish() }

        adapter = ExtensionAdapter(this, ArrayList())
        recyclerView.itemAnimator = null
        recyclerView.setHasFixedSize(true)
        adapter.getItemDecoration()?.let { recyclerView.addItemDecoration(it) }
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        customProgressBar.gone()
    }

    fun setInstalledExtensions(items: List<Extension>) {
        adapter.addAll(items)
    }

}