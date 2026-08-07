package ave.mujica.poc

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

import ave.mujica.poc.utils.ViewUtils

abstract class BasePocActivity : AppCompatActivity() {

	companion object {
		private const val CONTENT_HORIZONTAL_PADDING_DP = 24
		private const val CONTENT_VERTICAL_PADDING_DP = 16
		private const val CONTENT_ITEM_SPACING_DP = 10
		private const val PAGE_BACKGROUND_COLOR = 0xFFF4F7FB.toInt()

		private const val LOG_SHEET_PEEK_HEIGHT_DP = 164
		private const val LOG_SHEET_EXPANDED_TOP_OFFSET_DP = 48
		private const val LOG_HORIZONTAL_PADDING_DP = 12
		private const val LOG_VERTICAL_PADDING_DP = 12
		private const val LOG_SHEET_CORNER_RADIUS_DP = 22
		private const val LOG_SHEET_ELEVATION_DP = 10
		private const val LOG_HEADER_HORIZONTAL_PADDING_DP = 16
		private const val LOG_HEADER_VERTICAL_PADDING_DP = 10
		private const val LOG_HEADER_BACKGROUND_COLOR = 0xFFDCE6F2.toInt()
		private const val LOG_HEADER_TEXT_COLOR = 0xFF10243A.toInt()
		private const val LOG_META_TEXT_COLOR = 0xFF58708A.toInt()
		private const val LOG_CONSOLE_BACKGROUND_COLOR = 0xFFF8FBFF.toInt()
		private const val LOG_SHEET_BACKGROUND_COLOR = 0xFFEEF4FA.toInt()
		private const val LOG_SHEET_STROKE_COLOR = 0xFFB7C7D8.toInt()
		private const val LOG_HANDLE_COLOR = 0xFF8AA0B8.toInt()
		private const val LOG_DIVIDER_COLOR = 0xFFCBD8E5.toInt()
		private const val LOG_TEXT_COLOR = 0xFF20354C.toInt()
		private const val LOG_CLEAR_TEXT_COLOR = 0xFF0F548C.toInt()
		private const val LOG_CLEAR_BACKGROUND_COLOR = 0xFFE8F2FB.toInt()
		private const val LOG_EMPTY_TEXT = "No logs captured yet."
	}

	protected var logView: TextView? = null
	protected val logBuffer = StringBuilder()
	protected var rootLayout: LinearLayout? = null

	private var logMetaView: TextView? = null
	private var inputSectionCard: View? = null
	private var actionSectionCard: View? = null
	private var inputSectionContent: LinearLayout? = null
	private var actionSectionContent: LinearLayout? = null
	private var contentScrollView: ScrollView? = null
	private var logSheet: LinearLayout? = null
	private var logSheetExpandedTranslationY = 0f
	private var logSheetCollapsedTranslationY = 0f
	private var logSheetDragStartRawY = 0f
	private var logSheetDragStartTranslationY = 0f
	private var logSheetPositioned = false
	private var logHeaderDragging = false
	private var logHeaderMoved = false
	private var logHeaderTouchSlop = 0
	private var logSheetAnimator: ValueAnimator? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		logHeaderTouchSlop = ViewConfiguration.get(this).scaledTouchSlop

