package eu.wewox.lazytable.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import eu.wewox.lazytable.SortOrder

/**
 * A reusable header cell component that displays sorting indicators and handles sort events.
 *
 * @param column The column index.
 * @param headerText The text to display in the header.
 * @param sortable Whether the column is sortable.
 * @param currentSortOrder The current sort order for this column.
 * @param onSortClicked Callback for when the header is clicked for sorting.
 */
@Composable
fun SortableHeaderCell(
    column: Int,
    headerText: String,
    sortable: Boolean = true,
    currentSortOrder: SortOrder = SortOrder.NONE,
    onSortClicked: (Int) -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .border(Dp.Hairline, MaterialTheme.colorScheme.onPrimary)
            .then(
                if (sortable) {
                    Modifier.clickable { onSortClicked(column) }
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dp.Hairline)
        ) {
            Text(text = headerText)

            if (sortable && currentSortOrder != SortOrder.NONE) {
                Icon(
                    imageVector = when (currentSortOrder) {
                        SortOrder.ASCENDING -> Icons.Default.ArrowUpward
                        SortOrder.DESCENDING -> Icons.Default.ArrowDownward
                        else -> Icons.Default.ArrowUpward
                    },
                    contentDescription = null,
                )
            }
        }
    }
}