package com.softartdev.notedelight.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.softartdev.annotation.Preview
import com.softartdev.mr.composeLocalized
import com.softartdev.notedelight.MR
import com.softartdev.notedelight.di.getViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.change.ChangeViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.confirm.ConfirmViewModel
import com.softartdev.notedelight.shared.presentation.settings.security.enter.EnterViewModel
import com.softartdev.notedelight.shared.presentation.title.EditTitleViewModel
import com.softartdev.notedelight.ui.dialog.security.ChangePasswordDialog
import com.softartdev.notedelight.ui.dialog.security.ConfirmPasswordDialog
import com.softartdev.notedelight.ui.dialog.security.EnterPasswordDialog
import com.softartdev.themepref.AlertDialog
import com.softartdev.themepref.DialogHolder

fun DialogHolder.showSaveChanges(saveNoteAndNavBack: () -> Unit, doNotSaveAndNavBack: () -> Unit) = showDialog {
    val saveCallback = prepareDismissCallback(doBefore = saveNoteAndNavBack)
    val notSaveCallback = prepareDismissCallback(doBefore = doNotSaveAndNavBack)
    SaveDialog(saveCallback, notSaveCallback, ::dismissDialog)
}

fun DialogHolder.showEditTitle(noteId: Long) = showDialog {
    val editTitleViewModel: EditTitleViewModel = getViewModel()
    EditTitleDialog(noteId, ::dismissDialog, editTitleViewModel)
}

fun DialogHolder.showDelete(onDeleteClick: () -> Unit) = showDialog {
    val deleteCallback = prepareDismissCallback(doBefore = onDeleteClick)
    DeleteDialog(deleteCallback, ::dismissDialog)
}

fun DialogHolder.showEnterPassword(doAfterDismiss: (() -> Unit)? = null) = showDialog {
    val viewModel: EnterViewModel = getViewModel()
    val dismissCallback = prepareDismissCallback(doAfter = doAfterDismiss)
    EnterPasswordDialog(dismissCallback, viewModel)
}

fun DialogHolder.showConfirmPassword(doAfterDismiss: (() -> Unit)? = null) = showDialog {
    val viewModel: ConfirmViewModel = getViewModel()
    val dismissCallback = prepareDismissCallback(doAfter = doAfterDismiss)
    ConfirmPasswordDialog(dismissCallback, viewModel)
}

fun DialogHolder.showChangePassword(doAfterDismiss: (() -> Unit)? = null) = showDialog {
    val viewModel: ChangeViewModel = getViewModel()
    val dismissCallback = prepareDismissCallback(doAfter = doAfterDismiss)
    ChangePasswordDialog(dismissCallback, viewModel)
}

private fun DialogHolder.prepareDismissCallback(
    doBefore: (() -> Unit)? = null,
    doAfter: (() -> Unit)? = null
): () -> Unit = {
    doBefore?.invoke()
    dismissDialog()
    doAfter?.invoke()
}

fun DialogHolder.showError(message: String?) = showDialog {
    ErrorDialog(message, ::dismissDialog)
}

@Composable
fun ErrorDialog(message: String?, dismissDialog: () -> Unit) = ShowDialog(
    title = MR.strings.error_title.composeLocalized(),
    text = message,
    onConfirm = dismissDialog,
    onDismiss = dismissDialog
)

@Composable
fun ShowDialog(title: String, text: String?, onConfirm: () -> Unit, onDismiss: () -> Unit) = AlertDialog(
    title = { Text(title) },
    text = text?.let { { Text(it) } },
    confirmButton = { Button(onClick = onConfirm) { Text(MR.strings.yes.composeLocalized()) } },
    dismissButton = { Button(onClick = onDismiss) { Text(MR.strings.cancel.composeLocalized()) } },
    onDismissRequest = onDismiss,
)

@Preview
@Composable
fun PreviewErrorDialog() = PreviewDialog { ErrorDialog("preview err") {} }

@Preview
@Composable
fun PreviewDialog(dialogContent: @Composable () -> Unit) =
    Box(modifier = Modifier.fillMaxSize()) { dialogContent() }