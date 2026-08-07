package ave.mujica.poc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import ave.mujica.poc.pocs.bglogdemo.BgLogDemoActivity
import ave.mujica.poc.pocs.demo.DemoActivity
import ave.mujica.poc.utils.ViewUtils

class MainActivity : AppCompatActivity() {
    private var pocCard: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = ViewUtils.makeLinearLayout(this, "Main").apply {
            setPadding(0, paddingTop, 0, paddingBottom)
        }

        val scrollView = ScrollView(this).apply {
            clipToPadding = false
            ViewUtils.applyPageBackground(this)
            setPadding(0, ViewUtils.dpToPx(this@MainActivity, 8), 0, ViewUtils.dpToPx(this@MainActivity, 16))
            scrollBarStyle = ScrollView.SCROLLBARS_OUTSIDE_OVERLAY
        }

        val contentFrame = FrameLayout(this).apply {
            val horizontalPadding = ViewUtils.dpToPx(this@MainActivity, 20)
            setPadding(horizontalPadding, 0, horizontalPadding, 0)
            addView(
                root,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }

        scrollView.addView(
            contentFrame,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        setContentView(scrollView)

        root.addView(
            ViewUtils.makeInfoCard(
                this,
                "Use this launcher to open individual framework and OEM policy probes. " +
                    "Each page keeps its own action surface on top and an expandable log console at the bottom."
            ),
            1,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = ViewUtils.dpToPx(this@MainActivity, 18)
            }
        )

        pocCard = createPocCard()
        root.addView(
            pocCard,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = ViewUtils.dpToPx(this@MainActivity, 18)
            }
        )

        addPocEntry("Simple Demo", DemoActivity::class.java)
        addPocEntry("Background Log Demo", BgLogDemoActivity::class.java)
    }

    private fun createPocCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = ViewUtils.makeSurfaceBackground(this@MainActivity, 22)
            elevation = ViewUtils.dpToPx(this@MainActivity, 2).toFloat()
            setPadding(
                ViewUtils.dpToPx(this@MainActivity, 18),
                ViewUtils.dpToPx(this@MainActivity, 18),
                ViewUtils.dpToPx(this@MainActivity, 18),
                ViewUtils.dpToPx(this@MainActivity, 18)
            )

            addView(
                ViewUtils.makeCardTitle(this@MainActivity, "Available PoCs"),
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = ViewUtils.dpToPx(this@MainActivity, 6)
                }
            )

            addView(
                ViewUtils.makeSectionBody(
                    this@MainActivity,
                    "Each probe opens a dedicated test surface with inline controls and a persistent log console for raw output."
                ),
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = ViewUtils.dpToPx(this@MainActivity, 14)
                }
            )
        }
    }

    private fun addPocEntry(label: String, activityClass: Class<out Activity>) {
        val card = pocCard ?: return
        card.orientation = LinearLayout.VERTICAL
        card.addView(
            ViewUtils.makeButton(this, label) { startActivity(Intent(this, activityClass)) },
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = ViewUtils.dpToPx(this@MainActivity, 10)
            }
        )
    }
}