		val coordinatorLayout = CoordinatorLayout(this).apply {
			layoutParams = ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT
			)
			setBackgroundColor(PAGE_BACKGROUND_COLOR)
		}

		contentScrollView = ScrollView(this).apply {
			isFillViewport = true
			clipToPadding = false
			setPadding(0, 0, 0, 0)
			scrollBarStyle = ScrollView.SCROLLBARS_OUTSIDE_OVERLAY
			layoutParams = CoordinatorLayout.LayoutParams(
				CoordinatorLayout.LayoutParams.MATCH_PARENT,
				CoordinatorLayout.LayoutParams.MATCH_PARENT
			)
		}

		val root = ViewUtils.makeLinearLayout(this, tag())
		rootLayout = root
		root.apply {
			gravity = Gravity.NO_GRAVITY
			setPadding(
				0,
				ViewUtils.dpToPx(this@BasePocActivity, CONTENT_VERTICAL_PADDING_DP),
				0,
				ViewUtils.dpToPx(this@BasePocActivity, CONTENT_VERTICAL_PADDING_DP)
			)
			clipToPadding = false
		}

		val contentFrame = FrameLayout(this).apply {
			val horizontalPadding = ViewUtils.dpToPx(this@BasePocActivity, CONTENT_HORIZONTAL_PADDING_DP)
			setPadding(horizontalPadding, 0, horizontalPadding, 0)
			addView(
				root,
				FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT
				)
			)
		}
		contentScrollView?.addView(
			contentFrame,
			FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT
			)
		)
		coordinatorLayout.addView(contentScrollView)

		logView = TextView(this).apply {
			textSize = 12.5f
			setTextColor(LOG_TEXT_COLOR)
			typeface = getLogTypeface()
			setLineSpacing(0f, 1.18f)
			setPadding(
				ViewUtils.dpToPx(this@BasePocActivity, LOG_HORIZONTAL_PADDING_DP),
				ViewUtils.dpToPx(this@BasePocActivity, 14),
				ViewUtils.dpToPx(this@BasePocActivity, LOG_HORIZONTAL_PADDING_DP),
				ViewUtils.dpToPx(this@BasePocActivity, LOG_VERTICAL_PADDING_DP)
			)
			setHorizontallyScrolling(true)
			setTextIsSelectable(true)
		}

		val horizontalScrollView = HorizontalScrollView(this).apply {
			isFillViewport = false
			setBackgroundColor(LOG_CONSOLE_BACKGROUND_COLOR)
			addView(
				logView,
				FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT
				)
			)
		}

		val sv = ScrollView(this).apply {
			isFillViewport = true
			setBackgroundColor(LOG_CONSOLE_BACKGROUND_COLOR)
			addView(
				horizontalScrollView,
				FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT
				)
			)
		}
		sv.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
			val logText = logView ?: return@addOnLayoutChangeListener
			val minimumBottomPadding = ViewUtils.dpToPx(this, LOG_VERTICAL_PADDING_DP)
			val bottomPadding = max(minimumBottomPadding, view.height - logText.lineHeight)
			if (logText.paddingBottom != bottomPadding) {
				logText.setPadding(
					logText.paddingLeft,
					logText.paddingTop,
					logText.paddingRight,
					bottomPadding
				)
			}
		}

		val handleBar = View(this)
		val handleDrawable = GradientDrawable().apply {
			setColor(LOG_HANDLE_COLOR)
			cornerRadius = ViewUtils.dpToPx(this@BasePocActivity, 999).toFloat()
		}
		handleBar.background = handleDrawable
		val handleParams = LinearLayout.LayoutParams(
			ViewUtils.dpToPx(this, 42),
			ViewUtils.dpToPx(this, 4)
		).apply {
			gravity = Gravity.CENTER_HORIZONTAL
			topMargin = ViewUtils.dpToPx(this@BasePocActivity, 8)
			bottomMargin = ViewUtils.dpToPx(this@BasePocActivity, 8)
		}

		val titleView = TextView(this).apply {
			text = "Log Console"
			textSize = 14f
			setTextColor(LOG_HEADER_TEXT_COLOR)
			typeface = Typeface.create(getLogTypeface(), Typeface.BOLD)
		}

		logMetaView = TextView(this).apply {
			textSize = 10.5f
			setTextColor(LOG_META_TEXT_COLOR)
			typeface = getLogTypeface()
		}

		val titleColumn = LinearLayout(this).apply {
			orientation = LinearLayout.VERTICAL
			gravity = Gravity.CENTER_VERTICAL
			addView(titleView)
			addView(logMetaView)
		}

		val clearLogView = TextView(this).apply {
			text = "Clear"
			textSize = 12f
			setTextColor(LOG_CLEAR_TEXT_COLOR)
			typeface = Typeface.create(getLogTypeface(), Typeface.BOLD)
			gravity = Gravity.CENTER
			setPadding(
				ViewUtils.dpToPx(this@BasePocActivity, 10),
				ViewUtils.dpToPx(this@BasePocActivity, 6),
				ViewUtils.dpToPx(this@BasePocActivity, 10),
				ViewUtils.dpToPx(this@BasePocActivity, 6)
			)
			background = makeRoundedRect(LOG_CLEAR_BACKGROUND_COLOR, 999, 0, Color.TRANSPARENT)
			setOnClickListener { clearLog() }
		}

		val titleParams = LinearLayout.LayoutParams(
			0,
			LinearLayout.LayoutParams.WRAP_CONTENT,
			1f
		)

		val headerRow = LinearLayout(this).apply {
			orientation = LinearLayout.HORIZONTAL
			gravity = Gravity.CENTER_VERTICAL
			addView(titleColumn, titleParams)
			addView(
				clearLogView,
				LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT
				)
			)
		}

		val headerLayout = LinearLayout(this).apply {
			orientation = LinearLayout.VERTICAL
			setPadding(
				ViewUtils.dpToPx(this@BasePocActivity, LOG_HEADER_HORIZONTAL_PADDING_DP),
				ViewUtils.dpToPx(this@BasePocActivity, LOG_HEADER_VERTICAL_PADDING_DP),
				ViewUtils.dpToPx(this@BasePocActivity, LOG_HEADER_HORIZONTAL_PADDING_DP),
				ViewUtils.dpToPx(this@BasePocActivity, LOG_HEADER_VERTICAL_PADDING_DP)
			)
			background = makeHeaderBackground()
			addView(handleBar, handleParams)
			addView(
				headerRow,
				LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT
				)
			)
		}

		val dividerView = View(this).apply {
			setBackgroundColor(LOG_DIVIDER_COLOR)
		}

		logSheet = LinearLayout(this).apply {
			orientation = LinearLayout.VERTICAL
			background = makeSheetBackground()
			elevation = ViewUtils.dpToPx(this@BasePocActivity, LOG_SHEET_ELEVATION_DP).toFloat()
			addView(
				headerLayout,
				LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT
				)
			)
			addView(
				dividerView,
				LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					ViewUtils.dpToPx(this@BasePocActivity, 1)
				)
			)
			addView(
				sv,
				LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					0,
					1f
				)
			)
			layoutParams = CoordinatorLayout.LayoutParams(
				CoordinatorLayout.LayoutParams.MATCH_PARENT,
				CoordinatorLayout.LayoutParams.MATCH_PARENT
			).apply {
				gravity = Gravity.BOTTOM
			}
		}

		coordinatorLayout.addView(logSheet)
		logSheet?.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
			updateLogSheetBounds()
		}
		coordinatorLayout.post { updateLogSheetBounds() }

		val logHeaderTouchListener = View.OnTouchListener { view, event ->
			handleLogHeaderTouch(view, event)
		}
		handleBar.setOnTouchListener(logHeaderTouchListener)
		titleColumn.setOnTouchListener(logHeaderTouchListener)
		titleView.setOnTouchListener(logHeaderTouchListener)
		logMetaView?.setOnTouchListener(logHeaderTouchListener)

		setContentView(coordinatorLayout)
		renderLogBuffer()
	}

	protected fun log(tag: String, msg: String) {
		Log.d(tag, msg)
		logBuffer.append(msg).append("\n")
		logView?.post { renderLogBuffer() }
	}

	protected fun clearLog() {
		logBuffer.setLength(0)
		renderLogBuffer()
	}

	protected fun getLogTypeface(): Typeface {
		return ViewUtils.getLogTypeface(this)
	}

	protected fun defaultLayoutParams(): LinearLayout.LayoutParams {
		return LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		).apply {
			bottomMargin = ViewUtils.dpToPx(this@BasePocActivity, CONTENT_ITEM_SPACING_DP)
		}
	}

	protected fun addActionButton(label: String, onClick: View.OnClickListener) {
		val root = rootLayout ?: return
		ensureActionSection()
		actionSectionContent?.addView(ViewUtils.makeButton(this, label, onClick), defaultLayoutParams())
		root.invalidate()
	}

	protected fun addInputField(view: View?) {
		if (rootLayout == null || view == null) {
			return
		}
		ensureInputSection()
		inputSectionContent?.addView(view, defaultLayoutParams())
	}

	protected fun toggleLogSheet() {
		val sheet = logSheet ?: return
		val midpoint = (logSheetExpandedTranslationY + logSheetCollapsedTranslationY) / 2f
		if (sheet.translationY <= midpoint) {
			animateLogSheetTo(logSheetCollapsedTranslationY)
			return
		}
		animateLogSheetTo(logSheetExpandedTranslationY)
	}

	private fun renderLogBuffer() {
		val lv = logView ?: return
		val content = if (logBuffer.isEmpty()) LOG_EMPTY_TEXT else logBuffer.toString()
		lv.text = content
		lv.alpha = if (logBuffer.isEmpty()) 0.62f else 1f
		logMetaView?.let { meta ->
			val entries = countLogEntries()
			val suffix = if (entries == 1) "entry" else "entries"
			meta.text = "$entries $suffix  |  Drag title freely"
		}
	}

	private fun countLogEntries(): Int {
		if (logBuffer.isEmpty()) {
			return 0
		}
		var count = 0
		for (char in logBuffer) {
			if (char == '\n') {
				count++
			}
		}
		return count
	}

	private fun makeSheetBackground(): GradientDrawable {
		val radius = ViewUtils.dpToPx(this, LOG_SHEET_CORNER_RADIUS_DP).toFloat()
		return GradientDrawable().apply {
			setColor(LOG_SHEET_BACKGROUND_COLOR)
			cornerRadii = floatArrayOf(
				radius, radius,
				radius, radius,
				0f, 0f,
				0f, 0f
			)
			setStroke(ViewUtils.dpToPx(this@BasePocActivity, 1), LOG_SHEET_STROKE_COLOR)
		}
	}

	private fun makeHeaderBackground(): GradientDrawable {
		val radius = ViewUtils.dpToPx(this, LOG_SHEET_CORNER_RADIUS_DP).toFloat()
		return GradientDrawable().apply {
			setColor(LOG_HEADER_BACKGROUND_COLOR)
			cornerRadii = floatArrayOf(
				radius, radius,
				radius, radius,
				0f, 0f,
				0f, 0f
			)
		}
	}

	private fun handleLogHeaderTouch(view: View, event: MotionEvent): Boolean {
		val sheet = logSheet ?: return false
		when (event.actionMasked) {
			MotionEvent.ACTION_DOWN -> {
				cancelLogSheetAnimation()
				logHeaderDragging = true
				logHeaderMoved = false
				logSheetDragStartRawY = event.rawY
				logSheetDragStartTranslationY = sheet.translationY
				return true
			}

			MotionEvent.ACTION_MOVE -> {
				if (!logHeaderDragging) {
					return false
				}
				val deltaY = event.rawY - logSheetDragStartRawY
				if (abs(deltaY) > logHeaderTouchSlop) {
					logHeaderMoved = true
				}
				setLogSheetTranslation(logSheetDragStartTranslationY + deltaY)
				return true
			}

			MotionEvent.ACTION_UP -> {
				if (!logHeaderDragging) {
					return false
				}
				if (!logHeaderMoved) {
					toggleLogSheet()
				}
				logHeaderDragging = false
				view.performClick()
				return true
			}

			MotionEvent.ACTION_CANCEL -> {
				if (!logHeaderDragging) {
					return false
				}
				logHeaderDragging = false
				return true
			}
		}
		return false
	}

	private fun updateLogSheetBounds() {
		val sheet = logSheet ?: return
		if (sheet.height == 0) {
			return
		}
		logSheetExpandedTranslationY = ViewUtils.dpToPx(this, LOG_SHEET_EXPANDED_TOP_OFFSET_DP).toFloat()
		logSheetCollapsedTranslationY = max(
			logSheetExpandedTranslationY,
			(sheet.height - ViewUtils.dpToPx(this, LOG_SHEET_PEEK_HEIGHT_DP)).toFloat()
		)
		if (!logSheetPositioned) {
			logSheetPositioned = true
			setLogSheetTranslation(logSheetCollapsedTranslationY)
			return
		}
		setLogSheetTranslation(sheet.translationY)
	}

	private fun setLogSheetTranslation(translationY: Float) {
		val sheet = logSheet ?: return
		val clamped = max(logSheetExpandedTranslationY, min(logSheetCollapsedTranslationY, translationY))
		sheet.translationY = clamped
		updateContentBottomInset(clamped)
	}

	private fun animateLogSheetTo(targetTranslationY: Float) {
		val sheet = logSheet ?: return
		cancelLogSheetAnimation()
		val start = sheet.translationY
		val end = max(logSheetExpandedTranslationY, min(logSheetCollapsedTranslationY, targetTranslationY))
		if (abs(start - end) < 1f) {
			setLogSheetTranslation(end)
			return
		}
		logSheetAnimator = ValueAnimator.ofFloat(start, end).apply {
			duration = 220L
			addUpdateListener { animation ->
				setLogSheetTranslation(animation.animatedValue as Float)
			}
			start()
		}
	}

	private fun cancelLogSheetAnimation() {
		logSheetAnimator?.cancel()
		logSheetAnimator = null
	}

	private fun updateContentBottomInset(logSheetTranslationY: Float) {
		val scrollView = contentScrollView ?: return
		val sheet = logSheet ?: return
		val visibleLogHeight = max(0, (sheet.height - logSheetTranslationY).roundToInt())
		val bottomInset = visibleLogHeight + ViewUtils.dpToPx(this, CONTENT_ITEM_SPACING_DP)
		if (scrollView.paddingBottom == bottomInset) {
			return
		}
		scrollView.setPadding(0, scrollView.paddingTop, 0, bottomInset)
	}

	private fun ensureInputSection() {
		if (inputSectionCard != null) {
			return
		}
		val section = createSectionCard(
			"Inputs",
			"Adjust probe parameters before issuing binder calls."
		)
		inputSectionCard = section.card
		inputSectionContent = section.content
		rootLayout?.addView(inputSectionCard, 1, sectionLayoutParams())
	}

	private fun ensureActionSection() {
		if (actionSectionCard != null) {
			return
		}
		val section = createSectionCard(
			"Actions",
			"Run binder probes and inspect the resulting output in the log console."
		)
		actionSectionCard = section.card
		actionSectionContent = section.content
		val root = rootLayout ?: return
		val index = if (inputSectionCard == null) 1 else root.indexOfChild(inputSectionCard) + 1
		root.addView(actionSectionCard, index, sectionLayoutParams())
	}

	private fun sectionLayoutParams(): LinearLayout.LayoutParams {
		return LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		).apply {
			bottomMargin = ViewUtils.dpToPx(this@BasePocActivity, 14)
		}
	}

	private fun createSectionCard(title: String, subtitle: String): SectionViews {
		val card = LinearLayout(this).apply {
			orientation = LinearLayout.VERTICAL
			background = ViewUtils.makeSurfaceBackground(this@BasePocActivity, 22)
			elevation = ViewUtils.dpToPx(this@BasePocActivity, 2).toFloat()
			setPadding(
				ViewUtils.dpToPx(this@BasePocActivity, 16),
				ViewUtils.dpToPx(this@BasePocActivity, 14),
				ViewUtils.dpToPx(this@BasePocActivity, 16),
				ViewUtils.dpToPx(this@BasePocActivity, 14)
			)
		}

		val label = ViewUtils.makeSectionLabel(this, title)
		card.addView(
			label,
			LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			).apply {
				bottomMargin = ViewUtils.dpToPx(this@BasePocActivity, 4)
			}
		)

		val body = ViewUtils.makeSectionBody(this, subtitle).apply {
			textSize = 12.5f
		}
		card.addView(
			body,
			LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			).apply {
				bottomMargin = ViewUtils.dpToPx(this@BasePocActivity, 12)
			}
		)

		val content = LinearLayout(this).apply {
			orientation = LinearLayout.VERTICAL
		}
		card.addView(
			content,
			LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
			)
		)

		return SectionViews(card, content)
	}

	private fun makeRoundedRect(fillColor: Int, radiusDp: Int, strokeWidthDp: Int, strokeColor: Int): GradientDrawable {
		return GradientDrawable().apply {
			setColor(fillColor)
			cornerRadius = ViewUtils.dpToPx(this@BasePocActivity, radiusDp).toFloat()
			if (strokeWidthDp > 0) {
				setStroke(ViewUtils.dpToPx(this@BasePocActivity, strokeWidthDp), strokeColor)
			}
		}
	}

	private data class SectionViews(
		val card: LinearLayout,
		val content: LinearLayout
	)

	abstract fun tag(): String
}