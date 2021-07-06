package de.dertyp7214.rboardthememanager.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.dertyp7214.rboardthememanager.R

inline val Activity.content: View
    get() {
        return findViewById(android.R.id.content)
    }

operator fun <T : ViewModel> FragmentActivity.get(modelClass: Class<T>): T =
    run(::ViewModelProvider)[modelClass]

fun Activity.openUrl(url: String) {
    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

fun Activity.openDialog(
    message: String,
    title: String,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setCancelable(false)
        .setMessage(message)
        .setTitle(title)
        .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> positive(dialogInterface) }
        .apply {
            if (negative != null) setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                negative.invoke(
                    dialogInterface
                )
            }
        }
        .create().also { it.show() }
}

fun Activity.openDialog(
    @StringRes message: Int,
    @StringRes title: Int,
    cancelable: Boolean = false,
    negative: ((dialogInterface: DialogInterface) -> Unit)? = { it.dismiss() },
    positive: (dialogInterface: DialogInterface) -> Unit
): AlertDialog = openDialog(getString(message), getString(title), cancelable, negative, positive)

fun Activity.openShareThemeDialog(
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, name: String, author: String) -> Unit
) = openDialog(R.layout.share_popup, false) { dialog ->
    val nameInput = findViewById<EditText>(R.id.editTextName)
    val authorInput = findViewById<EditText>(R.id.editTextAuthor)

    findViewById<Button>(R.id.ok)?.setOnClickListener {
        positive(
            dialog,
            nameInput?.text?.toString() ?: "Shared Pack",
            authorInput?.text?.toString() ?: "Rboard Theme Manager"
        )
    }
    findViewById<Button>(R.id.cancel)?.setOnClickListener {
        negative(dialog)
    }
}

@SuppressLint("InflateParams")
fun Activity.openInputDialog(
    @StringRes hint: Int,
    negative: ((dialogInterface: DialogInterface) -> Unit) = { it.dismiss() },
    positive: (dialogInterface: DialogInterface, text: String) -> Unit
) = openDialog(R.layout.loading_dialog, false) { dialog ->
    val input = findViewById<EditText>(R.id.editText)
    input.setHint(hint)

    findViewById<Button>(R.id.ok)?.setOnClickListener {
        positive(dialog, input?.text?.toString() ?: "")
    }
    findViewById<Button>(R.id.cancel)?.setOnClickListener { negative(dialog) }
}

fun Activity.openLoadingDialog(@StringRes message: Int) =
openDialog(R.layout.loading_dialog, false) {
    findViewById<TextView>(R.id.message).setText(message)
}

fun Activity.openDialog(
    @LayoutRes layout: Int,
    cancelable: Boolean = true,
    block: View.(DialogInterface) -> Unit
): AlertDialog {
    val view = layoutInflater.inflate(layout, null)
    return MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setView(view)
        .create().also { dialog ->
            block(view, dialog)
            dialog.show()
        }
}