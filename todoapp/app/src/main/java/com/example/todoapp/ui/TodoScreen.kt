package com.example.todoapp.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
// IMPORT YANG HILANG:
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// IMPORT BARU (BEST PRACTICE):
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.viewmodel.TodoViewModel


@Composable
fun TodoScreen(vm: TodoViewModel = viewModel()) {
    // Diubah untuk best practice
    val todos by vm.todos.collectAsStateWithLifecycle()
    var text by rememberSaveable { mutableStateOf("") }


    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tambah tugas...") },
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


        Divider()


        LazyColumn {
            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    onToggle = { vm.toggleTask(todo.id) },
                    onDelete = { vm.deleteTask(todo.id) }
                )
            }
        }
    }
}
