package com.example.todoapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.model.Todo
import kotlinx.coroutines.flow.*


enum class TodoFilter {
    ALL,
    ACTIVE,
    COMPLETED
}


class TodoViewModel: ViewModel() {


    private val _todos = MutableStateFlow<List<Todo>>(emptyList())


    private val _filterState = MutableStateFlow(TodoFilter.ALL)
    val filterState: StateFlow<TodoFilter> = _filterState


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery


    fun setFilter(filter: TodoFilter) {
        _filterState.value = filter
    }


    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }


    val todos: StateFlow<List<Todo>> =
        combine(_todos, _filterState, _searchQuery) { todos, filter, query ->


            val filteredList = when (filter) {
                TodoFilter.ALL -> todos
                TodoFilter.ACTIVE -> todos.filter { !it.isDone }
                TodoFilter.COMPLETED -> todos.filter { it.isDone }
            }


            if (query.isBlank()) {
                filteredList
            } else {
                filteredList.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    val activeTaskCount: StateFlow<Int> = _todos.map { list ->
        list.count { !it.isDone }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    val completedTaskCount: StateFlow<Int> = _todos.map { list ->
        list.count { it.isDone }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    fun addTask(title: String) {
        val nextId = (_todos.value.maxOfOrNull { it.id } ?: 0) + 1
        val newTask = Todo(id = nextId, title = title)
        _todos.value = _todos.value + newTask
    }


    fun toggleTask(id: Int) {
        _todos.value = _todos.value.map { t ->
            if (t.id == id) t.copy(isDone = !t.isDone) else t
        }
    }


    fun deleteTask(id: Int) {
        _todos.value = _todos.value.filterNot { it.id == id }
    }
}
