
package id.forum.core.util.transition

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.transition.TransitionValues
import androidx.transition.Visibility

/**
 * A [androidx.transition.Transition] which animates visibility changes by sliding in/out diagonally
 * from the bottom right edge.
 */
class DiagonalSlide : Visibility() {

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ) = animate(view, sceneRoot, true)

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ) = animate(view, sceneRoot, false)

    private fun animate(
        view: View,
        sceneRoot: ViewGroup,
        appear: Boolean
    ): ObjectAnimator {
        val tX = view.translationX
        val goneTX = (sceneRoot.width - view.left).toFloat()
        view.translationX = goneTX
        val tY = view.translationY
        val goneTY = (sceneRoot.height - view.top).toFloat()
        view.translationY = goneTY
        return if (appear) {
            ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, goneTX, tX),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, goneTY, tY)
            )
        } else {
            ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, tX, goneTX),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, tY, goneTY)
            ).apply {
                doOnEnd {
                    view.visibility = View.GONE
                }
            }
        }
    }
}
