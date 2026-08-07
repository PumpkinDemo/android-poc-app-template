package ave.mujica.poc.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

object ViewUtils {
    private const val PAGE_BACKGROUND_COLOR = 0xFFF4F7FB.toInt()
    private const val SURFACE_COLOR = 0xFFFFFFFF.toInt()
    private const val SURFACE_STROKE_COLOR = 0xFFD7E1EC.toInt()
    private const val TITLE_COLOR = 0xFF10243A.toInt()
    private const val SUBTITLE_COLOR = 0xFF5E7389.toInt()
    private const val ACCENT_COLOR = 0xFF0F5F9A.toInt()
    private const val ACCENT_DARK_COLOR = 0xFF0A4C7B.toInt()
    private const val ACCENT_PRESSED_COLOR = 0xFF073B61.toInt()
    private const val ACCENT_FOCUSED_COLOR = 0xFF136EAE.toInt()
    private const val INPUT_BACKGROUND_COLOR = 0xFFFBFDFF.toInt()
    private const val INPUT_TEXT_COLOR = 0xFF183046.toInt()
    private const val INPUT_HINT_COLOR = 0xFF73879D.toInt()

    private const val LOG_FONT_ASSET = "fonts/SpaceMono-Regular.ttf"
    private var logTypeface: Typeface? = null

    fun getLogTypeface(context: Context): Typeface {
        logTypeface?.let { return it }
        val loaded = try {
            Typeface.createFromAsset(context.assets, LOG_FONT_ASSET)
        } catch (e: RuntimeException) {
            Log.w("ViewUtils", "Failed to load $LOG_FONT_ASSET, falling back to monospace", e)
            Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }
        logTypeface = loaded
        return loaded
    }

    fun makeLinearLayout(context: Context, title: String): LinearLayout {
        val rootLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            fitsSystemWindows = true
            setBackgroundColor(PAGE_BACKGROUND_COLOR)
            val paddingX = dpToPx(context, 20)
            val paddingY = dpToPx(context, 16)
            setPadding(paddingX, paddingY, paddingX, paddingY)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val titleCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = makeRoundedRect(SURFACE_COLOR, dpToPx(context, 22), dpToPx(context, 1), SURFACE_STROKE_COLOR)
            elevation = dpToPx(context, 2).toFloat()
            setPadding(
                dpToPx(context, 18),
                dpToPx(context, 18),
                dpToPx(context, 18),
                dpToPx(context, 18)
            )
        }

        val eyebrow = TextView(context).apply {
            text = "Android PoC Template"
            textSize = 11f
            setTextColor(ACCENT_COLOR)
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val titleText = TextView(context).apply {
            text = title
            textSize = 24f
            setTextColor(TITLE_COLOR)
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val subtitle = TextView(context).apply {
            text = defaultSubtitleForTitle(title)
            textSize = 13f
            setTextColor(SUBTITLE_COLOR)
            setLineSpacing(0f, 1.12f)
        }

        titleCard.addView(
            eyebrow,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(context, 10)
            }
        )
        titleCard.addView(
            titleText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(context, 6)
            }
        )
        titleCard.addView(subtitle)

