package com.project.expirytracker.fragment

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.ContactsContract.Intents.Insert.ACTION
import android.provider.LiveFolders.INTENT
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.expirytracker.R
import com.project.expirytracker.db.AppDatabase
import com.project.expirytracker.db.DatabaseModel
import com.project.expirytracker.databinding.FragmentAddItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

public var path:String? = null
class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            getActivity()?.onBackPressedDispatcher?.onBackPressed()
        }

        return binding.root
    }

    var mfgDate: Array<Int>? = null
    var expDate: Array<Int>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        var filePath:String? = null
//        binding.csvPath.setOnClickListener{
//            val intent = Intent(Intent.ACTION_GET_CONTENT).setType("*/*")
//            @Suppress("DEPRECATION")
//            startActivityForResult(intent,10)
//        }
//        readCSV()

        binding.enterMfg.setOnClickListener {
            datePicker(1)
        }

        binding.expDate.setOnClickListener {
            datePicker(2)
        }
        binding.addItemButton.setOnClickListener {
            val itemName = updateTIL(binding.enterItemName, binding.itemNameTIL)
            val itemType:String = updateTIL(binding.enterType,binding.itemNameTIL1)
            val itemQuantity:Short = Integer.parseInt(updateTIL(binding.enterQuantity,binding.itemNameTIL2)).toShort()
            val price:Short = Integer.parseInt(updateTIL(binding.enterPrice,binding.itemNameTIL5)).toShort()

            if(itemName != "0" && itemType != "0" && itemQuantity != 0.toShort() && price != 0.toShort()
                && !binding.enterMfg.text.isNullOrBlank() && !binding.expDate.text.isNullOrBlank()) {
                val databaseModel =
                    DatabaseModel(
                        name = itemName,
                        type = itemType,
                        quantity = itemQuantity,
                        mfgYear = mfgDate!![0].toShort(),
                        mfgMonth = mfgDate!![1].toByte(),
                        mfgDate = mfgDate!![2].toByte(),
                        expYear = expDate!![0].toShort(),
                        expMonth = expDate!![1].toByte(),
                        expDate = expDate!![2].toByte(),
                        itemPrice = price
                    )

                CoroutineScope(Dispatchers.IO).launch {
                    insertDatabase(databaseModel)
                }

                findNavController().navigate(R.id.action_addItemFragment_to_homeFragment)
            } else {
                binding.mfgDateTIL.error = "Cannot be Empty"
                binding.expDateTIL.error = "Cannot be Empty"
            }

        }
    }


//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if(requestCode == 10){
//            if(resultCode == RESULT_OK){
//                path = data.toString()
//                var line = ""
//                var displayData = ""
//                while(reader.)
////                Toast.makeText(requireContext(), path, Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }
    private fun updateTIL(TIET: TextInputEditText, TIL: TextInputLayout): String {
        var result = "0"
        if(!TIET.text.isNullOrBlank()) {
            result = TIET.text.toString()
        } else {
            TIL.error = "Cannot be Empty"
        }
        return result
    }


    fun datePicker(x: Int){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DATE)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                if (x == 1) {
                    mfgDate =arrayOf(year,monthOfYear+1,dayOfMonth)
                    binding.enterMfg.setText(dat)
                }
                if (x == 2){
                    expDate = arrayOf(year,monthOfYear+1,dayOfMonth)
                    binding.expDate.setText(dat)
                }
            },
            year,
            month,
            date
        )
        datePickerDialog.show()
    }

    private suspend fun insertDatabase(data: DatabaseModel) {
        val database = AppDatabase.getDatabase(requireContext())
        database.databaseDao().insert(data)
    }


//    private fun readCSV(){
//        val file: File = File("food_attribute.csv")
//        val rows: List<List<String>> = csvReader().readAll(file)
//    }
}