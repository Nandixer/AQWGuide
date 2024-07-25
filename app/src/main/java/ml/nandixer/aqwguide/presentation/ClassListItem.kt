package ml.nandixer.aqwguide.presentation

import android.graphics.Point
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ml.nandixer.aqwguide.R
import ml.nandixer.aqwguide.domain.model.CombatClass
import ml.nandixer.aqwguide.domain.model.Dps
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalTextApi::class
)
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
                    if (viewModel.compareClass.value == theClass) {
                        viewModel.chooseComparison(null)
                        Toast
                            .makeText(context, "Deselected for Comparison", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        viewModel.chooseComparison(theClass)
                        Toast
                            .makeText(context, "Selected for Comparison", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            )


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(){

                val labelFontSize = if (viewModel.sortValueLength.value == 1) 24.sp else 18.sp
                val labelRemainingSpace = if (viewModel.sortValueLength.value == 1) .95f else .90f

                Text(theClass.names[0],
                    fontSize = 24.sp,
                    color = if (isExpanded && viewModel.compareClass.value!= null && viewModel.compareClass.value != theClass) Color.Blue
                        else (if (theClass == viewModel.compareClass.value && !isExpanded) Color.Red
                                else (textColor())),
                    modifier = Modifier.fillMaxWidth(labelRemainingSpace)
                )
                if (!isExpanded){

                    val letter = viewModel.getClassSortLabel(theClass)

                    Text(letter, fontSize = labelFontSize)
                }
            }
            if (altNames.isNotEmpty()){
                Text(text = altNames, fontSize = 12.sp, fontStyle = FontStyle.Italic)
            }
            if (theClass.best?.isNotEmpty() == true){
                Text(text = "Best ${theClass.best} class", fontSize = 12.sp)
            }
            val drawColor = textColor().copy(alpha = 0.3f)
            val drawColorStrong = textColor()
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                theClass.description?.let { Text(text = it, fontSize = 12.sp) }

                Canvas(modifier = Modifier
                    .fillMaxWidth(.7f)
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally)){

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


                    if (viewModel.compareClass.value != null && viewModel.compareClass.value != theClass){
                        drawPath(path, color = Color(0xFF0000BB))
                        drawPath(path, color = Color(0xFF0000FF), style= Stroke(4.dp.toPx()))
                    } else {
                        drawPath(path, color = drawColor)
                        drawPath(path, color = drawColorStrong, style= Stroke(4.dp.toPx()))
                    }


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

                    drawLine(color = drawColor,
                        Offset(cX, cY-size.height/2),
                        Offset(cX, cY+size.height/2),
                        strokeWidth = 4f
                    )

                    drawLine(color = drawColor,
                        Offset(cX+size.width*sqrt(3f)/2f/2, cY-size.height/2/2),
                        Offset(cX-size.width* sqrt(3f)/2f/2, cY+size.height/2/2),
                        strokeWidth = 4f
                    )

                    drawLine(color = drawColor,
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
                            color = drawColor,
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

                    Enhancements(text = enh.weapon, "Weapon")
                    Enhancements(text = enh.armor, "Armor")
                    Enhancements(text = enh.helm, "Helm")
                    Enhancements(text = enh.cape, "Cape")

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.background(color = if(isSystemInDarkTheme()) Color.Black else Color.White)
                    ) {
                        // damage tests
                        val otherDPS = if (viewModel.compareClass.value != null && viewModel.compareClass.value != theClass) viewModel.compareClass.value!!.maxPerformance() else Dps(null, null, null, null, null, null)

                        Measurement("Classhall DPS", dps.classhall , dps.classhallNsod, otherDPS.classhall, otherDPS.classhallNsod)

                        if ((dps.classhall != null || dps.classhallNsod != null) && (dps.revenant != null || dps.revenantNsod != null)){
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Measurement("Revenant KPM", dps.revenant, dps.revenantNsod, otherDPS.revenant, otherDPS.revenantNsod)

                        if ((dps.revenant != null || dps.revenantNsod != null) && (dps.icestorm != null || dps.icestormNsod != null)){
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Measurement("Icestormarena KPM", dps.icestorm, dps.icestormNsod, otherDPS.icestorm, otherDPS.icestormNsod)

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

                        val doWeHaveLoadableIcons = theClass.icons?.let {
                            listOf(it.s1, it.s1, it.s3, it.s4, it.s5).all { it != null }
                        }?:false

                        if (doWeHaveLoadableIcons){
                            Row(
                                modifier = Modifier.fillMaxWidth().height(70.dp),
                                horizontalArrangement = Arrangement.Absolute.Left,
                            ){
                                val spellNumbers = line.split(" ")

                                val mapping = mapOf(
                                    "1" to theClass.icons?.s1,
                                    "2" to theClass.icons?.s2,
                                    "3" to theClass.icons?.s3,
                                    "4" to theClass.icons?.s4,
                                    "5" to theClass.icons?.s5,
                                    "6" to "http://aqwwiki.wdfiles.com/local--files/image-tags/Passive_Skill.png"

                                )

                                for (spell in spellNumbers){
                                    Box(
                                        contentAlignment = Alignment.Center
                                    ){
                                        AsyncImage(
                                            model = mapping[spell],
                                            contentDescription = "Sample Image",
                                            modifier = Modifier.height(60.dp)
                                        )
                                        // of the two identical texts,
                                        // one is responsible for the black stroke
                                        // the other for the white fill
                                        Text(spell,
                                            textAlign = TextAlign.Center,

                                            style = TextStyle.Default.copy(
                                                fontSize = 40.sp,
                                                drawStyle = Stroke(
                                                    miter = 10f,
                                                    width = 20f,
                                                    join = StrokeJoin.Round,
                                                )
                                            ),
                                            color = Color.Black
                                        )
                                        Text(spell,
                                            textAlign = TextAlign.Center,
                                            fontSize = 40.sp,
                                            color=Color.White
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(line, fontSize = 24.sp)
                        }

                    }else{
                        Text(line)
                    }

                }

                if (theClass.video != null || !theClass.videos.isNullOrEmpty()){
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // todo make these clickable
                theClass.video?.let { Text(text = theClass.video, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                for (video in theClass.videos?: listOf()){
                    Text(text = video, fontSize=12.sp)
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
fun Enhancements(text: String, hint: String? = null){

    var imageVector = when(text){
        "Arcana" -> R.drawable.arcana
        "Vim" -> R.drawable.vim
        "Vainglory" -> R.drawable.vainglory
        "Penitence" -> R.drawable.penitence
        "Dauntless" -> R.drawable.dauntless
        "Health Vamp (Luck)" -> R.drawable.lucky_awe_health
        "Mana Vamp (Luck)" -> R.drawable.lucky_awe_mana
        "Spiral Carve (Luck)" -> R.drawable.lucky_awe_spiral
        "Awe Blast (Luck)" -> R.drawable.lucky_awe_blast
        "Powerword Die (Luck)" -> R.drawable.lucky_awe_death
        "Luck" -> when(hint){
            "Weapon" -> R.drawable.lucky_weapon
            "Armor" -> R.drawable.lucky_armor
            "Helm" -> R.drawable.lucky_helm
            "Cape" -> R.drawable.lucky_cape
            else -> R.drawable.missing
        }
        "Forge" -> when(hint){
            "Helm" -> R.drawable.forge_helm
            else -> R.drawable.missing
        }
        else -> R.drawable.missing
    }

    Row(modifier = Modifier
        .background(Color.Black)
        .fillMaxWidth()
        .height(32.dp)
        .border(1.dp, Color.Gray)
        .padding(start = 8.dp)
        .wrapContentHeight(align = Alignment.CenterVertically)
    ){
        Image(imageVector = ImageVector.vectorResource(imageVector), contentDescription = "enh icon", modifier = Modifier.padding(end = 8.dp))

        Text(text = text,
            color = Color.White,


        )
    }
}

@Composable
fun Measurement(label: String, base: Int?, nsod: Int?, compBase: Int?, compNsod: Int?){

    val blue = Color(0xFF2266FF) // a bit brighter for contrast

    if (base != null) {
        Row {
            Text(text = "${label}:", color = if (compBase != null) blue else textColor())
            Spacer(modifier = Modifier.weight(1.0f))
            Text(base.toString(), color = if (compBase != null) blue else textColor())
        }
    }

    if (compBase != null) {
        Row {
            Text(text = "${label}:", color=Color.Red)
            Spacer(modifier = Modifier.weight(1.0f))
            Text(compBase.toString(), color=Color.Red)
        }
    }

    if (nsod != null) {
        Row {
            Text(text = "$label +51%:", color = if (compNsod != null) blue else textColor())
            Spacer(modifier = Modifier.weight(1.0f))
            if (base != null) {
                val percent =
                    nsod.toFloat() / base.toFloat()
                val growth = (percent * 100 - 100).toInt()
                Text(text = "+$growth% ", color = Color.Green)
            }
            Text(nsod.toString(), color = if (compNsod != null) blue else textColor())
        }
    }

    if (compNsod != null) {
        Row {
            Text(text = "$label +51%:", color=Color.Red)
            Spacer(modifier = Modifier.weight(1.0f))
            if (compBase != null) {
                val percent =
                    compNsod.toFloat() / compBase.toFloat()
                val growth = (percent * 100 - 100).toInt()
                Text(text = "+$growth% ", color = Color.Green)
            }
            Text(compNsod.toString(), color=Color.Red)
        }
    }

}
@Composable
fun textColor(): Color {
    return MaterialTheme.colorScheme.onSurface
}