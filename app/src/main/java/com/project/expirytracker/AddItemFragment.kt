package com.project.expirytracker

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.expirytracker.databinding.FragmentAddItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddItemFragment : Fragment() {
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_add_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mfgDate: Array<Int>? = null
        var expDate: Array<Int>? = null

        binding.enterMfg.setOnClickListener {
            val temp = datePicker(1)
            mfgDate = temp
        }

        binding.expDate.setOnClickListener {
            val temp = datePicker(2)
            expDate = temp
        }
        binding.addItemButton.setOnClickListener {
            var itemName = updateTIL(binding.enterItemName, binding.itemNameTIL)
            var itemType:String = updateTIL(binding.enterType,binding.itemNameTIL1)
            var itemQuantity:Short = Integer.parseInt(updateTIL(binding.enterQuantity,binding.itemNameTIL2)).toShort()
            var price:Short = Integer.parseInt(updateTIL(binding.enterPrice,binding.itemNameTIL5)).toShort()

            val listItem = listOf<DatabaseModel>(
                DatabaseModel(
                    id,
                    itemName,
                    itemType,
                    itemQuantity,
                    mfgDate!![0].toShort(),
                    mfgDate!![1].toByte(),
                    mfgDate!![2].toByte(),
                    expDate!![0].toShort(),
                    expDate!![1].toByte(),
                    expDate!![2].toByte(),
                    price
                )
            )
            CoroutineScope(Dispatchers.IO).launch {
                insertDatabase(listItem)
            }
        }
    }

    private fun updateTIL(TIET: TextInputEditText, TIL: TextInputLayout): String {
        var result = ""
        if(!TIET.text.isNullOrBlank()) {
            result = TIET.text.toString()
        } else {
            TIL.isErrorEnabled = true
        }
        return result
    }


    fun datePicker(x:Int): Array<Int> {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DATE)

        var temp = arrayOf(year, month, date)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                if(x == 1) binding.enterMfg.setText(dat)
                if(x ==2 ) binding.expDate.setText(dat)
            },
            year,
            month,
            date
        )
        datePickerDialog.show()
        return temp
    }

    private suspend fun insertDatabase(dataArray: List<DatabaseModel>) {
        val database = AppDatabase.getDatabase(requireContext())
        database.databaseDao().insertAll(dataArray)
    }
}