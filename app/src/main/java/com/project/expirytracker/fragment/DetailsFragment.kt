package com.project.expirytracker.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.project.expirytracker.R
import com.project.expirytracker.databinding.FragmentDetailsBinding
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

        var soldQuantity:Short
        var oldQuantity:Short = item.quantity
        var addQuantity:Short
        binding.quantity.setOnClickListener{
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.alert_quantity,null)

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Edit Quantity")
            builder.setMessage("Enter the Quantity Sold or Purchase\n Total Quantity: $oldQuantity")
            builder.setIcon(android.R.drawable.ic_dialog_alert)


            builder.setView(dialogLayout)
            builder.setPositiveButton("Sold"){dialogInterface, which ->
                soldQuantity = Integer.parseInt(dialogLayout.findViewById<EditText>(R.id.quantity).text.toString()).toShort()
                val sub = oldQuantity.minus(soldQuantity).toShort()
                var temp = ""
                CoroutineScope(Dispatchers.IO).launch {
                    item.quantity = sub
                    temp = updateQ(item).toString()
                }
                Toast.makeText(context, "$sub", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Purchase"){dialogInterface, which ->
                addQuantity = Integer.parseInt(dialogLayout.findViewById<EditText>(R.id.quantity).text.toString()).toShort()
                val add = oldQuantity.plus(addQuantity).toShort()
                CoroutineScope(Dispatchers.IO).launch {
                    val x = updateQ(item)
                }
                Toast.makeText(context, "$add", Toast.LENGTH_SHORT).show()

            }
            val alertDialog: AlertDialog = builder.create()
//            alertDialog.setCancelable(false)
            alertDialog.show()
        }

    }

    private suspend fun updateQ(item: DatabaseModel){
        val db = AppDatabase.getDatabase(requireContext()).databaseDao()
        db.up(item)
    }
}