package top.rechinx.meow.ui.catalogs

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import top.rechinx.meow.R

class CatalogsFragment : Fragment() {

    companion object {
        fun newInstance() = CatalogsFragment()
    }

    private lateinit var viewModel: CatalogsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalogs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CatalogsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
