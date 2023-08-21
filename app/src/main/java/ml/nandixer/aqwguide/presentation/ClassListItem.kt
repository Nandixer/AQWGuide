package ml.nandixer.aqwguide.presentation

import android.graphics.Point
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ml.nandixer.aqwguide.domain.model.CombatClass
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ClassListItem(theClass: CombatClass, viewModel: MainViewModel){
    val ratings = theClass.ratings
    val altNames = theClass.names.drop(1).joinToString (" | ")
    val isExpanded = theClass.abbr == viewModel.chosenClass.value
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .animateContentSize(
                animationSpec = tween(
                    easing = LinearOutSlowInEasing
                )
            )
            .combinedClickable(
                onClick = {
                    viewModel.chooseClass(theClass.abbr)
                },
                onLongClick = {
                    if (viewModel.compareClass.value == theClass){
                        viewModel.chooseComparison(null)
                        Toast.makeText(context, "Deselected for Comparison", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.chooseComparison(theClass)
                        Toast.makeText(context, "Selected for Comparison", Toast.LENGTH_SHORT).show()
                    }

                }
            )


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(theClass.names[0], fontSize = 24.sp, color = if (isExpanded) Color.Blue else (if (theClass == viewModel.compareClass.value) Color.Red else Color.White) )
            if (altNames.isNotEmpty()){
                Text(text = altNames, fontSize = 12.sp, fontStyle = FontStyle.Italic)
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                Canvas(modifier = Modifier.fillMaxWidth(.7f).aspectRatio(1f).align(Alignment.CenterHorizontally)){

                    val letterToDist = mapOf("S" to 6f/6, "A" to 5f/6, "B" to 4f/6, "C" to 3f/6, "D" to 2f/6, "E" to 1f/6, "F" to 0f/6).withDefault { 0f/6 }

                    val cY = size.height/2
                    val cX = size.width/2

                    // this is the same as below
                    // except for opacity
                    // todo move
                    if (viewModel.compareClass.value != null && viewModel.compareClass.value != theClass){
                        val otherRatings = viewModel.compareClass.value!!.ratings
                        val path = Path().apply {
                            moveTo(cX, cY-size.height/2* letterToDist[otherRatings.damage]!!)
                            lineTo(cX+size.width*sqrt(3f)/2f/2*letterToDist[otherRatings.survival]!!, cY-size.height/2/2*letterToDist[otherRatings.survival]!!)
                            lineTo(cX+size.width*sqrt(3f)/2f/2*letterToDist[otherRatings.farming]!!, cY+size.height/2/2*letterToDist[otherRatings.farming]!!)
                            lineTo(cX, cY+size.height/2*letterToDist[otherRatings.pvp]!!)
                            lineTo(cX-size.width* sqrt(3f)/2f/2*letterToDist[otherRatings.ultras]!!, cY+size.height/2/2*letterToDist[otherRatings.ultras]!!)
                            lineTo(cX-size.width* sqrt(3f)/2f/2*letterToDist[otherRatings.support]!!, cY-size.height/2/2*letterToDist[otherRatings.support]!!)
                            lineTo(cX, cY-size.height/2*letterToDist[otherRatings.damage]!!)
                        }

                        drawPath(path, color = Color(0xFFBB0000)) // opacity here and line below
                        drawPath(path, color = Color(0xFFFF0000), style= Stroke(4.dp.toPx()))
                    }
                    // until here

                    val path = Path().apply {
                        moveTo(cX, cY-size.height/2* letterToDist[ratings.damage]!!)
                        lineTo(cX+size.width*sqrt(3f)/2f/2*letterToDist[ratings.survival]!!, cY-size.height/2/2*letterToDist[ratings.survival]!!)
                        lineTo(cX+size.width*sqrt(3f)/2f/2*letterToDist[ratings.farming]!!, cY+size.height/2/2*letterToDist[ratings.farming]!!)
                        lineTo(cX, cY+size.height/2*letterToDist[ratings.pvp]!!)
                        lineTo(cX-size.width* sqrt(3f)/2f/2*letterToDist[ratings.ultras]!!, cY+size.height/2/2*letterToDist[ratings.ultras]!!)
                        lineTo(cX-size.width* sqrt(3f)/2f/2*letterToDist[ratings.support]!!, cY-size.height/2/2*letterToDist[ratings.support]!!)
                        lineTo(cX, cY-size.height/2*letterToDist[ratings.damage]!!)
                    }

                    drawPath(path, color = Color.LightGray)
                    drawPath(path, color = Color.White, style= Stroke(4.dp.toPx()))

                    // this is the thing below
                    if (viewModel.compareClass.value != null && viewModel.compareClass.value != theClass){
                        val otherRatings = viewModel.compareClass.value!!.ratings
                        val path = Path().apply {
                            moveTo(cX, cY-size.height/2* letterToDist[otherRatings.damage]!!)
                            lineTo(cX+size.width*sqrt(3f)/2f/2*letterToDist[otherRatings.survival]!!, cY-size.height/2/2*letterToDist[otherRatings.survival]!!)
                            lineTo(cX+size.width*sqrt(3f)/2f/2*letterToDist[otherRatings.farming]!!, cY+size.height/2/2*letterToDist[otherRatings.farming]!!)
                            lineTo(cX, cY+size.height/2*letterToDist[otherRatings.pvp]!!)
                            lineTo(cX-size.width* sqrt(3f)/2f/2*letterToDist[otherRatings.ultras]!!, cY+size.height/2/2*letterToDist[otherRatings.ultras]!!)
                            lineTo(cX-size.width* sqrt(3f)/2f/2*letterToDist[otherRatings.support]!!, cY-size.height/2/2*letterToDist[otherRatings.support]!!)
                            lineTo(cX, cY-size.height/2*letterToDist[otherRatings.damage]!!)
                        }

                        drawPath(path, color = Color(0x88BB0000))
                        drawPath(path, color = Color(0x88FF0000), style= Stroke(4.dp.toPx()))
                    }
                    // until here

                    drawLine(color = Color.White,
                        Offset(cX, cY-size.height/2),
                        Offset(cX, cY+size.height/2),
                        strokeWidth = 4f
                    )

                    drawLine(color = Color.White,
                        Offset(cX+size.width*sqrt(3f)/2f/2, cY-size.height/2/2),
                        Offset(cX-size.width* sqrt(3f)/2f/2, cY+size.height/2/2),
                        strokeWidth = 4f
                    )

                    drawLine(color = Color.White,
                        Offset(cX+size.width*sqrt(3f)/2f/2, cY+size.height/2/2),
                        Offset(cX-size.width* sqrt(3f)/2f/2, cY-size.height/2/2),
                        strokeWidth = 4f
                    )

                    for (mult in 1..6){
                        val m = mult/6f

                        drawPoints(
                            points = listOf(

                                Offset(cX, cY-size.height/2*m),
                                Offset(cX+size.width*sqrt(3f)/2f/2*m, cY-size.height/2/2*m),
                                Offset(cX+size.width*sqrt(3f)/2f/2*m, cY+size.height/2/2*m),
                                Offset(cX, cY+size.height/2*m),
                                Offset(cX-size.width* sqrt(3f)/2f/2*m, cY+size.height/2/2*m),
                                Offset(cX-size.width* sqrt(3f)/2f/2*m, cY-size.height/2/2*m),
                                Offset(cX, cY-size.height/2*m),

                            ),
                            pointMode = PointMode.Polygon,
                            color = Color.White.copy(alpha = 0.7f),
                            strokeWidth = 4f

                        )

                    }
                }

                Text(text = "Damage: ${ratings.damage}, Survival: ${ratings.survival}, Support: ${ratings.support}")
                Text(text = "Farming: ${ratings.farming}, PvP: ${ratings.pvp}, Ultras: ${ratings.ultras}")

                Spacer(modifier = Modifier.height(16.dp))

                for (enh in theClass.enhancements) {
                    val dps = enh.dps

                    if (enh.name != null){
                        Text(enh.name)
                    }

                    Enhancements(text = enh.weapon)
                    Enhancements(text = enh.armor)
                    Enhancements(text = enh.helm)
                    Enhancements(text = enh.cape)

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.background(color = Color(0xFFDDDDDD))
                    ) {
                        // damage tests
                        if (dps.classhall != null) {
                            Row {
                                Text(text = "Classhall DPS:", color = Color.Black)
                                Spacer(modifier = Modifier.weight(1.0f))
                                Text(dps.classhall.toString(), color = Color.Black)
                            }
                        }

                        if (dps.classhallNsod != null) {
                            Row {
                                Text(text = "Classhall DPS +51%:", color = Color.Black)
                                Spacer(modifier = Modifier.weight(1.0f))
                                if (dps.classhall != null) {
                                    val percent =
                                        dps.classhallNsod!!.toFloat() / dps.classhall!!.toFloat()
                                    val growth = (percent * 100 - 100).toInt()
                                    Text(text = "+$growth% ", color = Color.Green)
                                }
                                Text(dps.classhallNsod.toString(), color = Color.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (dps.revenant != null) {
                            Row {
                                Text(text = "Revenant KPM:", color = Color.Black)
                                Spacer(modifier = Modifier.weight(1.0f))
                                Text(dps.revenant.toString(), color = Color.Black)
                            }
                        }

                        if (dps.revenantNsod != null) {
                            Row {
                                Text(text = "Revenant KPM +51%:", color = Color.Black)
                                Spacer(modifier = Modifier.weight(1.0f))
                                if (dps.revenant != null) {
                                    val percent =
                                        dps.revenantNsod!!.toFloat() / dps.revenant!!.toFloat()
                                    val growth = (percent * 100 - 100).toInt()
                                    Text(text = "+$growth% ", color = Color.Green)
                                }
                                Text(dps.revenantNsod.toString(), color = Color.Black)
                            }
                        }
                    }

                    if (enh != theClass.enhancements.last()) {
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val regex = "^[\\d\\s]+$".toRegex()
                for (line in theClass.combo){
                    if (regex.matches(line)){
                        Text(line, fontSize = 24.sp)
                    }else{
                        Text(line)
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    theClass.tags.joinToString(", "),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )

            }
        }
    }
}

@Composable
fun Enhancements(text: String){
    Text(text = text,
        color = Color.White,
        modifier = Modifier
            .background(Color.Black)
            .fillMaxWidth()
            .height(32.dp)
            .border(1.dp, Color.Gray)
            .padding(start = 8.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)

    )
}