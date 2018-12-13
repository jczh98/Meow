package top.rechinx.meow.ui.details.chapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_chapters_selection.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.global.Extras
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.base.BaseAdapter

class ChaptersActivity: BaseActivity(), BaseAdapter.OnItemClickListener {

    private val list by lazy { intent.getParcelableArrayListExtra<Chapter>(Extras.EXTRA_CHAPTERS) }

    private lateinit var adapter: ChaptersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters_selection)
        setSupportActionBar(customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        customToolbar.setNavigationOnClickListener { finish() }
        // For recycler view
        adapter = ChaptersAdapter(this, ArrayList(list
                .map { ChaptersAdapter.Switcher(it, false) }))
        adapter.setOnItemClickListener(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = null
        recyclerView.adapter = adapter
        // Click to return selected chapters
        fabDownload.setOnClickListener {
            val list = ArrayList<Chapter>()
            for (item in adapter.datas) {
                if(!item.element.download && item.enable) {
                    list.add(item.element)
                }
            }
            if(list.isEmpty()) {

            } else {
                val intent = Intent()
                intent.putParcelableArrayListExtra(Extras.EXTRA_CHAPTERS, list)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onItemClick(view: View, position: Int) {
        val item = adapter.getItem(position)
        if(!item.element.download) {
            item.switchEnable()
            adapter.notifyItemChanged(position)
        }
    }

    companion object {

        fun createIntent(context: Context, list: ArrayList<Chapter>): Intent {
            val intent = Intent(context, ChaptersActivity::class.java)
            intent.putParcelableArrayListExtra(Extras.EXTRA_CHAPTERS, list)
            return intent
        }
    }
}