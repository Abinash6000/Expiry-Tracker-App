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
import kotlinx.coroutines.withContext

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: DetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            getActivity()?.onBackPressedDispatcher?.onBackPressed()
        }

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
        binding.priceTV.text = "₹ ${item.itemPrice}"
        val fromDate = LocalDate.of(item.expYear.toInt(),item.expMonth.toInt(),item.expDate.toInt())
        val red = reducePrice(item.itemPrice,item.arrayData,item.quantity,fromDate)
        binding.reducedPriceTV.text = "₹ $red"
//        timeDifference(fromDate)

        var soldQuantity:Int =0
        var oldQuantity:Short = item.quantity
        var addQuantity:Short
        binding.quantityTV.setOnClickListener{
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.alert_quantity,null)

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Edit Quantity")
            builder.setMessage("Enter the Quantity Sold or Purchase\n\nTotal Quantity: ${item.quantity}")
            builder.setIcon(R.drawable.ic_dialog)

            builder.setView(dialogLayout)
            builder.setPositiveButton("Sold"){dialogInterface, which ->
                soldQuantity = Integer.parseInt(dialogLayout.findViewById<EditText>(R.id.quantity).text.toString())

                val sub = oldQuantity.minus(soldQuantity).toShort()


//                val fromDate = LocalDate.of(item.expYear.toInt(),item.expMonth.toInt(),item.expDate.toInt())

                var arrTemp = item.arrayData
                arrTemp += soldQuantity
                CoroutineScope(Dispatchers.IO).launch {
                    item.quantity = sub
                    item.arrayData = arrTemp
                    updateQ(item)
                }
//                Toast.makeText(context, "$sub", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val getData = fetchDatabase(requireContext())
                    itemList.clear()
                    itemList.addAll(getData)
                    withContext(Dispatchers.Main){
                        adapter?.notifyDataSetChanged()
                    }
                }

                binding.quantityTV.text = item.quantity.toString()
                binding.reducedPriceTV.text = "₹ $red"

            }

            builder.setNegativeButton("Purchase"){dialogInterface, which ->
                addQuantity = Integer.parseInt(dialogLayout.findViewById<EditText>(R.id.quantity).text.toString()).toShort()
                val add = oldQuantity.plus(addQuantity).toShort()
                CoroutineScope(Dispatchers.IO).launch {
                    item.quantity = add
                    updateQ(item)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    val getData = fetchDatabase(requireContext())
                    itemList.clear()
                    itemList.addAll(getData)
                    withContext(Dispatchers.Main){
                        adapter?.notifyDataSetChanged()
                    }
                }

                binding.quantityTV.text = item.quantity.toString()
                binding.reducedPriceTV.text = "₹ $red"
//                Toast.makeText(context, "$add", Toast.LENGTH_SHORT).show()

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }


//        Price Reduce Algo

    }

    private suspend fun updateQ(item: DatabaseModel){
        val db = AppDatabase.getDatabase(requireContext()).databaseDao()
        db.up(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reducePrice(
        price: Short,
        arrayData: Array<Int>,
        quantity: Short,
        fromDate: LocalDate
    ): Any {
        val x = soldTrack(quantity,fromDate,arrayData).toInt()
        val temp = x*2
        if(x>0 && temp<50){
            return price - price*temp/100
        }
        else{
            return price
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
public fun soldTrack(q: Short, fromDate: LocalDate, arrayData: Array<Int>,):Float{
    var RR:Float = q/timeDifference(fromDate).toFloat()

    var temp = 0
    arrayData.forEach {
        temp += it
    }
    if(arrayData.size ==0){
        return RR
    }else{
        val CRR = (temp/arrayData.size).toFloat()
        return RR-CRR
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat")
private fun timeDifference(fromDate: LocalDate):Long {
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("dd-MM-yyyy")
    val dateTemp = formatter.format(time)
    val formatterT = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val Date = LocalDate.parse(dateTemp, formatterT)
    val toDate = LocalDate.of(Date.year,Date.month,Date.dayOfMonth)
    val daysDifference = ChronoUnit.DAYS.between(toDate,fromDate)
    return daysDifference
}