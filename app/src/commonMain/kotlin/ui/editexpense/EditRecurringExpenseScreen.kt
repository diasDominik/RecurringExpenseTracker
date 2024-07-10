package ui.editexpense

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import recurringexpensetracker.app.generated.resources.Res
import recurringexpensetracker.app.generated.resources.edit_expense_button_add
import recurringexpensetracker.app.generated.resources.edit_expense_button_delete
import recurringexpensetracker.app.generated.resources.edit_expense_button_save
import recurringexpensetracker.app.generated.resources.edit_expense_title
import ui.theme.ExpenseTrackerTheme
import viewmodel.EditRecurringExpenseViewModel

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun EditRecurringExpenseScreen(
    expenseId: Int?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditRecurringExpenseViewModel = koinViewModel { parametersOf(expenseId) },
) {
    val scrollState = rememberScrollState()
    val localFocusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.edit_expense_title),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        content = { paddingValues ->
            Column(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState),
            ) {
                NameOption(
                    name = viewModel.nameState,
                    onNameChange = { viewModel.nameState = it },
                    nameInputError = viewModel.nameInputError.value,
                    onNext = { localFocusManager.moveFocus(FocusDirection.Next) },
                )
                DescriptionOption(
                    description = viewModel.descriptionState,
                    onDescriptionChange = { viewModel.descriptionState = it },
                    onNext = { localFocusManager.moveFocus(FocusDirection.Next) },
                )
                PriceOption(
                    price = viewModel.priceState,
                    onPriceChange = { viewModel.priceState = it },
                    priceInputError = viewModel.priceInputError.value,
                    onNext = { localFocusManager.moveFocus(FocusDirection.Next) },
                )
                RecurrenceOption(
                    everyXRecurrence = viewModel.everyXRecurrenceState,
                    onEveryXRecurrenceChange = { viewModel.everyXRecurrenceState = it },
                    everyXRecurrenceInputError = viewModel.everyXRecurrenceInputError.value,
                    selectedRecurrence = viewModel.selectedRecurrence,
                    onSelectRecurrence = { viewModel.selectedRecurrence = it },
                    onNext = { localFocusManager.clearFocus() },
                )
                FirstPaymentOption(
                    date = viewModel.firstPaymentDate,
                    onSelectDate = { viewModel.firstPaymentDate = it },
                )
                ColorOption(
                    expenseColor = viewModel.expenseColor,
                    onSelectExpenseColor = { viewModel.expenseColor = it },
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(align = Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                            .navigationBarsPadding()
                            .imePadding(),
                ) {
                    if (viewModel.showDeleteButton) {
                        OutlinedButton(
                            onClick = {
                                viewModel.deleteExpense()
                                onDismiss()
                            },
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error,
                                ),
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .wrapContentWidth(),
                        ) {
                            Text(
                                text = stringResource(Res.string.edit_expense_button_delete),
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                    }
                    Button(
                        onClick = {
                            viewModel.updateExpense { successful ->
                                if (successful) {
                                    onDismiss()
                                }
                            }
                        },
                        modifier =
                            Modifier
                                .weight(1f)
                                .wrapContentWidth(),
                    ) {
                        Text(
                            text =
                                stringResource(
                                    if (viewModel.isNewExpense) {
                                        Res.string.edit_expense_button_add
                                    } else {
                                        Res.string.edit_expense_button_save
                                    },
                                ),
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }
            }
        },
    )
}

@Preview
@Composable
private fun EditRecurringExpensePreview() {
    ExpenseTrackerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
//            EditRecurringExpenseInternal(
//                onUpdateExpense = {},
//                confirmButtonString = "Add Expense",
//            )
        }
    }
}
