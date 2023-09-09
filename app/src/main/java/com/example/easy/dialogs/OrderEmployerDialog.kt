package com.example.easy.dialogs

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.easy.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar

@SuppressLint("InflateParams")
fun Fragment.setupBottomSheetOrderDialog(
    onSendClick: (String, String, String) -> Unit
) {

    val dialog = BottomSheetDialog(requireContext())
    val view = layoutInflater.inflate(R.layout.order_employer_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()


    val etDate = view.findViewById<EditText>(R.id.etDate)
    val etLocation = view.findViewById<AutoCompleteTextView>(R.id.etLocation)
    val etDescription = view.findViewById<EditText>(R.id.etDescription)
    val btnCancel = view.findViewById<Button>(R.id.btnCancel)
    val btnSend = view.findViewById<Button>(R.id.btn_order)

    val locationOptions = resources.getStringArray(R.array.jobs_loacations)
    val locationAdapter = ArrayAdapter(
        requireContext(),
        android.R.layout.simple_dropdown_item_1line,
        locationOptions
    )
    etLocation.setAdapter(locationAdapter)

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
    btnSend.setOnClickListener {
        val date = etDate.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val description = etDescription.text.toString().trim()
        onSendClick(date, location, description)
        dialog.dismiss()
    }
    fun formatDate(selectedYear: Int, selectedMonth: Int, selectedDay: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth, selectedDay)
        return "${calendar.get(Calendar.YEAR)}-${selectedMonth + 1}-${selectedDay}"
    }

    fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = formatDate(selectedYear, selectedMonth, selectedDay)
                editText.setText(selectedDate)

            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
    etDate.setOnClickListener {
        showDatePicker(etDate)
    }





}