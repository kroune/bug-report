@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package ninemensmorrisappkmp.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.InternalResourceApi

private object CommonMainFont0 {
  public val roboto_mono: FontResource by 
      lazy { init_roboto_mono() }
}

@InternalResourceApi
internal fun _collectCommonMainFont0Resources(map: MutableMap<String, FontResource>) {
  map.put("roboto_mono", CommonMainFont0.roboto_mono)
}

internal val Res.font.roboto_mono: FontResource
  get() = CommonMainFont0.roboto_mono

private fun init_roboto_mono(): FontResource = org.jetbrains.compose.resources.FontResource(
  "font:roboto_mono",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/ninemensmorrisappkmp.composeapp.generated.resources/font/roboto_mono.xml", -1, -1),
    )
)
