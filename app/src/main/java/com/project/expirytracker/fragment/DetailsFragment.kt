package com.project.expirytracker.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.navArgs
import com.project.expirytracker.R
import com.project.expirytracker.databinding.FragmentDetailsBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import com.project.expirytracker.db.AppDatabase
import com.project.expirytracker.db.DatabaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = args.item
        binding.itemTypeTV.text = item.type
        binding.itemName.text = item.name
        binding.quantityTV.text = item.quantity.toString()
        binding.mfgDateTV.text = "${item.mfgDate}-${item.mfgMonth}-${item.mfgYear}"
        binding.expDateTV.text = "${item.expDate}-${item.expMonth}-${item.expYear}"
        binding.priceTV.text = "Rs. ${item.itemPrice}"
        binding.reducedPriceTV.text = "Rs. ${item.itemPrice}"

        val fromDate = LocalDate.of(item.expYear.toInt(),item.expMonth.toInt(),item.expDate.toInt())
        timeDifference(fromDate)

        var soldQuantity:Short
        var oldQuantity:Short = item.quantity
        var addQuantity:Short
        binding.quantity.setOnClickListener{
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.alert_quantity,null)

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Edit Quantity")
            builder.setMessage("Enter the Quantity Sold or Purchase\n\nTotal Quantity: $oldQuantity")
            builder.setIcon(android.R.drawable.ic_dialog_alert)


            builder.setView(dialogLayout)
            builder.setPositiveButton("Sold"){dialogInterface, which ->
                soldQuantity = Integer.parseInt(dialogLayout.findViewById<EditText>(R.id.quantity).text.toString()).toShort()
                val sub = oldQuantity.minus(soldQuantity).toShort()

                CoroutineScope(Dispatchers.IO).launch {
                    item.quantity = sub
                    updateQ(item)
                }
                Toast.makeText(context, "$sub", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Purchase"){dialogInterface, which ->
                addQuantity = Integer.parseInt(dialogLayout.findViewById<EditText>(R.id.quantity).text.toString()).toShort()
                val add = oldQuantity.plus(addQuantity).toShort()
                CoroutineScope(Dispatchers.IO).launch {
                    item.quantity = add
                    updateQ(item)
                }
                Toast.makeText(context, "$add", Toast.LENGTH_SHORT).show()

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun timeDifference(fromDate: LocalDate) {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val dateTemp = formatter.format(time)
        val formatterT = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val Date = LocalDate.parse(dateTemp, formatterT)
        val toDate = LocalDate.of(Date.year,Date.month,Date.dayOfMonth)
        val daysDifference = ChronoUnit.DAYS.between(toDate,fromDate)

        Toast.makeText(context, "$daysDifference", Toast.LENGTH_SHORT).show()

    }

    private suspend fun updateQ(item: DatabaseModel){
        val db = AppDatabase.getDatabase(requireContext()).databaseDao()
        db.up(item)
    }
}