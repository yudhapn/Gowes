package id.forum.mediapreviewer

import android.widget.ImageView
import com.igreenwood.loupe.Loupe


object Pref {
   var useSharedElements = true
   var maxZoom = Loupe.DEFAULT_MAX_ZOOM
   var flingAnimationDuration = Loupe.DEFAULT_ANIM_DURATION
   var scaleAnimationDuration = Loupe.DEFAULT_ANIM_DURATION_LONG
   var overScaleAnimationDuration = Loupe.DEFAULT_ANIM_DURATION_LONG
   var overScrollAnimationDuration = Loupe.DEFAULT_ANIM_DURATION
   var dismissAnimationDuration = Loupe.DEFAULT_ANIM_DURATION
   var restoreAnimationDuration = Loupe.DEFAULT_ANIM_DURATION
   var viewDragFriction = Loupe.DEFAULT_VIEW_DRAG_FRICTION
}



fun Loupe.setOnViewTranslateListener(
   onStart: (view: ImageView) -> Unit = {},
   onViewTranslate: (view: ImageView, amount: Float) -> Unit = { _, _ -> },
   onRestore: (view: ImageView) -> Unit = {},
   onDismiss: (view: ImageView) -> Unit = {}
) {
   this.onViewTranslateListener = object : Loupe.OnViewTranslateListener {
      override fun onDismiss(view: ImageView) {
         onDismiss(view)
      }

      override fun onRestore(view: ImageView) {
         onRestore(view)
      }

      override fun onStart(view: ImageView) {
         onStart(view)
      }

      override fun onViewTranslate(view: ImageView, amount: Float) {
         onViewTranslate(view, amount)
      }
   }
}