package com.project.expirytracker.fragment

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Intents.Insert.ACTION
import android.provider.LiveFolders.INTENT
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.expirytracker.Notification
import com.project.expirytracker.R
import com.project.expirytracker.db.AppDatabase
import com.project.expirytracker.db.DatabaseModel
import com.project.expirytracker.databinding.FragmentAddItemBinding
import com.project.expirytracker.messageExtra
import com.project.expirytracker.notificationID
import com.project.expirytracker.titleExtra
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

public var path:String? = null
class AddItemFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    var mfgDate: Array<Int>? = null
    var expDate: Array<Int>? = null
    var item: DatabaseModel? = null
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

    @RequiresApi(Build.VERSION_CODES.O)
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
            val itemType: String = updateTIL(binding.enterType, binding.itemNameTIL1)
            val itemQuantity: Short =
                Integer.parseInt(updateTIL(binding.enterQuantity, binding.itemNameTIL2)).toShort()
            val price: Short =
                Integer.parseInt(updateTIL(binding.enterPrice, binding.itemNameTIL5)).toShort()

            if (itemName != "0" && itemType != "0" && itemQuantity != 0.toShort() && price != 0.toShort()
                && !binding.enterMfg.text.isNullOrBlank() && !binding.expDate.text.isNullOrBlank()
            ) {
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
                item = databaseModel

                CoroutineScope(Dispatchers.IO).launch {
                    insertDatabase(databaseModel)
                }

                scheduleNotification()

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
        if (!TIET.text.isNullOrBlank()) {
            result = TIET.text.toString()
        } else {
            TIL.error = "Cannot be Empty"
        }
        return result
    }


    fun datePicker(x: Int) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val date = c.get(Calendar.DATE)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                if (x == 1) {
                    mfgDate = arrayOf(year, monthOfYear + 1, dayOfMonth)
                    binding.enterMfg.setText(dat)
                }
                if (x == 2) {
                    expDate = arrayOf(year, monthOfYear + 1, dayOfMonth)
                    binding.expDate.setText(dat)
                }
            },
            year,
            month,
            date
        )
        datePickerDialog.show()
    }

    private fun scheduleNotification() {
        val intent = Intent(requireContext(), Notification::class.java)
        val title = item!!.name
        intent.putExtra(titleExtra, title)

        val pendingIntent2 = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time5 = getTime(-5)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time5,
            pendingIntent2
        )
        Toast.makeText(
            requireContext(),
            "Not Set: ${(time5 - Calendar.getInstance().timeInMillis)/ 1000} seconds from now",
            Toast.LENGTH_SHORT
        ).show()
    }

    public fun getTime(daysBefore: Int): Long {
        val calendar2 = Calendar.getInstance()
        val currentHour = calendar2.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar2.get(Calendar.MINUTE)
        val minute = currentMinute+1
        val hour = currentHour
        val day = expDate!![2]
        val month = expDate!![1]
        val year = expDate!![0]

        val calendar = Calendar.getInstance()
        calendar.set(year, month-1, day, hour, minute)
        calendar.add(Calendar.DAY_OF_MONTH, daysBefore)
        Toast.makeText(requireContext(), "$day-$month-$year-$hour-$minute", Toast.LENGTH_LONG).show()
        return calendar.timeInMillis
    }

    private suspend fun insertDatabase(data: DatabaseModel) {
        val database = AppDatabase.getDatabase(requireContext())
        database.databaseDao().insert(data)
    }
}
