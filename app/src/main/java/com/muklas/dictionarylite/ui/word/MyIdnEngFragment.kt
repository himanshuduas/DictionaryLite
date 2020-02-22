package com.muklas.dictionarylite.ui.word


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.muklas.dictionarylite.R
import com.muklas.dictionarylite.adapter.ListAdapter
import com.muklas.dictionarylite.database.MyIdnEngHelper
import com.muklas.dictionarylite.helper.MappingHelper
import com.muklas.dictionarylite.model.Word
import com.muklas.dictionarylite.ui.DetailActivity.Companion.IDNENG
import kotlinx.android.synthetic.main.fragment_my_idn_eng.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class MyIdnEngFragment : Fragment() {

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"

        private lateinit var myIdnEngHelper: MyIdnEngHelper
        private lateinit var adapter: ListAdapter
        fun loadWordsAsync() {
            GlobalScope.launch(Dispatchers.Main) {
                val deferredResult = async(Dispatchers.IO) {
                    val cursor = myIdnEngHelper.queryAll()
                    MappingHelper.mapCursorToArrayList(cursor)
                }
                val result = deferredResult.await()
                if (result.isNotEmpty()) {
                    adapter.setData(result)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_idn_eng, container, false)
        myIdnEngHelper = MyIdnEngHelper(context as Context) //initialize helper
        myIdnEngHelper.open() //open the database

        //handle configuration change (screen orientation)
        if (savedInstanceState == null) {
            loadWordsAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Word>(EXTRA_STATE)
            if (list != null) {
                adapter.setData(list)
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListAdapter()
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickCallback(object : ListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Word) {
                val optionDialogFragment = OptionDialogFragment(data, IDNENG)
                optionDialogFragment.show(childFragmentManager, "TAG")
            }
        })
        rvResult.layoutManager = LinearLayoutManager(context)
        rvResult.adapter = adapter

        loadWordsAsync()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.list)
    }

}