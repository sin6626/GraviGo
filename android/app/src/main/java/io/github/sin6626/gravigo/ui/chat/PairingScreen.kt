package io.github.sin6626.gravigo.ui.chat

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.i18n.LanguageSelector
import io.github.sin6626.gravigo.R

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import io.github.sin6626.gravigo.ui.demo.AccentBlue
import io.github.sin6626.gravigo.ui.demo.Ink
import io.github.sin6626.gravigo.ui.demo.RoundIconButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun PairingScreen(
    isPairing: Boolean,
    error: String?,
    currentGatewayUrl: String? = null,
    onBack: (() -> Unit)? = null,
    onPair: (String, String) -> Unit,
) {
    var uri by rememberSaveable { mutableStateOf("") }
    var customUrl by rememberSaveable { mutableStateOf("") }
    val scanner = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let { uri = it }
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        verticalArrangement = if (onBack == null) Arrangement.Center else Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        if (onBack != null) {
            Spacer(Modifier.height(18.dp))
            RoundIconButton("arrow_back", appString(R.string.msg_572cf45ba436), onBack, size = 48.dp)
            Spacer(Modifier.height(28.dp))
        }
        LanguageSelector()
        Spacer(Modifier.height(16.dp))
        Text(if (onBack == null) appString(R.string.msg_6b6731b3a2be) else appString(R.string.msg_85cb0c8e9314), color = Ink, fontSize = 29.sp)
        Spacer(Modifier.height(14.dp))
        Text(appString(R.string.msg_5bdc68dd1b59), color = Color(0xFF666666),
            fontSize = 16.sp, lineHeight = 24.sp)
        if (!currentGatewayUrl.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(appString(R.string.msg_2f424ccdbb41, currentGatewayUrl), color = Color(0xFF666666), fontSize = 14.sp)
        }
        Spacer(Modifier.height(28.dp))
        OutlinedTextField(
            value = uri,
            onValueChange = { uri = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(appString(R.string.msg_1d1f1ced8899)) },
            placeholder = { Text(appString(R.string.msg_ab3143aff4e5)) },
            shape = RoundedCornerShape(18.dp),
            maxLines = 3,
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = customUrl,
            onValueChange = { customUrl = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(appString(R.string.msg_e0fb201616ca)) },
            placeholder = { Text(appString(R.string.msg_f2365c99a70d)) },
            shape = RoundedCornerShape(18.dp),
            singleLine = true,
        )
        Spacer(Modifier.height(9.dp))
        Text(appString(R.string.msg_bce73f6b5f3a),
            color = Color(0xFF666666), fontSize = 13.sp, lineHeight = 20.sp)
        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(error, color = Color(0xFFB3261E), fontSize = 14.sp)
        }
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = { onPair(uri, customUrl) },
            enabled = !isPairing,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
        ) { Text(if (isPairing) appString(R.string.msg_8be0ea36a9bb) else appString(R.string.msg_206b73957c9f)) }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                scanner.launch(ScanOptions()
                    .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    .setPrompt(appString(R.string.msg_ba61ce41e1b6))
                    .setBeepEnabled(false))
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) { Text(appString(R.string.msg_ac78fcc82ee5)) }
    }
}
