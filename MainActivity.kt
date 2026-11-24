package com.hifztracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.hifztracker.app.data.HifzDatabase
import com.hifztracker.app.data.ParaRevisionEntity
import com.hifztracker.app.data.SabaqEntity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = HifzDatabase.get(this).hifzDao()

        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MaterialTheme(
                    colorScheme = lightColorScheme(
                        primary = Color(0xFF0D4A36),
                        secondary = Color(0xFFC49A3A),
                        background = Color(0xFFF6F7F5)
                    )
                ) {
                    val scope = rememberCoroutineScope()
                    val sabaqList by db.observeSabaq().collectAsState(initial = emptyList())
                    val sabqiList by db.observeRevisions("sabqi").collectAsState(initial = emptyList())
                    val manzilList by db.observeRevisions("manzil").collectAsState(initial = emptyList())

                    var tab by remember { mutableIntStateOf(0) }
                    var startPage by remember { mutableStateOf("") }
                    var endPage by remember { mutableStateOf("") }
                    var selectedPara by remember { mutableIntStateOf(1) }

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, label = { Text("سبق") }, icon = {})
                                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, label = { Text("سبقی") }, icon = {})
                                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, label = { Text("منزل") }, icon = {})
                            }
                        }
                    ) { padding ->
                        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                            Text("HIFZ TRACKER — ذاتی حفظ ٹریکر", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF0D4A36))
                            Spacer(modifier = Modifier.height(12.dp))

                            if (tab == 0) {
                                OutlinedTextField(value = startPage, onValueChange = { startPage = it }, label = { Text("ابتدائی صفحہ") }, modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(value = endPage, onValueChange = { endPage = it }, label = { Text("اختتامی صفحہ") }, modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(onClick = {
                                    val s = startPage.toIntOrNull()
                                    val e = endPage.toIntOrNull()
                                    if (s != null && e != null && s >= 1 && e >= s) {
                                        scope.launch {
                                            db.upsertSabaq(SabaqEntity(UUID.randomUUID().toString(), LocalDate.now().toString(), s, e, e - s + 1, null, null, System.currentTimeMillis()))
                                            startPage = ""
                                            endPage = ""
                                        }
                                    }
                                }, modifier = Modifier.fillMaxWidth()) {
                                    Text("سبق محفوظ کریں")
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyColumn {
                                    items(sabaqList) { item ->
                                        Text("${item.date}: صفحہ ${item.startPage} تا ${item.endPage} (${item.pagesCount} صفحات)", modifier = Modifier.padding(vertical = 4.dp))
                                    }
                                }
                            } else {
                                val type = if (tab == 1) "sabqi" else "manzil"
                                val list = if (tab == 1) sabqiList else manzilList
                                Text("پارہ نمبر منتخب کریں: $selectedPara (1 تا 30)")
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { if (selectedPara > 1) selectedPara-- }) { Text("-") }
                                    Button(onClick = { if (selectedPara < 30) selectedPara++ }) { Text("+") }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        scope.launch {
                                            db.upsertRevision(ParaRevisionEntity(UUID.randomUUID().toString(), type, LocalDate.now().toString(), selectedPara, "completed", null, null, System.currentTimeMillis()))
                                        }
                                    }) { Text("مکمل") }
                                    OutlinedButton(onClick = {
                                        scope.launch {
                                            db.upsertRevision(ParaRevisionEntity(UUID.randomUUID().toString(), type, LocalDate.now().toString(), selectedPara, "not_completed", null, null, System.currentTimeMillis()))
                                        }
                                    }) { Text("نامکمل") }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                LazyColumn {
                                    items(list) { item ->
                                        val statusLabel = if (item.status == "completed") "مکمل" else "نامکمل"
                                        Text("${item.date}: پارہ ${item.paraNumber} — $statusLabel", modifier = Modifier.padding(vertical = 4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
