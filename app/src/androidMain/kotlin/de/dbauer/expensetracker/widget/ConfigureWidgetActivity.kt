package de.dbauer.expensetracker.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import de.dbauer.expensetracker.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.database.UserPreferencesRepository
import org.koin.android.ext.android.get
import recurringexpensetracker.app.generated.resources.Res
import recurringexpensetracker.app.generated.resources.biometric_prompt_manager_title
import recurringexpensetracker.app.generated.resources.cancel
import security.BiometricPromptManager
import security.BiometricPromptManager.BiometricResult
import ui.theme.ExpenseTrackerTheme
import org.jetbrains.compose.resources.stringResource as jetbrainsStringResource

class ConfigureWidgetActivity : AppCompatActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private val userPreferencesRepository = get<UserPreferencesRepository>()
    private val biometricPromptManager: BiometricPromptManager by lazy { BiometricPromptManager(this) }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_CANCELED, resultValue)

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        lifecycleScope.launch {
            biometricPromptManager.promptResult.collectLatest {
                when (it) {
                    is BiometricResult.AuthenticationSuccess -> {
                        launch {
                            userPreferencesRepository.biometricSecurity.save(false)
                        }
                    }
                    else -> {
                        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        setResult(RESULT_CANCELED, resultValue)
                        finish()
                        return@collectLatest
                    }
                }
            }
        }

        setContent {
            val biometricPromptTitle = jetbrainsStringResource(Res.string.biometric_prompt_manager_title)
            val biometricCancel = jetbrainsStringResource(Res.string.cancel)
            ExpenseTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (userPreferencesRepository.biometricSecurity.collectAsState().value) {
                            Text(text = stringResource(R.string.widget_configuration_biometric))
                            Button(
                                onClick = {
                                    biometricPromptManager.showBiometricPrompt(
                                        title = biometricPromptTitle,
                                        cancel = biometricCancel,
                                    )
                                },
                            ) {
                                Text(text = stringResource(R.string.widget_configuration_biometric_deactivate))
                            }
                        } else {
                            Button(
                                onClick = {
                                    val resultValue = Intent().putExtra(
                                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                                        appWidgetId,
                                    )
                                    setResult(RESULT_OK, resultValue)
                                    finish()
                                },
                            ) {
                                // TODO do something else
                                Text("Hello World")
                            }
                        }
                    }
                }
            }
        }
    }
}
