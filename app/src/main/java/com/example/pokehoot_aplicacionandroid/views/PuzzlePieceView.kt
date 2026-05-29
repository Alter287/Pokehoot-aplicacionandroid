package com.example.pokehoot_aplicacionandroid.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * Vista personalizada que dibuja una pieza de puzzle real.
 * Las pestañas (tabs) salen hacia fuera y las muescas van hacia dentro.
 *
 * Posición de la pieza en el grid 2x2:
 *   TOP_LEFT     TOP_RIGHT
 *   BOTTOM_LEFT  BOTTOM_RIGHT
 */
class PuzzlePieceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class Position { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    var position: Position = Position.TOP_LEFT
        set(value) { field = value; invalidate() }

    var pieceColor: Int = Color.parseColor("#29B6F6")
        set(value) { field = value; invalidate() }

    var text: String = ""
        set(value) { field = value; invalidate() }

    var letter: String = "A"
        set(value) { field = value; invalidate() }

    // Tamaño de la pestaña respecto al lado (25%)
    private val tabRatio = 0.25f

    private val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 6f
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val paintLetter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        alpha = 180
    }

    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val tab = minOf(w, h) * tabRatio   // radio de la pestaña
        val pad = 10f                       // padding interior para no cortar pestañas

        // Área útil dejando espacio para pestañas exteriores
        val left   = pad
        val top    = pad
        val right  = w - pad
        val bottom = h - pad

        paintFill.color = pieceColor
        buildPath(left, top, right, bottom, tab)

        // Sombra suave
        paintFill.setShadowLayer(8f, 2f, 2f, Color.argb(80, 0, 0, 0))
        setLayerType(LAYER_TYPE_SOFTWARE, paintFill)

        canvas.drawPath(path, paintFill)
        canvas.drawPath(path, paintStroke)

        // Letra (A, B, C, D) en esquina interior
        paintLetter.textSize = minOf(w, h) * 0.18f
        val letterX = when (position) {
            Position.TOP_LEFT, Position.BOTTOM_LEFT -> left + pad * 3
            Position.TOP_RIGHT, Position.BOTTOM_RIGHT -> right - pad * 3
        }
        val letterY = when (position) {
            Position.TOP_LEFT, Position.TOP_RIGHT -> top + pad * 3 + paintLetter.textSize * 0.4f
            Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT -> bottom - pad * 3
        }
        canvas.drawText(letter, letterX, letterY, paintLetter)

        // Texto de la opción centrado en la pieza
        paintText.textSize = minOf(w, h) * 0.13f
        val cx = w / 2f
        val cy = h / 2f + paintText.textSize * 0.4f

        // Si el texto es largo, partirlo en varias líneas
        drawMultilineText(canvas, text, cx, cy, w * 0.55f, paintText)
    }

    /**
     * Construye el Path de la pieza según su posición en el grid 2x2.
     *
     * Reglas de pestañas para encajar perfectamente:
     *   - TOP_LEFT:     tab derecha AFUERA, tab abajo AFUERA
     *   - TOP_RIGHT:    muesca izquierda (recibe tab de TL), tab abajo AFUERA
     *   - BOTTOM_LEFT:  muesca arriba (recibe tab de TL), tab derecha AFUERA
     *   - BOTTOM_RIGHT: muesca izquierda, muesca arriba
     */
    private fun buildPath(l: Float, t: Float, r: Float, b: Float, tab: Float) {
        path.reset()
        val mx = (l + r) / 2f   // centro horizontal
        val my = (t + b) / 2f   // centro vertical

        path.moveTo(l, t)

        // ── Lado superior ──
        when (position) {
            Position.TOP_LEFT, Position.TOP_RIGHT -> {
                // borde recto arriba
                path.lineTo(r, t)
            }
            Position.BOTTOM_LEFT -> {
                // muesca arriba (entra hacia dentro)
                path.lineTo(mx - tab, t)
                path.cubicTo(mx - tab, t - tab, mx + tab, t - tab, mx + tab, t)
                path.lineTo(r, t)
            }
            Position.BOTTOM_RIGHT -> {
                path.lineTo(mx - tab, t)
                path.cubicTo(mx - tab, t - tab, mx + tab, t - tab, mx + tab, t)
                path.lineTo(r, t)
            }
        }

        // ── Lado derecho ──
        when (position) {
            Position.TOP_RIGHT, Position.BOTTOM_RIGHT -> {
                // borde recto derecha
                path.lineTo(r, b)
            }
            Position.TOP_LEFT -> {
                // pestaña derecha (sale hacia fuera)
                path.lineTo(r, my - tab)
                path.cubicTo(r + tab, my - tab, r + tab, my + tab, r, my + tab)
                path.lineTo(r, b)
            }
            Position.BOTTOM_LEFT -> {
                path.lineTo(r, my - tab)
                path.cubicTo(r + tab, my - tab, r + tab, my + tab, r, my + tab)
                path.lineTo(r, b)
            }
        }

        // ── Lado inferior ──
        when (position) {
            Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT -> {
                // borde recto abajo
                path.lineTo(l, b)
            }
            Position.TOP_LEFT -> {
                // pestaña abajo (sale hacia fuera)
                path.lineTo(mx + tab, b)
                path.cubicTo(mx + tab, b + tab, mx - tab, b + tab, mx - tab, b)
                path.lineTo(l, b)
            }
            Position.TOP_RIGHT -> {
                path.lineTo(mx + tab, b)
                path.cubicTo(mx + tab, b + tab, mx - tab, b + tab, mx - tab, b)
                path.lineTo(l, b)
            }
        }

        // ── Lado izquierdo ──
        when (position) {
            Position.TOP_LEFT, Position.BOTTOM_LEFT -> {
                // borde recto izquierda
                path.lineTo(l, t)
            }
            Position.TOP_RIGHT -> {
                // muesca izquierda (entra hacia dentro)
                path.lineTo(l, my + tab)
                path.cubicTo(l - tab, my + tab, l - tab, my - tab, l, my - tab)
                path.lineTo(l, t)
            }
            Position.BOTTOM_RIGHT -> {
                path.lineTo(l, my + tab)
                path.cubicTo(l - tab, my + tab, l - tab, my - tab, l, my - tab)
                path.lineTo(l, t)
            }
        }

        path.close()
    }

    private fun drawMultilineText(
        canvas: Canvas, text: String,
        cx: Float, cy: Float,
        maxWidth: Float, paint: Paint
    ) {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = ""

        for (word in words) {
            val test = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(test) <= maxWidth) {
                current = test
            } else {
                if (current.isNotEmpty()) lines.add(current)
                current = word
            }
        }
        if (current.isNotEmpty()) lines.add(current)

        val lineHeight = paint.textSize * 1.3f
        val totalH = lineHeight * lines.size
        var y = cy - totalH / 2f + lineHeight * 0.5f

        for (line in lines) {
            canvas.drawText(line, cx, y, paint)
            y += lineHeight
        }
    }
}