package tw0reck1.paperview

import android.content.Context
import android.util.TypedValue

internal object PaperUtils {

    internal fun Context.dpToPx(dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}
