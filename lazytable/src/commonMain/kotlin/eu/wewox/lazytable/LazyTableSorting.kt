package eu.wewox.lazytable

/**
 * Represents the sorting state for a column in the LazyTable.
 */
public enum class SortOrder {
    NONE,
    ASCENDING,
    DESCENDING
}

/**
 * Represents the sorting state for all columns in the LazyTable.
 *
 * @property currentSortColumn The index of the currently sorted column, or null if no column is sorted.
 * @property sortOrder The current sort order for the sorted column.
 */
public data class LazyTableSortState(
    val currentSortColumn: Int? = null,
    val sortOrder: SortOrder = SortOrder.NONE
) {
    /**
     * Clears the current sorting state.
     *
     * @return A new LazyTableSortState with no sorting applied.
     */
    public fun clearSorting(): LazyTableSortState = LazyTableSortState()

    /**
     * Sets a specific column to be sorted with the given order.
     *
     * @param columnIndex The index of the column to sort.
     * @param order The sort order to apply.
     * @return A new LazyTableSortState with the specified sorting.
     */
    public fun setSorting(columnIndex: Int, order: SortOrder): LazyTableSortState = LazyTableSortState(columnIndex, order)
}

/**
 * A helper function that allows adding sorting capability to a LazyTable.
 *
 * @param T The type of data being displayed in the table.
 * @param items The list of items to be displayed in the table.
 * @param sortState The current sorting state of the table.
 * @param sortFunction Function that sorts the input list based on the column index and sort order.
 * @return The sorted list of items.
 */
public fun <T> applySorting(
    items: List<T>,
    sortState: LazyTableSortState,
    sortFunction: (List<T>, Int, SortOrder) -> List<T>
): List<T> {
    return if (sortState.currentSortColumn != null && sortState.sortOrder != SortOrder.NONE) {
        sortFunction(items, sortState.currentSortColumn, sortState.sortOrder)
    } else {
        items
    }
}