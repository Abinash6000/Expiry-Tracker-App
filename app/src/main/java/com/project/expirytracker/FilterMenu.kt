package com.project.expirytracker

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.project.expirytracker.fragment.sortByShortage
import com.project.expirytracker.fragment.sortByWaste

class FilterMenu {

    @RequiresApi(Build.VERSION_CODES.O)
    fun showMenu(context: Context, view: View) {
        val pop = PopupMenu(context,view)
        pop.inflate(R.menu.filter_menu)
        pop.setOnMenuItemClickListener {
            when(it!!.itemId){
                R.id.waste -> {
                    sortByWaste(context)
                    true
                }
                R.id.out -> {
                    sortByShortage(context)
                    true
                }
                else -> false

            }
        }
        try {
            val fieldMpop = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMpop.isAccessible = true
            val mPop = fieldMpop.get(pop)
            mPop.javaClass
                .getDeclaredMethod("filter",Boolean::class.java)
                .invoke(mPop,true)
        }
        catch(e:Exception) {

        }finally {
            pop.show()
        }
    }

}