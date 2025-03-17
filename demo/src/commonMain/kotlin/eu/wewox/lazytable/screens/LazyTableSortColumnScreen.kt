package eu.wewox.lazytable.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import eu.wewox.lazytable.Example
import eu.wewox.lazytable.LazyTable
import eu.wewox.lazytable.LazyTableDimensions
import eu.wewox.lazytable.LazyTableItem
import eu.wewox.lazytable.LazyTableSortState
import eu.wewox.lazytable.SortOrder
import eu.wewox.lazytable.ui.components.SortableHeaderCell
import eu.wewox.lazytable.applySorting
import eu.wewox.lazytable.data.Pokemon
import eu.wewox.lazytable.data.pokemons
import eu.wewox.lazytable.lazyTableDimensions
import eu.wewox.lazytable.lazyTablePinConfiguration
import eu.wewox.lazytable.ui.components.TopBar
import eu.wewox.lazytable.ui.extensions.formatToDecimals

/**
 * Example how to setup columns sorting.
 */
@Composable
fun LazyTableSortColumnScreen(
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(
                title = Example.LazyTableSort.label,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        val columns = 11
        val pokemonsData = produceState(initialValue = emptyList<Pokemon>()) { value = pokemons() }

        var showSortDialog by rememberSaveable { mutableStateOf(false) }
        var dialogColumnIndex by rememberSaveable { mutableStateOf(0) }
        var dialogColumnTitle by rememberSaveable { mutableStateOf("") }

        // track sort state
        var sortState by remember { mutableStateOf(LazyTableSortState()) }

        // create a sorted version of the Pokemon list
        val sortedPokemons = remember(pokemonsData.value, sortState) {
            applySorting(
                items = pokemonsData.value,
                sortState = sortState
            ) { items, columnIndex, sortOrder ->
                val comparator = columnComparators[columnIndex]
                if (comparator != null) {
                    val sortedList = items.sortedWith(comparator)
                    if (sortOrder == SortOrder.DESCENDING) {
                        sortedList.reversed()
                    } else {
                        sortedList
                    }
                } else {
                    items
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (pokemonsData.value.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyTable(
                        pinConfiguration = lazyTablePinConfiguration(columns = 0, rows = 1),
                        dimensions = dimensions(),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = sortedPokemons.size * columns,
                            layoutInfo = {
                                LazyTableItem(
                                    column = it % columns,
                                    row = it / columns + 1, // +1 for header row
                                )
                            },
                        ) {
                            Cell(pokemon = sortedPokemons[it / columns], column = it % columns)
                        }

                        // header cells with sorting
                        items(
                            count = columns,
                            layoutInfo = {
                                LazyTableItem(
                                    column = it % columns,
                                    row = 0,
                                )
                            },
                        ) {
                            val columnIndex = it
                            val isSortable = columnIndex != 1 // all columns except image (1) are sortable
                            val columnTitle = when (columnIndex) {
                                0 -> "Name"
                                1 -> "Img"
                                2 -> "Number"
                                3 -> "Height (cm)"
                                4 -> "Weight (kg)"
                                5 -> "Health"
                                6 -> "Attack"
                                7 -> "Defence"
                                8 -> "Sp. attack"
                                9 -> "Sp. defence"
                                10 -> "Speed"
                                else -> error("")
                            }

                            // determine if this column is currently sorted and what the sort order is
                            val currentSortOrder = if (sortState.currentSortColumn == columnIndex) {
                                sortState.sortOrder
                            } else {
                                SortOrder.NONE
                            }

                            SortableHeaderCell(
                                column = columnIndex,
                                headerText = columnTitle,
                                sortable = isSortable,
                                currentSortOrder = currentSortOrder,
                                onSortClicked = { clickedColumn ->
                                    if (isSortable) {
                                        dialogColumnIndex = clickedColumn
                                        dialogColumnTitle = columnTitle
                                        showSortDialog = true
                                    }
                                },
                            )
                        }
                    }
            }
            if (showSortDialog) {
                val isCurrentlySorted = sortState.currentSortColumn == dialogColumnIndex
                val currentSortOrderForColumn = if (isCurrentlySorted) sortState.sortOrder else SortOrder.NONE

                AlertDialog(
                    onDismissRequest = { showSortDialog = false },
                    title = { Text(text = "Sort by $dialogColumnTitle") },
                    text = {
                        Column(Modifier.selectableGroup()) {
                            SortDialogRadioItem(
                                text = "Ascending",
                                selected = currentSortOrderForColumn == SortOrder.ASCENDING,
                                onClick = {
                                    sortState = sortState.setSorting(dialogColumnIndex, SortOrder.ASCENDING)
                                    showSortDialog = false
                                }
                            )
                            SortDialogRadioItem(
                                text = "Descending",
                                selected = currentSortOrderForColumn == SortOrder.DESCENDING,
                                onClick = {
                                    sortState = sortState.setSorting(dialogColumnIndex, SortOrder.DESCENDING)
                                    showSortDialog = false
                                }
                            )
                            SortDialogRadioItem(
                                text = "No sorting",
                                selected = currentSortOrderForColumn == SortOrder.NONE,
                                onClick = {
                                    sortState = sortState.clearSorting()
                                    showSortDialog = false
                                }
                            )
                        }
                    },
                    confirmButton = { },
                    dismissButton = {
                        TextButton(
                            onClick = { showSortDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

        }
    }
}

private val columnComparators: Map<Int, Comparator<Pokemon>> = mapOf(
    0 to compareBy { it.name },
    2 to compareBy { it.number },
    3 to compareBy { it.height },
    4 to compareBy { it.weight },
    5 to compareBy { it.stats.health },
    6 to compareBy { it.stats.attack },
    7 to compareBy { it.stats.defence },
    8 to compareBy { it.stats.specialAttack },
    9 to compareBy { it.stats.specialDefence },
    10 to compareBy { it.stats.speed }
)

private fun dimensions(): LazyTableDimensions =
    lazyTableDimensions(
        columnSize = {
            when (it) {
                0 -> 148.dp
                1 -> 48.dp
                else -> 96.dp
            }
        },
        rowSize = {
            if (it == 0) {
                32.dp
            } else {
                48.dp
            }
        }
    )

@Composable
private fun SortDialogRadioItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null because we're handling click on the row
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Suppress("ComplexMethod")
@Composable
private fun Cell(
    pokemon: Pokemon,
    column: Int
) {
    val content = when (column) {
        0 -> pokemon.name
        1 -> "" // Second column is reserved for an image
        2 -> pokemon.number
        3 -> pokemon.height.formatToDecimals()
        4 -> pokemon.weight.formatToDecimals()
        5 -> pokemon.stats.health.toString()
        6 -> pokemon.stats.attack.toString()
        7 -> pokemon.stats.defence.toString()
        8 -> pokemon.stats.specialAttack.toString()
        9 -> pokemon.stats.specialDefence.toString()
        10 -> pokemon.stats.speed.toString()
        else -> error("Unknown column index: $column")
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .border(Dp.Hairline, MaterialTheme.colorScheme.onSurface)
    ) {
        if (content.isNotEmpty()) { Text(text = content) }
        if (column == 1) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(pokemon.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
            )
        }
    }
}
