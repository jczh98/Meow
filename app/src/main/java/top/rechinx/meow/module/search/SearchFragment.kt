package top.rechinx.meow.module.search

import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import top.rechinx.meow.R
import top.rechinx.meow.module.base.BaseFragment
import top.rechinx.meow.module.result.ResultActivity
import top.rechinx.meow.source.Dmzj

class SearchFragment: BaseFragment() {

    @BindView(R.id.main_text_layout) lateinit var mInputLayout: TextInputLayout

    @BindView(R.id.main_keyword_input) lateinit var mEditText: EditText

    @BindView(R.id.main_search_btn) lateinit var mSearchBtn: FloatingActionButton

    @OnClick(R.id.main_search_btn) fun onSearchBtnClick() {
        val keyword = mEditText.text.toString()
        if (keyword.isEmpty()) {
            mInputLayout.error = getString(R.string.empty_for_search)
        } else {
            startActivity(ResultActivity.createIntent(this!!.activity!!, keyword, intArrayOf(Dmzj.TYPE)))
        }
    }

    override fun initView() {
        mEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                mInputLayout.error = null
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        mEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mSearchBtn.performClick()
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun initPresenter() {

    }

    override fun getLayoutId(): Int = R.layout.fragment_search

}