        rootLayout.addView(
            titleCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dpToPx(context, 18)
            }
        )

        return rootLayout
    }

    fun makeButton(context: Context, label: String, onClick: View.OnClickListener): Button {
        return Button(context).apply {
            text = label
            setOnClickListener(onClick)
            isAllCaps = false
            setTextColor(Color.WHITE)
            textSize = 14f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            gravity = Gravity.CENTER
            minimumHeight = dpToPx(context, 46)
            setPadding(
                dpToPx(context, 14),
                dpToPx(context, 10),
                dpToPx(context, 14),
                dpToPx(context, 10)
            )
            background = makeButtonBackground(context)
            setOnTouchListener { view, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        view.animate().cancel()
                        view.animate()
                            .alpha(0.84f)
                            .scaleX(0.985f)
                            .scaleY(0.985f)
                            .translationY(dpToPx(context, 1).toFloat())
                            .setDuration(55L)
                            .start()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        view.animate().cancel()
                        view.animate()
                            .alpha(1f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .translationY(0f)
                            .setDuration(120L)
                            .start()
                    }
                }
                false
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun createInputField(context: Context, hint: String, value: String, typeface: Typeface = getLogTypeface(context)): EditText {
        return EditText(context).apply {
            setHint(hint)
            setText(value)
            this.typeface = typeface
            textSize = 13.5f
            setTextColor(INPUT_TEXT_COLOR)
            setHintTextColor(INPUT_HINT_COLOR)
            background = makeRoundedRect(INPUT_BACKGROUND_COLOR, dpToPx(context, 16), dpToPx(context, 1), SURFACE_STROKE_COLOR)
            setPadding(
                dpToPx(context, 14),
                dpToPx(context, 10),
                dpToPx(context, 14),
                dpToPx(context, 10)
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun makeInfoCard(context: Context, text: String): TextView {
        return TextView(context).apply {
            setText(text)
            textSize = 13.5f
            setTextColor(SUBTITLE_COLOR)
            setLineSpacing(0f, 1.12f)
            background = makeRoundedRect(SURFACE_COLOR, dpToPx(context, 18), dpToPx(context, 1), SURFACE_STROKE_COLOR)
            setPadding(
                dpToPx(context, 16),
                dpToPx(context, 14),
                dpToPx(context, 16),
                dpToPx(context, 14)
            )
        }
    }

    fun makeSectionLabel(context: Context, text: String): TextView {
        return TextView(context).apply {
            setText(text)
            textSize = 12f
            setTextColor(ACCENT_COLOR)
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
    }

    fun makeCardTitle(context: Context, text: String): TextView {
        return TextView(context).apply {
            setText(text)
            textSize = 18f
            setTextColor(TITLE_COLOR)
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
    }

    fun makeSectionBody(context: Context, text: String): TextView {
        return TextView(context).apply {
            setText(text)
            textSize = 13.5f
            setTextColor(SUBTITLE_COLOR)
            setLineSpacing(0f, 1.12f)
        }
    }

    fun makeSurfaceBackground(context: Context, radiusDp: Int): GradientDrawable {
        return makeRoundedRect(SURFACE_COLOR, dpToPx(context, radiusDp), dpToPx(context, 1), SURFACE_STROKE_COLOR)
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return kotlin.math.round(dp * density).toInt()
    }

    private fun makeButtonBackground(context: Context): StateListDrawable {
        val radius = dpToPx(context, 16)
        val stroke = dpToPx(context, 1)
        return StateListDrawable().apply {
            addState(
                intArrayOf(android.R.attr.state_pressed),
                makeRoundedRect(ACCENT_PRESSED_COLOR, radius, stroke, ACCENT_PRESSED_COLOR)
            )
            addState(
                intArrayOf(android.R.attr.state_focused),
                makeRoundedRect(ACCENT_FOCUSED_COLOR, radius, stroke, ACCENT_DARK_COLOR)
            )
            addState(
                intArrayOf(),
                makeRoundedRect(ACCENT_COLOR, radius, stroke, ACCENT_DARK_COLOR)
            )
        }
    }

    private fun defaultSubtitleForTitle(title: String): String {
        return if (title == "Main") {
            "Binder probes and OEM policy surfaces collected in a single launcher."
        } else {
            "Interactive probe surface for $title."
        }
    }

    private fun makeRoundedRect(fillColor: Int, radiusPx: Int, strokeWidthPx: Int, strokeColor: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(fillColor)
            cornerRadius = radiusPx.toFloat()
            if (strokeWidthPx > 0) {
                setStroke(strokeWidthPx, strokeColor)
            }
        }
    }
}