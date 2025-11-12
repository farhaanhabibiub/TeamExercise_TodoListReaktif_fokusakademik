package com.example.todoapp.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.viewmodel.TodoFilter
import com.example.todoapp.viewmodel.TodoViewModel


@Composable
fun TodoScreen(vm: TodoViewModel = viewModel()) {


   val todos by vm.todos.collectAsStateWithLifecycle()
   val searchQuery by vm.searchQuery.collectAsStateWithLifecycle()
   val filterState by vm.filterState.collectAsStateWithLifecycle()
   val activeCount by vm.activeTaskCount.collectAsStateWithLifecycle()
   val completedCount by vm.completedTaskCount.collectAsStateWithLifecycle()


   var text by rememberSaveable { mutableStateOf("") }


   Column(Modifier.padding(16.dp)) {


       OutlinedTextField(
           value = searchQuery,
           onValueChange = { vm.setSearchQuery(it) },
           label = { Text("Cari tugas...") },
           modifier = Modifier.fillMaxWidth(),
           singleLine = true
       )


       Spacer(Modifier.height(8.dp))


       OutlinedTextField(
           value = text,
           onValueChange = { text = it },
           label = { Text("Tambah tugas baru...") },
           modifier = Modifier.fillMaxWidth()
       )


       Button(
           onClick = {
               if (text.isNotBlank()) {
                   vm.addTask(text.trim())
                   text = ""
               }
           },
           modifier = Modifier.padding(vertical = 8.dp)
       ) { Text("Tambah") }


       Row(
           modifier = Modifier.fillMaxWidth(),
           horizontalArrangement = Arrangement.SpaceEvenly
       ) {
           Text("Aktif: $activeCount", fontWeight = FontWeight.Bold)
           Text("Selesai: $completedCount", fontWeight = FontWeight.Bold)
       }


       val filterOptions = TodoFilter.values()
       TabRow(
           selectedTabIndex = filterOptions.indexOf(filterState),
           modifier = Modifier.padding(vertical = 8.dp)
       ) {
           filterOptions.forEach { filter ->
               Tab(
                   selected = filterState == filter,
                   onClick = { vm.setFilter(filter) },
                   text = { Text(filter.name) }               )
           }
       }


       Divider()


       LazyColumn {
           items(todos, key = { it.id }) { todo ->
               TodoItem(
                   todo = todo,
                   onToggle = { vm.toggleTask(todo.id) },
                   onDelete = { vm.deleteTask(todo.id) }
               )
           }
       }
   }
}
