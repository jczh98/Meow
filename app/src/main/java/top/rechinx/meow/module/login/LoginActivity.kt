package top.rechinx.meow.module.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.OnClick
import top.rechinx.meow.R
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.module.result.ResultActivity
import top.rechinx.meow.support.log.L

class LoginActivity: BaseActivity(), LoginView {

    private lateinit var mPresenter: LoginPresenter
    private lateinit var source: String

    @BindView(R.id.login_layout) lateinit var mLayout: RelativeLayout
    @BindView(R.id.username_input) lateinit var mUserNameInput: EditText
    @BindView(R.id.password_input) lateinit var mPasswordInput: EditText
    @BindView(R.id.custom_progress_bar) lateinit var mProgress: ProgressBar

    override fun initData() {
        source = intent.getStringExtra(EXTRA_SOURCE)
        supportActionBar?.title = getString(R.string.title_activity_login) + SourceManager.getInstance().getSource(source).title
    }

    override fun initView() {
        mToolbar?.setNavigationOnClickListener { finish() }
        mProgress.visibility = View.GONE
    }

    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initPresenter() {
        mPresenter = LoginPresenter(this)
        mPresenter.attachView(this)
    }

    @OnClick(R.id.login_btn) fun login() {
        // force to hide soft keyboard
        var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mUserNameInput.windowToken, 0) //强制隐藏键盘
        if(mUserNameInput.text.toString().isEmpty() || mPasswordInput.text.toString().isEmpty()) {
            Snackbar.make(mLayout, getString(R.string.snackbar_login_check), Snackbar.LENGTH_SHORT).show()
            return
        }
        mProgress.visibility = View.VISIBLE
        mPresenter.login(source, mUserNameInput.text.toString(), mPasswordInput.text.toString())
    }

    override fun onLoginSuccess() {
        var intent = Intent()
        intent.putExtra("data", 1)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onLoginFailured() {
        var intent = Intent()
        intent.putExtra("data", 2)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {

        const val EXTRA_SOURCE = "extra_source"

        fun createIntent(context: Context, source: String): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra(EXTRA_SOURCE, source)
            return intent
        }

    }

}