package com.example.easy.dialogs

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.easy.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {

    val dialog = BottomSheetDialog(requireContext())
    val view = layoutInflater.inflate(R.layout.reset_possword_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()


    val etEmail = view.findViewById<EditText>(R.id.etPassword)
    val btnCancel = view.findViewById<Button>(R.id.btnCancel)
    val btnSend = view.findViewById<Button>(R.id.btnSend)

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }
    btnSend.setOnClickListener {
        val email = etEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }
}